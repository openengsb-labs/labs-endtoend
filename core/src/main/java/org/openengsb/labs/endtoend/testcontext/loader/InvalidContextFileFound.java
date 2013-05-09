package org.openengsb.labs.endtoend.testcontext.loader;

public class InvalidContextFileFound extends Exception {
    private static final long serialVersionUID = 8398547731287608886L;

    public InvalidContextFileFound(String filename, Throwable e) {
        super("Invalid context file found: " + filename, e);
    }
}
