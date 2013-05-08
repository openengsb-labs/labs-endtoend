package org.openengsb.labs.endtoend.karaf.output;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.recognizer.NullRecognizer;
import org.openengsb.labs.endtoend.recognizer.Recognizer;

public class OutputHandler {
    private static final int DEFAULT_BUFFER_SIZE = 20;
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
        this.executor.shutdown();
    }

    public String getOutput(Long time, TimeUnit timeUnit) throws TimeoutException {
        ResponseWorker responseWorker =
            new ResponseWorker(this.promptRecognizer);
        Future<String> future = this.executor.submit(responseWorker);

        try {
            String response = future.get(time, timeUnit);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw e;
        }

        return responseWorker.getOutput();
    }

    public Boolean recognize(Long time, TimeUnit timeUnit, Recognizer positiveRecognizer) {
        return recognize(time, timeUnit, positiveRecognizer, nullRecognizer);
    }

    public Boolean recognize(Long time, TimeUnit timeUnit, Recognizer positiveRecognizer,
            Recognizer negativeRecognizer) {
        Future<String> future =
            this.executor.submit(new ResponseWorker(this.promptRecognizer));

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
        }

        return result;
    }

    private class ResponseWorker implements Callable<String> {
        private final StringBuilder out = new StringBuilder();
        private final KarafPromptRecognizer promptRecognizer;

        public ResponseWorker(KarafPromptRecognizer promptRecognizer) {
            this.promptRecognizer = promptRecognizer;
        }

        public String getOutput() {
            return this.out.toString();
        }

        @Override
        public String call() throws IOException {
            CharBuffer buf = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);

            while (true) {
                while (!in.ready()) {
                }
                while (in.ready()) {
                    in.read(buf);
                    out.append(buf.flip());
                    buf.clear();
                }

                if (this.promptRecognizer.recognize(out.toString())) {
                    out.delete(out.length() - 1 - this.promptRecognizer.getPrompt().length(), out.length());
                    return out.toString();
                }
            }
        }
    }
}
