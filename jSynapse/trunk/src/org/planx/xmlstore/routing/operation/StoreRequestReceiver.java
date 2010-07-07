package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.planx.xmlstore.routing.HashCalculator;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.TimestampedEntry;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Receives an incoming StoreRequestMessage and issues StoreMessages for
 * mappings in the requested interval that should be available at the
 * origin node.
 **/
public class StoreRequestReceiver extends OriginReceiver {
    private Map localMap;
    private HashCalculator hasher;

    public StoreRequestReceiver(MessageServer server, Node local, Space space, Map localMap) {
        super(server, local, space);
        this.localMap = localMap;
        hasher = new HashCalculator(local, space, localMap);
    }

    public void receive(Message incoming, int comm) throws IOException {
        super.receive(incoming, comm);
        StoreRequestMessage mess = (StoreRequestMessage) incoming;
        Node origin = mess.getOrigin();

        List mappings = hasher.mappingsBetween(origin, mess.getBegin(), mess.getEnd());
        for (int i=0,max=mappings.size(); i<max; i++) {
            TimestampedEntry entry = (TimestampedEntry) mappings.get(i);
            Message rep = new StoreMessage(local,entry.getKey(),entry.getValue(),true);
            server.send(rep, origin.getInetAddress(), origin.getPort(), null);
        }
    }
}
