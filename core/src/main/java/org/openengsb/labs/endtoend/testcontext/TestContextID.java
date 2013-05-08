package org.openengsb.labs.endtoend.testcontext;

public class TestContextID {
    private final String configFileName;

    public TestContextID(String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public String toString() {
        return configFileName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((configFileName == null) ? 0 : configFileName.hashCode());
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
        if (configFileName == null) {
            if (other.configFileName != null)
                return false;
        } else if (!configFileName.equals(other.configFileName))
            return false;
        return true;
    }
}
