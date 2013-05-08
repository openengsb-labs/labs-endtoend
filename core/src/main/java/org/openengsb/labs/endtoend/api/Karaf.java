package org.openengsb.labs.endtoend.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Karaf {
    void start(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    void shutdown(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    Shell getShell();

    RemoteShell login(String user, String pass, Long timeout, TimeUnit timeUnit) throws TimeoutException;
}
