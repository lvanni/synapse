package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.planx.xmlstore.routing.HashCalculator;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Receives a HashMessage and compares it to hashes generated locally.
 * Any hash for an interval not matching will result in a StoreRequestMessage
 * for that interval.
 **/
public class HashReceiver extends OriginReceiver {
    private Map localMap;
    private HashCalculator hasher;

    public HashReceiver(MessageServer server, Node local, Space space, Map localMap) {
        super(server, local, space);
        this.localMap = localMap;
        hasher = new HashCalculator(local, space, localMap);
    }

    public void receive(Message incoming, int comm) throws IOException,
                                               UnknownMessageException {
        super.receive(incoming, comm);
        HashMessage mess = (HashMessage) incoming;
        Node origin = mess.getOrigin();
        List inhashes = mess.getHashes();
        List hashes = hasher.logarithmicHashes(origin, mess.getBaseTime());

        long interval = HashCalculator.UNIT_INTERVAL;
        long previous = Long.MAX_VALUE;
        long current = mess.getBaseTime();
        for (int i=0,max1=hashes.size(),max2=inhashes.size(); i<max1 && i<max2; i++) {
            byte[] hash = (byte[]) hashes.get(i);
            byte[] inhash = (byte[]) inhashes.get(i);
            if (!Arrays.equals(inhash, hash)) {
                Message req = new StoreRequestMessage(local, current, previous);
                server.send(req, origin.getInetAddress(), origin.getPort(), null);
            }
            previous = current;
            current -= interval;
            interval *= 2;
        }

        if (inhashes.size() > hashes.size()) {
            Message req = new StoreRequestMessage(local, Long.MIN_VALUE, previous);
            server.send(req, origin.getInetAddress(), origin.getPort(), null);
        }

    }
}
