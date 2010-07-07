package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import java.util.Map;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.TimestampedValue;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Receives a StoreMessage and stores the mapping if a mapping with the same
 * key and a newer timestamp does not exist. If requested, sends a
 * StoreMessage is sent if this node has a mapping with the same key that is newer.
 **/
public class StoreReceiver extends OriginReceiver {
    private Map localMap;

    public StoreReceiver(MessageServer server, Node local, Space space, Map localMap) {
        super(server, local, space);
        this.localMap = localMap;
    }

    public void receive(Message incoming, int comm) throws IOException {
        super.receive(incoming, comm);

        // Store mapping
        StoreMessage mess = (StoreMessage) incoming;
        Node origin = mess.getOrigin();
        Identifier key = mess.getKey();
        TimestampedValue incmValue = mess.getValue();
        TimestampedValue exstValue = (TimestampedValue) localMap.get(key);

        if ((exstValue == null) || (incmValue.timestamp() > exstValue.timestamp())) {
            // Incoming mapping was newer
            localMap.put(key, incmValue);
        } else if ((exstValue != null) && mess.updateRequested() &&
                   (exstValue.timestamp() > incmValue.timestamp())) {
            // Existing mapping newer and update message requested
            Message updmess = new StoreMessage(local, key, exstValue, false);
            server.send(updmess, origin.getInetAddress(), origin.getPort(), null);
        }
    }
}

