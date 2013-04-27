package org.openengsb.labs.testing.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.karaf.shell.SSHShell;

public interface Karaf {
    void start(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    void shutdown(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    Shell getShell();

    SSHShell login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException;
}
