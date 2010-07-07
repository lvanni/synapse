package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.IOException;
import org.planx.xmlstore.routing.Node;

public class AcknowledgeMessage extends OriginMessage {
    protected AcknowledgeMessage() {}

    public AcknowledgeMessage(Node origin) {
        super(origin);
    }

    public AcknowledgeMessage(DataInput in) throws IOException {
        super(in);
    }

    public byte code() {
        return MessageFactoryImpl.CODE_ACKNOWLEDGE;
    }

    public String toString() {
        return "AcknowledgeMessage[origin="+origin+"]";
    }
}
