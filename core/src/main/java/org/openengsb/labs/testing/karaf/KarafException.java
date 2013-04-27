package org.openengsb.labs.testing.karaf;

public class KarafException extends Exception {
    private static final long serialVersionUID = 8375407232947632347L;

    public KarafException() {
    }

    public KarafException(Exception e) {
        super(e);
    }
}
