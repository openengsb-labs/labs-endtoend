package org.openengsb.labs.endtoend.karaf.shell;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.CommandTimeoutException;
import org.openengsb.labs.endtoend.karaf.output.OutputHandler;
import org.openengsb.labs.endtoend.recognizer.Recognizer;

abstract class AbstractKarafShell implements Shell {

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        getOutputHandler().waitForPrompt(timeout, timeUnit);
    }

    @Override
    public void execute(String command) {
        getPrintWriter().println(command);
        getPrintWriter().flush();
    }

    @Override
    public String execute(String command, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException {
        getPrintWriter().println(command);
        getPrintWriter().flush();
        try {
            return getOutputHandler().getOutput(timeout, timeUnit);
        } catch (TimeoutException e) {
            throw new CommandTimeoutException(command, e);
        }
    }

    @Override
    public boolean checkOutput(String command, Recognizer recognizer, Long timeout, TimeUnit timeUnit)
        throws CommandTimeoutException {
        getPrintWriter().println(command);
        getPrintWriter().flush();
        try {
            return getOutputHandler().recognize(timeout, timeUnit, recognizer);
        } catch (TimeoutException e) {
            throw new CommandTimeoutException(command, e);
        }
    }
    
    protected abstract OutputHandler getOutputHandler();

    protected abstract PrintWriter getPrintWriter();
}
