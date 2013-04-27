package org.openengsb.labs.testing.api;

import org.openengsb.labs.testing.karaf.KarafException;

public interface RemoteShell extends Shell {
    void logout() throws KarafException;
}
