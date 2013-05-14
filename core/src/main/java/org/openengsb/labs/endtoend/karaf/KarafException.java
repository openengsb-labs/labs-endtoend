package org.openengsb.labs.endtoend.karaf;

public class KarafException extends RuntimeException {
    private static final long serialVersionUID = 8375407232947632347L;

    public KarafException(String msg, Exception e) {
        super(msg, e);
    }
}
