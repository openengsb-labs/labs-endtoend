package org.openengsb.labs.endtoend.util;

public class BinaryKey<Key1, Key2> {
    private final Key1 key1;
    private final Key2 key2;

    public BinaryKey(Key1 key1, Key2 key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key1 == null) ? 0 : key1.hashCode());
        result = prime * result + ((key2 == null) ? 0 : key2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BinaryKey other = (BinaryKey) obj;
        if (key1 == null) {
            if (other.key1 != null)
                return false;
        } else if (!key1.equals(other.key1))
            return false;
        if (key2 == null) {
            if (other.key2 != null)
                return false;
        } else if (!key2.equals(other.key2))
            return false;
        return true;
    }
}
