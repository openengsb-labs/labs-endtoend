package org.openengsb.labs.testing.karaf.output;

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

import org.openengsb.labs.testing.recognizer.NullRecognizer;
import org.openengsb.labs.testing.recognizer.Recognizer;

public class OutputHandler {
    private static final int DEFAULT_BUFFER_SIZE = 20;
    private static final Recognizer nullRecognizer = new NullRecognizer();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final InputStreamReader in;
    private final Recognizer promptRecognizer;

    public OutputHandler(final InputStreamReader in, final Recognizer promptRecognizer) {
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
            new ResponseWorker(this.promptRecognizer, this.promptRecognizer, nullRecognizer);
        Future<Boolean> future = this.executor.submit(responseWorker);

        try {
            future.get(time, timeUnit);
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
        Future<Boolean> future =
            this.executor.submit(new ResponseWorker(this.promptRecognizer, positiveRecognizer, negativeRecognizer));

        Boolean result = false;
        try {
            result = future.get(time, timeUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
        }

        return result;
    }

    private class ResponseWorker implements Callable<Boolean> {
        private final Recognizer positiveRecognizer;
        private final Recognizer negativeRecognizer;
        private final StringBuilder out = new StringBuilder();
        private final Recognizer promptRecognizer;

        public ResponseWorker(Recognizer promptRecognizer, Recognizer positiveRecognizer, Recognizer negativeRecognizer) {
            this.promptRecognizer = promptRecognizer;
            this.positiveRecognizer = positiveRecognizer;
            this.negativeRecognizer = negativeRecognizer;
        }

        public String getOutput() {
            return this.out.toString();
        }

        @Override
        public Boolean call() throws IOException {
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
                    if (this.negativeRecognizer.recognize(out.toString())) {
                        return false;
                    } else if (this.positiveRecognizer.recognize(out.toString())) {
                        return true;
                    }
                }
            }
        }
    }
}
