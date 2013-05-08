package org.openengsb.labs.endtoend.api;

import org.openengsb.labs.endtoend.karaf.KarafException;

public interface RemoteShell extends Shell {
    void logout() throws KarafException;
}
