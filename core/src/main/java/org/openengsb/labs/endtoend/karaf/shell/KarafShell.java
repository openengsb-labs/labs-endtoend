package org.openengsb.labs.endtoend.karaf.shell;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.openengsb.labs.endtoend.karaf.output.KarafPromptRecognizer;
import org.openengsb.labs.endtoend.karaf.output.OutputHandler;

public class KarafShell extends AbstractKarafShell {
    private final PrintWriter pw;
    private final OutputHandler outputHandler;

    public KarafShell(OutputStream output, InputStream input, String applicationName) {
        this.pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output)));
        this.outputHandler = new OutputHandler(new InputStreamReader(input), new KarafPromptRecognizer("karaf",
                applicationName));
    }
    
    public void close() {
        this.pw.close();
        this.outputHandler.shutdown();
    }

    @Override
    protected OutputHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    protected PrintWriter getPrintWriter() {
        return pw;
    }
}
