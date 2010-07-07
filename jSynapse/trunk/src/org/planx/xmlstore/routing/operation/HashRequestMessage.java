package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.IOException;
import org.planx.xmlstore.routing.Node;

public class HashRequestMessage extends OriginMessage {
    protected HashRequestMessage() {}

    public HashRequestMessage(Node origin) {
        super(origin);
    }

    public HashRequestMessage(DataInput in) throws IOException {
        super(in);
    }

    public byte code() {
        return MessageFactoryImpl.CODE_HASH_REQUEST;
    }

    public String toString() {
        return "HashRequestMessage[origin="+origin+"]";
    }
}
