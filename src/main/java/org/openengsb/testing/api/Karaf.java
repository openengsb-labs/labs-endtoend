package org.openengsb.testing.api;

import org.openengsb.testing.service.KarafException;

public interface Karaf {
    void start(Integer timeout) throws KarafException;

    void shutdown(Integer timeout) throws KarafException;

    void addFeature(String feature);
}
