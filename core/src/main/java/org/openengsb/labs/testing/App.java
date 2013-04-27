package org.openengsb.labs.testing;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.testing.api.Karaf;
import org.openengsb.labs.testing.karaf.KarafException;
import org.openengsb.labs.testing.karaf.KarafService;
import org.openengsb.labs.testing.karaf.shell.SSHShell;

public class App {
    public static void main(String[] args) throws KarafException {

        Karaf k = new KarafService();
        try {
            System.out.println("Starting Karaf...");
            k.start(10L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SSHShell shell = null;
        try {
            System.out.println("Remote login...");
            shell = k.login("root", "pw", 10L, TimeUnit.SECONDS);
            System.out.println("Logout...");
            shell.logout();
        } catch (TimeoutException e1) {
            e1.printStackTrace();
        }

        try {
            System.out.println("Stopping Karaf...");
            k.shutdown(10L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        System.out.println("Finished.");
    }
}
