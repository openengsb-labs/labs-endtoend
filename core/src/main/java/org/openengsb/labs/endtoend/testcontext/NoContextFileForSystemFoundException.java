package org.openengsb.labs.endtoend.testcontext;

public class NoContextFileForSystemFoundException extends Exception {

    private static final long serialVersionUID = -9198474789188790868L;

    public NoContextFileForSystemFoundException(String osName, String osArch) {
        super("No context file for system found (" + osName + ", " + osArch + ")");
    }

}
