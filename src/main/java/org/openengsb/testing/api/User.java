package org.openengsb.testing.api;

import org.openengsb.testing.service.KarafException;

public interface User {
    void login() throws KarafException;

    void logout() throws KarafException;
}
