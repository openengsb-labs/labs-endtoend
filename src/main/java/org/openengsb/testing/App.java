package org.openengsb.testing;

import org.openengsb.testing.api.Karaf;
import org.openengsb.testing.api.User;
import org.openengsb.testing.service.KarafException;
import org.openengsb.testing.service.KarafService;
import org.openengsb.testing.service.RemoteUser;

public class App {
    public static void main(String[] args) throws KarafException {

        Karaf k = new KarafService();
        try {
            System.out.println("Starting Karaf...");
            k.start(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User u = new RemoteUser();
        u.login();
        u.logout();

        try {
            System.out.println("Stopping Karaf...");
            k.shutdown(10);
        } catch (KarafException e) {
            e.printStackTrace();
        }

        System.out.println("Finished.");
    }
}
