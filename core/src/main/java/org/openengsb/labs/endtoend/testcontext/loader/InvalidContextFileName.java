package org.openengsb.labs.endtoend.testcontext.loader;

public class InvalidContextFileName extends Exception {

    private static final long serialVersionUID = -2863464068538096093L;

    public InvalidContextFileName(String filename) {
        super("Invalid filename: " + filename);
    }
}
