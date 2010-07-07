package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.IOException;
import java.util.Map;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.Space;
import org.planx.xmlstore.routing.messaging.*;

/**
 * Essentially this class translates numbers into classes. Given a message code
 * it can create a Message object or a Receiver.
 **/
public class MessageFactoryImpl implements MessageFactory {
    public static final byte CODE_CONNECT       = 0x01;
    public static final byte CODE_ACKNOWLEDGE   = 0x02;
    public static final byte CODE_NODE_LOOKUP   = 0x03;
    public static final byte CODE_NODE_REPLY    = 0x04;
    public static final byte CODE_DATA_LOOKUP   = 0x05;
    public static final byte CODE_DATA          = 0x06;
    public static final byte CODE_STORE         = 0x07;
    public static final byte CODE_STORE_REQUEST = 0x08;
    public static final byte CODE_HASH          = 0x09;
    public static final byte CODE_HASH_REQUEST  = 0x0A;
    public static final byte CODE_REMOVE        = 0x0B;

    private Map localMap;
    private Node local;
    private Space space;

    public MessageFactoryImpl(Map localMap, Node local, Space space) {
        this.localMap = localMap;
        this.local = local;
        this.space = space;
    }

    public Message createMessage(byte code, DataInput in)
                   throws IOException, UnknownMessageException {
        switch (code) {
        case CODE_CONNECT:
            return new ConnectMessage(in);
        case CODE_ACKNOWLEDGE:
            return new AcknowledgeMessage(in);
        case CODE_NODE_LOOKUP:
            return new NodeLookupMessage(in);
        case CODE_NODE_REPLY:
            return new NodeReplyMessage(in);
        case CODE_DATA_LOOKUP:
            return new DataLookupMessage(in);
        case CODE_DATA:
            return new DataMessage(in);
        case CODE_STORE:
            return new StoreMessage(in);
        case CODE_STORE_REQUEST:
            return new StoreRequestMessage(in);
        case CODE_HASH:
            return new HashMessage(in);
        case CODE_HASH_REQUEST:
            return new HashRequestMessage(in);
        case CODE_REMOVE:
            return new RemoveMessage(in);
        default:
            throw new UnknownMessageException("Unknown message code: "+code);
        }
    }

    public Receiver createReceiver(byte code, MessageServer server) {
        switch (code) {
        case CODE_CONNECT:
            return new ConnectReceiver(server, local, space);
        case CODE_NODE_LOOKUP:
            return new NodeLookupReceiver(server, local, space);
        case CODE_DATA_LOOKUP:
            return new DataLookupReceiver(server, local, space, localMap);
        case CODE_STORE:
            return new StoreReceiver(server, local, space, localMap);
        case CODE_STORE_REQUEST:
            return new StoreRequestReceiver(server, local, space, localMap);
        case CODE_HASH:
            return new HashReceiver(server, local, space, localMap);
        case CODE_HASH_REQUEST:
            return new HashRequestReceiver(server, local, space, localMap);
        case CODE_REMOVE:
            return new RemoveReceiver(server, local, space, localMap);
        default:
            return null;
        }
    }
}
