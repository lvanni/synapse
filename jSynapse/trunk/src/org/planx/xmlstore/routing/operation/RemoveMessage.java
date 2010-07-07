package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;

public class RemoveMessage extends OriginMessage {
    protected Identifier key;

    protected RemoveMessage() {}

    public RemoveMessage(Node origin, Identifier key) {
        super(origin);
        this.key = key;
    }

    public RemoveMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        key = new Identifier(in);
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        key.toStream(out);
    }

    public Identifier getKey() {
        return key;
    }

    public byte code() {
        return MessageFactoryImpl.CODE_REMOVE;
    }

    public String toString() {
        return "RemoveMessage[origin="+origin+",key="+key+"]";
    }
}
