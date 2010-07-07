package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.TimestampedValue;

/**
 * Message representing a mapping from a key to a value.
 * If <code>upd</code> is <code>true</code> a StoreMessage with the same key is
 * expected as reply if the receiving node has a newer version of the mapping.
 **/
public class StoreMessage extends DataMessage {
    protected boolean upd;

    protected StoreMessage() {}

    public StoreMessage(Node origin, Identifier key, TimestampedValue value, boolean upd) {
        super(origin, key, value);
        this.upd = upd;
    }

    public StoreMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        upd = in.readBoolean();
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        out.writeBoolean(upd);
    }

    public boolean updateRequested() {
        return upd;
    }

    public byte code() {
        return MessageFactoryImpl.CODE_STORE;
    }

    public String toString() {
        return "StoreMessage[origin="+origin+",key="+key+",value="+value+
               ",upd="+upd+"]";
    }
}
