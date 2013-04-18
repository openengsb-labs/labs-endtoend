package org.openengsb.testing.service;

import java.io.ByteArrayInputStream;

import jline.Terminal;

import org.apache.karaf.shell.console.jline.TerminalFactory;
import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.common.RuntimeSshException;
import org.apache.sshd.common.util.NoCloseInputStream;
import org.fusesource.jansi.AnsiConsole;

public class SshConnection {

    public static void main(String args[]) {
        connectToKaraf();
    }

    private static void connectToKaraf() {
        String host = "localhost";
        int port = 8101;
        String user = "karaf";
        String password = "karaf";
        StringBuilder sb = new StringBuilder();
        int level = 1;
        int retryAttempts = 0;
        int retryDelay = 2;

        SshClient client = null;
        Terminal terminal = null;
        try {
            client = SshClient.setUpDefaultClient();
            client.start();
            int retries = 0;
            ClientSession session = null;
            // connect
            do {
                ConnectFuture future = client.connect(host, port);
                future.await();
                try {
                    session = future.getSession();
                } catch (RuntimeSshException ex) {
                    if (retries++ < retryAttempts) {
                        Thread.sleep(retryDelay * 1000);
                        System.out.println("retrying (attempt " + retries + ") ...");
                    } else {
                        throw ex;
                    }
                }
            } while (session == null);
            // auth
            if (!session.authPassword(user, password).await().isSuccess()) {
                throw new Exception("Authentication failure");
            }

            ClientChannel channel;
            if (sb.length() > 0) {
                channel = session.createChannel("exec", sb.append("\n").toString());
                channel.setIn(new ByteArrayInputStream(new byte[0]));
            } else {
                terminal = new TerminalFactory().getTerminal();
                channel = session.createChannel("shell");
                channel.setIn(new NoCloseInputStream(System.in));
                ((ChannelShell) channel).setupSensibleDefaultPty();
            }

            channel.setOut(AnsiConsole.wrapOutputStream(System.out));
            channel.setErr(AnsiConsole.wrapOutputStream(System.err));
            channel.open();
            channel.waitFor(ClientChannel.CLOSED, 0);
        } catch (Throwable t) {
            if (level > 1) {
                t.printStackTrace();
            } else {
                System.err.println(t.getMessage());
            }
            System.exit(1);
        } finally {
            try {
                client.stop();
            } catch (Throwable t) {
            }
            try {
                if (terminal != null) {
                    terminal.restore();
                }
            } catch (Throwable t) {
            }
        }
    }
}
