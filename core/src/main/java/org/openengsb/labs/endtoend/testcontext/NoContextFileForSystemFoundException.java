package org.openengsb.labs.endtoend.testcontext;

public class NoContextFileForSystemFoundException extends Exception {

    public NoContextFileForSystemFoundException(String osName, String osArch) {
        super("No context file for system found (" + osName + ", " + osArch + ")");
    }

}
