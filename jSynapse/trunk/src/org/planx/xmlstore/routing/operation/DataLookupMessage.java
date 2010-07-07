package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.IOException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;

public class DataLookupMessage extends LookupMessage {
    protected DataLookupMessage() {}

    public DataLookupMessage(Node origin, Identifier lookup) {
        super(origin, lookup);
    }

    public DataLookupMessage(DataInput in) throws IOException {
        super(in);
    }

    public byte code() {
        return MessageFactoryImpl.CODE_DATA_LOOKUP;
    }

    public String toString() {
        return "DataLookupMessage[origin="+origin+",lookup="+lookup+"]";
    }
}
