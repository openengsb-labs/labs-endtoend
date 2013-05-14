package org.openengsb.labs.endtoend.karaf;

import java.util.concurrent.TimeoutException;

public class CommandTimeoutException extends Exception {
    private static final long serialVersionUID = 8375407232947632347L;

    public CommandTimeoutException(String command, TimeoutException e) {
        super("Command timed out: " + command, e);
    }
}
