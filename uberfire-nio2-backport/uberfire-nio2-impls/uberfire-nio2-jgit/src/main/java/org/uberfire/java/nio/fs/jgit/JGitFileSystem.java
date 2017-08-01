package org.uberfire.java.nio.fs.jgit;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.FileSystemStateAware;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;

public interface JGitFileSystem extends FileSystem,
                                        FileSystemId,
                                        FileSystemStateAware {

    String getName();

    Git getGit();

    CredentialsProvider getCredential();

    void checkClosed() throws IllegalStateException;

    void publishEvents(Path watchable,
                       List<WatchEvent<?>> elist);

    boolean isOnBatch();

    void setState(String state);

    CommitInfo buildCommitInfo(String defaultMessage,
                               CommentedOption op);

    void setBatchCommitInfo(String defaultMessage,
                            CommentedOption op);

    void setHadCommitOnBatchState(Path path,
                                  boolean hadCommitOnBatchState);

    void setHadCommitOnBatchState(boolean value);

    boolean isHadCommitOnBatchState(Path path);

    void setBatchCommitInfo(CommitInfo batchCommitInfo);

    CommitInfo getBatchCommitInfo();

    int incrementAndGetCommitCount();

    void resetCommitCount();

    int getNumberOfCommitsSinceLastGC();

    void lock();

    void unlock();

    void addOldHeadsOfPendingDiffs(String branchName,
                                   NotificationModel notificationModel);

    Map<String, NotificationModel> getOldHeadsOfPendingDiffs();

    boolean hasOldHeadsOfPendingDiffs();

    void clearOldHeadsOfPendingDiffs();
}
