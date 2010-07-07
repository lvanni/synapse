package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.IOException;
import org.planx.xmlstore.routing.Node;

public class ConnectMessage extends OriginMessage {
    protected ConnectMessage() {}

    public ConnectMessage(Node origin) {
        super(origin);
    }

    public ConnectMessage(DataInput in) throws IOException {
        super(in);
    }

    public byte code() {
        return MessageFactoryImpl.CODE_CONNECT;
    }

    public String toString() {
        return "ConnectMessage[origin="+origin+"]";
    }
}
