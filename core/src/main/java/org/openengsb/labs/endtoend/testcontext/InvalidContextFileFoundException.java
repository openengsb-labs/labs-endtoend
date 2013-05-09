package org.openengsb.labs.endtoend.testcontext;

public class InvalidContextFileFoundException extends Exception {
    private static final long serialVersionUID = 8398547731287608886L;

    public InvalidContextFileFoundException(String filename) {
        super("Invalid context file found: " + filename);
    }
}
