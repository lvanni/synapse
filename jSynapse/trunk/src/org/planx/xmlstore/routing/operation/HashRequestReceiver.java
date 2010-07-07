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
 * Receives a HashRequestMessage and replies with a HashMessage containing
 * group hashes of mappings that should both be available at the local node
 * and the origin node.
 **/
public class HashRequestReceiver extends OriginReceiver {
    private Map localMap;
    private HashCalculator hasher;

    public HashRequestReceiver(MessageServer server, Node local, Space space, Map localMap) {
        super(server, local, space);
        this.localMap = localMap;
        hasher = new HashCalculator(local, space, localMap);
    }

    public void receive(Message incoming, int comm) throws IOException,
                                               UnknownMessageException {
        super.receive(incoming, comm);
        HashRequestMessage mess = (HashRequestMessage) incoming;
        Node origin = mess.getOrigin();

        long now = System.currentTimeMillis();
        List hashes = hasher.logarithmicHashes(origin, now);

        if (hashes.size() > 0) {
            Message rep = new HashMessage(local, now, hashes);
            server.reply(comm, rep, origin.getInetAddress(), origin.getPort());
        }
    }
}
