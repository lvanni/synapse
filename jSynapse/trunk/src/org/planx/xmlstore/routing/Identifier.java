package org.planx.xmlstore.routing;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;
import org.planx.xmlstore.routing.messaging.Streamable;

/**
 * An identifier consists of at most {@link #IDSIZE} bit and it is
 * always positive. Trying to construct an Identifer outside these boundaries
 * will throw an IndexOutOfBoundsException.
 **/
public class Identifier implements Streamable, Serializable {
    private final static Random random = new Random();
    private BigInteger bigint;

    /**
     * Number of bits in an ID. Must be a multiplum of 8.
     * Should be <code>final</code> but test program changes this value to
     * simplify the tests.
     **/
    public static int IDSIZE = 128;

    /**
     * Constructs an Identifier with value 0.
     **/
    public Identifier() {
        bigint = BigInteger.ZERO;
    }

    /**
     * Constructs an Identifier from the specified byte array which is
     * interpreted as a positive integer in big-endian order (most significant
     * byte is in the zeroth element)
     **/
    public Identifier(byte[] val) {
        bigint = new BigInteger(1, val);
        checkBounds();
    }

    /**
     * Constructs an Identifier from the specified BigInteger.
     **/
    public Identifier(BigInteger val) {
        bigint = val;
        checkBounds();
    }

    /**
     * Constructs an Identifier by reading IDSIZE/8 bytes from the specified
     * DataInput.
     **/
    public Identifier(DataInput in) throws IOException {
        fromStream(in);
    }

    /**
     * Returns a random Identifer.
     **/
    public static Identifier randomIdentifier() {
        Identifier id = new Identifier();
        id.bigint = new BigInteger(IDSIZE, random);
        return id;
    }

    /**
     * Safeguard to help detect errors.
     **/
    private void checkBounds() {
        if ((bigint.signum() < 0) || (bigint.bitLength() > IDSIZE)) {
            throw new IndexOutOfBoundsException("Value out of bounds: "
                  +bigint.toString()+", bitLength="+bigint.bitLength());
        }
    }

    public void fromStream(DataInput in) throws IOException {
        byte[] a = new byte[IDSIZE/8];
        in.readFully(a);
        bigint = new BigInteger(1, a);
        checkBounds();
    }

    public void toStream(DataOutput out) throws IOException {
        out.write(toByteArray());
    }

    /**
     * Returns the value of the identifier in a byte array of size
     * <code>IDSIZE/8</code> in big-endian order (most significant byte
     * is in the zeroth element).
     *
     * @throws IndexOutOfBoundsException if the identifier is to large to fit
     *                                   in the array
     **/
    public byte[] toByteArray() {
        int len = IDSIZE/8;
        byte[] a = bigint.toByteArray();
        byte[] b = new byte[len];
        if (a.length == len+1) {
            // strip sign bit which is in the first byte
            System.arraycopy(a, 1, b, len-a.length+1, a.length-1);
        } else if (a.length <= len) {
            System.arraycopy(a, 0, b, len-a.length, a.length);
        } else {
            throw new IndexOutOfBoundsException("length="+(a.length-1)+
                                                ", required="+len);
        }
        return b;
    }

    public BigInteger value() {
        return bigint;
    }

    public boolean equals(Object o) {
        if (o instanceof Identifier) {
            return bigint.equals(((Identifier) o).bigint);
        }
        return false;
    }

    public int hashCode() {
        return bigint.hashCode();
    }

    public String toString() {
        return bigint.toString();
    }

    /**
     * Returns a binary string representation of this identifier
     * with <code>IDSIZE</code> bits.
     **/
    public String toBinary() {
        return toBinary(bigint);
    }

    /**
     * Returns a binary string representation of the specified BigInteger
     * with <code>IDSIZE</code> bits.
     **/
    public static String toBinary(BigInteger val) {
        String s = val.toString(2);
        StringBuffer sb = new StringBuffer(IDSIZE);
        for (int i = 0; i < (IDSIZE-s.length()); i++) sb.append('0');
        sb.append(s);
        return sb.toString();
    }
}
