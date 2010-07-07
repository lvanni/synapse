package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;

public abstract class LookupMessage extends OriginMessage {
    protected Identifier lookup;

    protected LookupMessage() {}

    public LookupMessage(Node origin, Identifier lookup) {
        super(origin);
        this.lookup = lookup;
    }

    public LookupMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        lookup = new Identifier(in);
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        lookup.toStream(out);
    }

    public Identifier getLookupId() {
        return lookup;
    }

    public abstract byte code();
}
