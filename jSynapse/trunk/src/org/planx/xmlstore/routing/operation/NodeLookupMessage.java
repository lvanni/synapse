package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.IOException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;

public class NodeLookupMessage extends LookupMessage {
    protected NodeLookupMessage() {}

    public NodeLookupMessage(Node origin, Identifier lookup) {
        super(origin, lookup);
    }

    public NodeLookupMessage(DataInput in) throws IOException {
        super(in);
    }

    public byte code() {
        return MessageFactoryImpl.CODE_NODE_LOOKUP;
    }

    public String toString() {
        return "NodeLookupMessage[origin="+origin+",lookup="+lookup+"]";
    }
}
