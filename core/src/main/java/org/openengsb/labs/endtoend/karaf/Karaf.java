package org.openengsb.labs.endtoend.karaf;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openengsb.labs.endtoend.karaf.shell.RemoteShell;
import org.openengsb.labs.endtoend.karaf.shell.Shell;

public interface Karaf {
    /**
     * Starts the Karaf instance.
     * 
     * @param timeout Time to wait for Karaf to start.
     * @param timeUnit Unit of timeout.
     * @throws TimeoutException If Karaf didn't start within timeout, i.e. the root shell prompt wasn't displayed within
     *         timeout.
     */
    void start(Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;

    /**
     * Gets the root shell from this Karaf instance.
     * 
     * @return The Karaf root shell.
     */
    Shell getShell();

    /**
     * Shuts down the Karaf instance gracefully. If Karaf doesn't shutdown within the timeout, the process is killed.
     * 
     * @param timeout Time to wait for Karaf to shutdown.
     * @param timeUnit Unit of timeout.
     * @throws TimeoutException If the Karaf instance didn't shutdown within the timeout and thus the proecess had to be
     *         killed.
     */
    void shutdown(Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;

    /**
     * Login remotely with the given credentials using the standard Karaf client in a separate system process.
     * 
     * @param username The username.
     * @param pass The Password.
     * @param timeout Time to wait for prompt.
     * @param timeUnit Unit of timeout.
     * @return A karaf shell.
     * @throws TimeoutException If the login didn't succeed within timeout, i.e. the shell prompt wasn't displayed
     *         within timeout.
     */
    RemoteShell login(String username, String pass, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;
    
    /**
     * Install a karaf feature.
     * 
     * @param feature the feature to install
     * @param timeout time to wait for prompt
     * @param timeUnit unit for the timeout
     * @throws CommandTimeoutException if the command didn't execute within timeout interval
     */
    void installFeature(String feature, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;
    
    /**
     * Checks if a feature is installed.
     * 
     * @param feature the feature to check
     * @param timeout time to wait for prompt
     * @param timeUnit unit for the timeout
     * @return <code>true</code> if the feature is installed, <code>false</code> otherwise
     * @throws CommandTimeoutException if the command didn't execute within timeout interval
     */
    boolean isFeatureInstalled(String feature, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;
    
    /**
     * Checks if a bundle is in active state.
     * 
     * @param bundleName the bundle name
     * @param timeout time to wait for prompt
     * @param timeUnit unit for the timeout
     * @return <code>true</code> if the bundle is active, and <code>false</code> otherwise, or if bundle is not found.
     * @throws CommandTimeoutException if the command didn't execute within timeout interval
     */
    boolean isBundleActive(String bundleName, Long timeout, TimeUnit timeUnit) throws CommandTimeoutException;
}
