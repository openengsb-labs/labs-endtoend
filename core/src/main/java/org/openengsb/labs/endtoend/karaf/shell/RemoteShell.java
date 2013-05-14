package org.openengsb.labs.endtoend.karaf.shell;

import java.util.concurrent.TimeUnit;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;

public interface RemoteShell extends Shell {
    /**
     * Logout and stop client.
     */
    void logout(Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;

}
