package org.planx.xmlstore.routing.operation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.planx.xmlstore.routing.Node;

public class NodeReplyMessage extends OriginMessage {
    protected List nodes;

    protected NodeReplyMessage() {}

    public NodeReplyMessage(Node origin, List nodes) {
        super(origin);
        this.nodes = nodes;
    }

    public NodeReplyMessage(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        super.fromStream(in);
        int len = in.readByte();
        nodes = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            nodes.add(new Node(in));
        }
    }

    public void toStream(DataOutput out) throws IOException {
        super.toStream(out);
        int len = nodes.size();
        if (len > 255) {
            throw new IndexOutOfBoundsException("Too many nodes in list: "+nodes.size());
        }
        out.writeByte((byte) len);
        for (int i = 0; i < len; i++) {
            ((Node) nodes.get(i)).toStream(out);
        }
    }

    public byte code() {
        return MessageFactoryImpl.CODE_NODE_REPLY;
    }

    public List getNodes() {
        return nodes;
    }

    public String toString() {
        return "NodeReplyMessage[origin="+origin+",nodes="+nodes+"]";
    }
}

