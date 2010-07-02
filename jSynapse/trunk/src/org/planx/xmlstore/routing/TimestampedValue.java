package org.planx.xmlstore.routing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.planx.xmlstore.routing.messaging.Streamable;

/**
 * A wrapper class that adds a timestamp to any serializable object.
 * The object is internally represented by a byte array and can both
 * be returned as a byte array and as an object.
 **/
public class TimestampedValue implements Streamable, Serializable {
    private byte[] value;
    private long timestamp;

    public TimestampedValue(Serializable obj, long timestamp) throws IOException {
        this.timestamp = timestamp;

        // Converts obj to a byte array
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(obj);
        oout.close();
        value = bout.toByteArray();
    }

    public TimestampedValue(DataInput in) throws IOException {
        fromStream(in);
    }

    public void fromStream(DataInput in) throws IOException {
        timestamp = in.readLong();
        // Read byte array
        value = new byte[in.readInt()];
        in.readFully(value);
    }

    public void toStream(DataOutput out) throws IOException {
        out.writeLong(timestamp);
        // Write byte array to stream
        out.writeInt(value.length);
        out.write(value);
    }

    public long timestamp() {
        return timestamp;
    }

    /**
     * Returns the value as a byte array.
     **/
    public byte[] getByteArray() {
        return value;
    }

    /**
     * Returns the value as an Object.
     **/
    public Serializable getObject() throws IOException {
        try {
            // Create object from byte array
            ByteArrayInputStream bin = new ByteArrayInputStream(value);
            ObjectInputStream oin = new ObjectInputStream(bin);
            Serializable o = (Serializable) oin.readObject();
            oin.close();
            return o;
        } catch (ClassNotFoundException e) {
            throw new IOException(e.toString());
        }
    }

    public String toString() {
        return "{timestamp="+timestamp+",value="+value+"}";
    }
}
