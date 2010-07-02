package org.planx.xmlstore.routing;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Calculates group hashes for mappings.
 **/
public class HashCalculator {
    public static final int HASH_LENGTH = 16;
    public static final long UNIT_INTERVAL = 3600*1000;

    private Node local;
    private Space space;
    private Map localMap;

    public HashCalculator(Node local, Space space, Map localMap) {
        this.local = local;
        this.space = space;
        this.localMap = localMap;
    }

    /**
     * Calculates bundled hashes of the set of keys which is the
     * intersection of keys that both the local node and the specified node
     * are responsible for. The keys are bundled in bundles of exponentially
     * increasing size in the time difference from <code>time</code>, so that
     * the older the timestamp the larger the bundle. Keys with timestamp
     * greater then <code>time</code> are grouped in one large bundle.
     * The returned list will contain <i>O(log N)</i> hashes, where <i>N</i>
     * is the number of mappings.
     *
     * @return List of hashes as byte arrays
     **/
    public List logarithmicHashes(Node node, long time) {
        List mappings = mappingsBetween(node, Long.MIN_VALUE, Long.MAX_VALUE);
        List hashes = new ArrayList();

        // Make larger and larger bundles as we go back in time
        long interval = UNIT_INTERVAL;
        while (mappings.size() > 0) {
            List bundle = tailFrom(mappings, time);
            hashes.add(calculateHash(bundle));
            time -= interval;
            interval *= 2;
        }
        return hashes;
    }

    /**
     * Removes TimestampedEntry objects from the end of <code>mappings</code> until
     * a mapping is encountered with timestamp less than <code>time</code>. The
     * mappings removed are returned in a List in the order that they were removed.
     * That is, the reverse order of the ordering in <code>mappings</code>.
     **/
    private List tailFrom(List mappings, long time) {
        List tail = new ArrayList();
        while (mappings.size() > 0) {
            TimestampedEntry entry = (TimestampedEntry) mappings.get(mappings.size()-1);
            if (entry.getValue().timestamp() >= time) {
                tail.add(entry);
                mappings.remove(mappings.size()-1);
            } else break;
        }
        return tail;
    }

    /**
     * Returns the subset of mappings in the local map with timestamps between
     * <code>begin</code> (including) and <code>end</code> (not including)
     * that the specified node is also responsible for. That is, the specified
     * node and the local node is one of the <i>K</i> closest nodes to each key.
     * The mappings are returned as {@link TimestampedEntry} objects in
     * a List sorted by their timestamp in ascending order.
     **/
    public List mappingsBetween(Node node, long begin, long end) {
        List mappings = new ArrayList();
        Iterator it = localMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Identifier key = (Identifier) entry.getKey();
            List closest = space.getClosestNodes(key);
            if (closest.contains(node) && closest.contains(local)) {
                TimestampedValue value = (TimestampedValue) entry.getValue();
                if ((begin <= value.timestamp()) && (value.timestamp() < end)) {
                    mappings.add(new TimestampedEntry(key, value));
                }
            }
        }
        Collections.sort(mappings, TimestampedEntry.TIMESTAMP_COMPARATOR);
        return mappings;
    }

    /**
     * Calculates a unique hash of the keys in the specified bundle.
     * The bundle is a list of TimestampedEntry objects. To get consistent hashing
     * the bundle should be ordered in a deterministic way. This is that case for
     * lists returned by {@link #mappingsBetween}.
     **/
    public byte[] calculateHash(List bundle) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Actually an assertion
            if (md.getDigestLength() != HASH_LENGTH) {
                throw new RuntimeException("Unexpected hash length "+md.getDigestLength()+
                                           " - expected "+HASH_LENGTH);
            }
            for (int i=0,max=bundle.size(); i<max; i++) {
                TimestampedEntry entry = (TimestampedEntry) bundle.get(i);
                md.update(entry.getKey().toByteArray());
                md.update(entry.getValue().getByteArray());
            }
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
