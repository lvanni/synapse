package org.planx.xmlstore.routing;

import java.util.Comparator;

/**
 * A timestamped mapping consisting of a key and value.
 **/
public class TimestampedEntry {
    static final TimestampComparator TIMESTAMP_COMPARATOR = new TimestampComparator();

    private final Identifier key;
    private final TimestampedValue value;

    public TimestampedEntry(Identifier key, TimestampedValue value) {
        this.key = key;
        this.value = value;
    }

    public Identifier getKey() {
        return key;
    }

    public TimestampedValue getValue() {
        return value;
    }

    public String toString() {
        return "{key="+key+",value="+value+"}";
    }

    static class TimestampComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return (int) (longValue(o1) - longValue(o2));
        }

        private long longValue(Object o) {
            if (o instanceof TimestampedEntry) {
                return ((TimestampedEntry) o).value.timestamp();
            } else if (o instanceof Long) {
                return ((Long) o).longValue();
            } else {
                throw new ClassCastException("Cannot compare to"+o.getClass());
            }
        }
    }
}
