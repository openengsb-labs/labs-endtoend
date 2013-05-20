package org.openengsb.labs.endtoend.karaf.output;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.recognizer.NullRecognizer;
import org.openengsb.labs.endtoend.recognizer.Recognizer;

public class OutputHandler {
    private static final Recognizer nullRecognizer = new NullRecognizer();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final InputStreamReader in;
    private final KarafPromptRecognizer promptRecognizer;

    public OutputHandler(final InputStreamReader in, final KarafPromptRecognizer promptRecognizer) {
        this.in = in;
        this.promptRecognizer = promptRecognizer;
    }

    public void shutdown() {
        try {
            this.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executor.shutdownNow();
    }

    public String getOutput(Long time, TimeUnit timeUnit) throws TimeoutException {
        ResponseWorker responseWorker = new ResponseWorker(this.in, this.promptRecognizer);
        Future<String> future = this.executor.submit(responseWorker);

        try {
            String response = future.get(time, timeUnit);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }

        return responseWorker.getOutput();
    }

    public void waitForPrompt(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        recognize(timeout, timeUnit, this.promptRecognizer);
    }

    public Boolean recognize(Long time, TimeUnit timeUnit, Recognizer positiveRecognizer) throws TimeoutException {
        return recognize(time, timeUnit, positiveRecognizer, nullRecognizer);
    }

    public Boolean recognize(Long time, TimeUnit timeUnit, Recognizer positiveRecognizer, Recognizer negativeRecognizer)
            throws TimeoutException {
        Future<String> future = this.executor.submit(new ResponseWorker(this.in, this.promptRecognizer));

        Boolean result = false;
        try {
            String response = future.get(time, timeUnit);
            if (negativeRecognizer.recognize(response)) {
                return false;
            } else if (positiveRecognizer.recognize(response)) {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }

        return result;
    }
}
