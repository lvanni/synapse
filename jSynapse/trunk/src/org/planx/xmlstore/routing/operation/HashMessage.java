package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.planx.xmlstore.routing.HashCalculator;
import org.planx.xmlstore.routing.Node;

public class HashMessage extends OriginMessage {
    protected long baseTime;
    protected List hashes;

    protected HashMessage() {}

    public HashMessage(Node origin, long baseTime, List hashes) {
        super(origin);
        this.baseTime = baseTime;
        this.hashes = hashes;
    }

    public HashMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        baseTime = in.readLong();
        int len = in.readInt();
        hashes = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            byte[] a = new byte[HashCalculator.HASH_LENGTH];
            in.readFully(a);
            hashes.add(a);
        }
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        out.writeLong(baseTime);
        int len = hashes.size();
        out.writeInt(len);
        for (int i = 0; i < len; i++) out.write((byte[]) hashes.get(i));
    }

    public long getBaseTime() {
        return baseTime;
    }

    public List getHashes() {
        return hashes;
    }

    public byte code() {
        return MessageFactoryImpl.CODE_HASH;
    }

    public String toString() {
        return "HashMessage[origin="+origin+",baseTime="+baseTime+",hashes="+hashes+"]";
    }
}
