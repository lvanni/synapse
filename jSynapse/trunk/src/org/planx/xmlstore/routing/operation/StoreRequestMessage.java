package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.planx.xmlstore.routing.Node;

public class StoreRequestMessage extends OriginMessage {
    protected long begin;
    protected long end;

    protected StoreRequestMessage() {}

    public StoreRequestMessage(Node origin, long begin, long end) {
        super(origin);
        this.begin = begin;
        this.end = end;
    }

    public StoreRequestMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        begin = in.readLong();
        end = in.readLong();
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        out.writeLong(begin);
        out.writeLong(end);
    }

    public long getBegin() {
        return begin;
    }

    public long getEnd() {
        return end;
    }

    public byte code() {
        return MessageFactoryImpl.CODE_STORE_REQUEST;
    }

    public String toString() {
        return "StoreRequestMessage[origin="+origin+",begin="+begin+",end="+end+"]";
    }
}
