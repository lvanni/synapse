package org.planx.xmlstore.routing.operation;

import java.io.IOException;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Receives a ConnectMessage and sends an AcknowledgeMessage as reply.
 **/
public class ConnectReceiver extends OriginReceiver {
    public ConnectReceiver(MessageServer server, Node local, Space space) {
        super(server, local, space);
    }

    /**
     * Responds to a ConnectMessage by sending a ConnectReplyMessage.
     **/
    public void receive(Message incoming, int comm) throws IOException {
        super.receive(incoming, comm);
        ConnectMessage mess = (ConnectMessage) incoming;
        Node origin = mess.getOrigin();
        Message reply = new AcknowledgeMessage(local);
        server.reply(comm, reply, origin.getInetAddress(), origin.getPort());
    }
}
