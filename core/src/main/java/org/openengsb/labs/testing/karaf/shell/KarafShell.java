package org.openengsb.labs.testing.karaf.shell;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.api.Shell;
import org.openengsb.labs.testing.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.testing.karaf.output.OutputHandler;
import org.openengsb.labs.testing.recognizer.Recognizer;

public class KarafShell implements Shell {
    private final Recognizer karafPromptRecognizer = new KarafPromptRecognizer("karaf", "root");

    private final PrintWriter pw;
    private final OutputHandler outputHandler;

    public KarafShell(OutputStream output, InputStream input) {
        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
        this.outputHandler = new OutputHandler(new InputStreamReader(input),
            karafPromptRecognizer);
    }

    @Override
    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        outputHandler.recognize(timeout, timeUnit, this.karafPromptRecognizer);
    }

    public void execute(String command) {
        this.pw.println(command);
        this.pw.flush();
    }

    @Override
    public String execute(String command, Long timeout, TimeUnit timeUnit) throws TimeoutException {
        // TODO Auto-generated method stub
        return null;
    }

    public void close() {
        this.pw.close();
        this.outputHandler.shutdown();
    }
}
