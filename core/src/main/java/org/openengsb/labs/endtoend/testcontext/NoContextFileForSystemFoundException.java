package org.openengsb.labs.endtoend.testcontext;

import org.openengsb.labs.endtoend.util.Arch;
import org.openengsb.labs.endtoend.util.OS;

public class NoContextFileForSystemFoundException extends Exception {

    private static final long serialVersionUID = -9198474789188790868L;

    public NoContextFileForSystemFoundException(OS osName, Arch osArch) {
        super("No context file for system found (" + osName + ", " + osArch + ")");
    }

}
