package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.planx.xmlstore.routing.Identifier;
import org.planx.xmlstore.routing.Node;
import org.planx.xmlstore.routing.TimestampedValue;

public class DataMessage extends OriginMessage {
    protected Identifier key;
    protected TimestampedValue value;

    protected DataMessage() {}

    public DataMessage(Node origin, Identifier key, TimestampedValue value) {
        super(origin);
        this.key = key;
        this.value = value;
    }

    public DataMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        key = new Identifier(in);
        value = new TimestampedValue(in);
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        key.toStream(out);
        value.toStream(out);
    }

    public Identifier getKey() {
        return key;
    }

    public TimestampedValue getValue() {
        return value;
    }

    public byte code() {
        return MessageFactoryImpl.CODE_DATA;
    }

    public String toString() {
        return "DataMessage[origin="+origin+",key="+key+",value="+value+"]";
    }
}
