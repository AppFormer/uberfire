/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit;

import java.util.List;
import java.util.Map;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.FileSystemStateAware;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.LockableFileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.model.CommitInfo;


import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.eclipse.jgit.lib.Repository.shortenRefName;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public interface JGitFileSystem extends FileSystem,
                                        FileSystemId,
                                        FileSystemStateAware,
                                        LockableFileSystem { 

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

    void addOldHeadsOfPendingDiffs(String branchName,
                                   NotificationModel notificationModel);

    Map<String, NotificationModel> getOldHeadsOfPendingDiffs();

    boolean hasOldHeadsOfPendingDiffs();

    void clearOldHeadsOfPendingDiffs();

}
