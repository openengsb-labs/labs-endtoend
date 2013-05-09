package org.openengsb.labs.endtoend.karaf;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.shell.RemoteShell;
import org.openengsb.labs.endtoend.karaf.shell.Shell;

public interface Karaf {
    void start(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    void shutdown(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    void kill();

    Shell getShell();

    RemoteShell login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException;
}
