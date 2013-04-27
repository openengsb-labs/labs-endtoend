package org.openengsb.labs.testing.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Shell {
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    void execute(String command);

    String execute(String command, Long timeout, TimeUnit timeUnit) throws TimeoutException;
}