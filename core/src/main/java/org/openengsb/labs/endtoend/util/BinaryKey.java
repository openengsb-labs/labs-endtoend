package org.openengsb.labs.endtoend.util;

import java.util.Objects;

public class BinaryKey<Key1, Key2> {
    private final Key1 key1;
    private final Key2 key2;

    public BinaryKey(Key1 key1, Key2 key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public int hashCode() {
    	return Objects.hash(key1, key2);
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof BinaryKey) {
    		return Objects.equals(key1, ((BinaryKey<?, ?>) obj).key1) && Objects.equals(key2, ((BinaryKey<?, ?>) obj).key2);
    	}
    	return false;
    }
}
