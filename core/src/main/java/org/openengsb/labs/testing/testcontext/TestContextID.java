package org.openengsb.labs.testing.testcontext;

import java.io.File;

public class TestContextID {
    private final File configFile;

    public TestContextID(File configFile) {
        super();
        this.configFile = configFile;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configFile == null) ? 0 : configFile.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TestContextID other = (TestContextID) obj;
        if (configFile == null) {
            if (other.configFile != null)
                return false;
        } else if (!configFile.equals(other.configFile))
            return false;
        return true;
    }

    @Override
    public String toString() {
        // TODO Unique name.
        return configFile.getName();
    }

}
