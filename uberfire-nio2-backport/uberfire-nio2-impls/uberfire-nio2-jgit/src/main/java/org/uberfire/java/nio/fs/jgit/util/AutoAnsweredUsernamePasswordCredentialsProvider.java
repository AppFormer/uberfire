package org.uberfire.java.nio.fs.jgit.util;

import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to directly respond to YesNo questions that might be issued by underlying transport provider.
 * Example: when connecting to ssh server for the first time user is prompted to accept ssh key of given server
 * In some cases (like async processing) users can't be involved in the decision thus administrator
 * should have ability to control it globally as CredentialsProvider is set one for the JVM.
 *
 * By default it's configured to auto accept (answer Yes) but this can be altered by setting system property
 * <code>org.uberfire.git.ssh.accept</code> to <code>false</code> to reject (answer No)
 */
public class AutoAnsweredUsernamePasswordCredentialsProvider extends UsernamePasswordCredentialsProvider {

    private static final Logger logger = LoggerFactory.getLogger(AutoAnsweredUsernamePasswordCredentialsProvider.class);

    private boolean accept = Boolean.parseBoolean(System.getProperty("org.uberfire.git.ssh.accept", "true"));

    /**
     * @inheritDoc
     */
    public AutoAnsweredUsernamePasswordCredentialsProvider(String username, String password) {
        super(username, password);
    }

    /**
     * @inheritDoc
     */
    public AutoAnsweredUsernamePasswordCredentialsProvider(String username, char[] password) {
        super(username, password);
    }

    /**
     * Same as <code>AutoAnsweredUsernamePasswordCredentialsProvider(String username, String password)</code> with addition
     * to allow to explicitly set what should be the response to YesNo prompts
     * @param username
     * @param password
     * @param accept
     */
    public AutoAnsweredUsernamePasswordCredentialsProvider(String username, String password, boolean accept) {
        super(username, password);
        this.accept = accept;
    }

    /**
     * Same as <code>AutoAnsweredUsernamePasswordCredentialsProvider(String username, char[] password)</code> with addition
     * to allow to explicitly set what should be the response to YesNo prompts
     * @param username
     * @param password
     * @param accept
     */
    public AutoAnsweredUsernamePasswordCredentialsProvider(String username, char[] password, boolean accept) {
        super(username, password);
        this.accept = accept;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
        try {
            return super.get(uri, items);
        } catch (UnsupportedCredentialItem e) {
            for (CredentialItem i : items) {
                if (i instanceof CredentialItem.YesNoType) {
                    logger.debug("Auto answer to question '{}' with response {}", i.getPromptText(), accept);
                    ((CredentialItem.YesNoType) i).setValue(accept);

                    return true;
                } else {
                    continue;
                }
            }
        }
        return false;
    }
}
