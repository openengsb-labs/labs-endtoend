package org.openengsb.labs.endtoend.karaf.shell;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.recognizer.Recognizer;

public interface Shell {
    void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException;

    void execute(String command);

    String execute(String command, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;
    
    boolean checkOutput(String command, Recognizer recognizer, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;
}
