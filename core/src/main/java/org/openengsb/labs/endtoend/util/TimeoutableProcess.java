package org.openengsb.labs.endtoend.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeoutableProcess extends Process {
    private final Process process;

    public TimeoutableProcess(Process process) {
        this.process = process;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.process.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return this.process.getInputStream();
    }

    @Override
    public InputStream getErrorStream() {
        return this.process.getErrorStream();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return this.process.waitFor();
    }

    @Override
    public int exitValue() {
        return this.process.exitValue();
    }

    @Override
    public void destroy() {
        this.process.destroy();
    }

    public void waitFor(Long timeout, TimeUnit timeUnit) throws TimeoutException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> waitFor = service.submit(new ShutdownWorker());
        try {
            waitFor.get(timeout, timeUnit);
        } catch (TimeoutException e) {
            waitFor.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }

    private class ShutdownWorker implements Callable<Integer> {
        @Override
        public Integer call() throws IOException, InterruptedException {
            try {
                return TimeoutableProcess.this.process.waitFor();
            } catch (InterruptedException e) {
                return 0;
            }
        }
    }
}
