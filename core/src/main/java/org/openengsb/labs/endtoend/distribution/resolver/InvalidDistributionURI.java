package org.openengsb.labs.endtoend.distribution.resolver;

public class InvalidDistributionURI extends Exception {
    private static final long serialVersionUID = -2581687069670420307L;

    public InvalidDistributionURI(String uri) {
        super("Invalid Distribution URI: " + uri);
    }
}
