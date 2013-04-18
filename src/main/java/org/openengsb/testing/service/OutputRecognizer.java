package org.openengsb.testing.service;

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

public class OutputRecognizer {
    private static final int DEFAULT_BUFFER_SIZE = 20;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final InputStreamReader in;

    public OutputRecognizer(final InputStreamReader in) {
        this.in = in;
    }

    public Boolean recognize(Integer time, TimeUnit timeUnit, Recognizer positiveRecognizer) {
        return recognize(time, timeUnit, positiveRecognizer, null);
    }

    public Boolean recognize(Integer time, TimeUnit timeUnit, Recognizer positiveRecognizer,
            Recognizer negativeRecognizer) {
        Future<Boolean> future = this.executor.submit(new RecognizeWorker(positiveRecognizer, negativeRecognizer));
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

    private class RecognizeWorker implements Callable<Boolean> {

        private Recognizer positiveRecognizer;
        private Recognizer negativeRecognizer;

        public RecognizeWorker(Recognizer positiveRecognizer, Recognizer negativeRecognizer) {
            this.positiveRecognizer = positiveRecognizer;
            this.negativeRecognizer = negativeRecognizer;
        }

        @Override
        public Boolean call() throws IOException {
            StringBuilder out = new StringBuilder();
            CharBuffer buf = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);

            while (true) {
                while (!in.ready()) {
                }
                while (in.ready()) {
                    in.read(buf);
                    out.append(buf.flip());
                    buf.clear();
                }
                System.out.println(out);
                if (this.negativeRecognizer != null && this.negativeRecognizer.recognize(out.toString())) {
                    return false;
                } else if (this.positiveRecognizer.recognize(out.toString())) {
                    return true;
                }
            }
        }
    }

    public void shutdown() {
        try {
            this.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.executor.shutdown();
    }
}
