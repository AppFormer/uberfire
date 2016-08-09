/*
 * Copyright 2012 JBoss Inc
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RebaseCommand.InteractiveHandler;
import org.eclipse.jgit.api.RebaseResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IllegalTodoFileModification;
import org.eclipse.jgit.lib.RebaseTodoLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Test;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;
import static org.uberfire.java.nio.fs.jgit.AbstractTestInfra.createTempDirectory;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.DIRECTORY;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.FILE;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.PathType.NOT_FOUND;

public class JGitSquashingTest extends AbstractTestInfra {

    static {
        CredentialsProvider.setDefault(new UsernamePasswordCredentialsProvider("guest", ""));
    }

    /*
     * The following test shows how to do a rebase with Fixup to squash a set of commits
     * Notice that RebaseCommand only works on non-BARE repos that's why I need to clone 
     *   the original repo using the BARE repo flag set to false
     * This initial test is here to demonstrate what we need to achieve on the BARE repo
     */
    @Test
    public void rawRebaseWithFixUp() throws IOException, GitAPIException { // AKA SQUASH
        final File parentFolder = createTempDirectory();

        final File gitFolder = new File(parentFolder, "myrepo.git");
        // Create the origin repo
        final Git origin = JGitUtil.newRepository(gitFolder, true);

        final File gitClonedFolder = new File(parentFolder, "myclone.git");

        // Clone origin as normal (non-BARE) repository
        final Git clone = cloneRepository(gitClonedFolder, origin.getRepository().getDirectory().toString(), false, CredentialsProvider.getDefault());

        // create the file
        createAddAndCommitFile(clone, "testfile0");

        Iterable<RevCommit> logs = clone.log().all().setMaxCount(1).call();
        Iterator<RevCommit> iterator = logs.iterator();
        assertThat(iterator.hasNext()).isTrue();
        RevCommit firstCommit = iterator.next();

        // create the file
        createAddAndCommitFile(clone, "testfile1");

        // create the file
        createAddAndCommitFile(clone, "testfile2");

        // create the file
        createAddAndCommitFile(clone, "testfile3");

        logs = clone.log().all().setMaxCount(1).call();
        iterator = logs.iterator();
        assertThat(iterator.hasNext()).isTrue();
        final RevCommit thirdCommit = iterator.next();
        // create the file
        createAddAndCommitFile(clone, "testfile4");

        final String squashedCommitMessage = "squashed changes";
        final RevCommit lastSquashedCommit = thirdCommit;
        InteractiveHandler handler = new InteractiveHandler() {
            public void prepareSteps(List<RebaseTodoLine> steps) {
                try {
                    // loop through steps and use setAction to change action

                    int counter = 0;
                    for (RebaseTodoLine step : steps) {
                        if (counter == 0) {
                            step.setAction(RebaseTodoLine.Action.PICK);
                        } else {
                            step.setAction(RebaseTodoLine.Action.SQUASH);
                        }
                        if (step.getCommit().prefixCompare(lastSquashedCommit) == 0) {
                            break;
                        }
                        counter++;
                    }
                } catch (IllegalTodoFileModification ex) {
                    Logger.getLogger(JGitSquashingTest.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            @Override
            public String modifyCommitMessage(String oldMessage) {
                return squashedCommitMessage;
            }
        };

        System.out.println("#### Before Rebase");
        int counter = 0;
        for (RevCommit commit : clone.log().all().call()) {
            System.out.println(">Commit: " + commit.getFullMessage());
            counter++;
        }
        System.out.println("#### Before Rebase Commits: "+ counter);

        RebaseResult rebaseResult = clone.rebase().setUpstream(firstCommit).runInteractively(handler).call();
        assertThat(rebaseResult.getStatus()).isSameAs(RebaseResult.Status.OK);
        

        System.out.println("#### After Rebase");
        counter = 0;
        for (RevCommit commit : clone.log().all().call()) {
            System.out.println(">Commit: " + commit.getFullMessage());
            counter++;
        }
        System.out.println("#### After Rebase Commits: "+ counter);

    }

    /*
     * This test make 5 commits and then squah the last 4 into a single commit
    */
    
    @Test
    public void testSquash4Of5Commits() throws IOException, GitAPIException {

        final File parentFolder = createTempDirectory();
        System.out.println(">> Parent Forlder for the Test: "+ parentFolder.getAbsolutePath());
        final File gitFolder = new File(parentFolder, "my-local-repo.git");

        final Git origin = JGitUtil.newRepository(gitFolder, true);

        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 1!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file1.txt", tempFile("initial content file 1"));
            }
        });
 
        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 2!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file2.txt", tempFile("initial content file 2"));
            }
        });
        Iterable<RevCommit> logs = origin.log().setMaxCount(1).all().call();
        RevCommit secondCommit = logs.iterator().next(); 

        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 3!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file1.txt", tempFile("new content file 1"));
            }
        });

        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 4!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file2.txt", tempFile("new content file 2"));
            }
        });
        
        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 5!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file3.txt", tempFile("initial content file 3"));
            }
        });
        logs = origin.log().all().call();
        int commitsCount = 0;
        for (RevCommit commit : logs) {
            System.out.println(">>> Origin Commit: " + commit.getFullMessage() +  " - " + commit.toString());
            commitsCount++;
        }
        assertThat(commitsCount).isEqualTo(5);
        
        assertThat(JGitUtil.checkPath(origin, "master", "pathx/").getK1()).isEqualTo(NOT_FOUND);
        assertThat(JGitUtil.checkPath(origin, "master", "path/to/file1.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin, "master", "path/to/file2.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin, "master", "path/to/file3.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin, "master", "path/to").getK1()).isEqualTo(DIRECTORY);

        System.out.println("Squashing from "+ secondCommit.getName() + "  to HEAD");
        squash(origin.getRepository(), "squashed message", secondCommit.getName());

        commitsCount = 0;
        for (RevCommit commit : origin.log().all().call()) {
            System.out.println(">>> Final Commit: " + commit.getFullMessage() +  " - " + commit.toString());
            commitsCount++;
        }
        assertThat(commitsCount).isEqualTo(2);
        
    }
    
    /*
     * This test also perform 5 commits and squash the last 4 into a single commit
     *  but now the changes are in different paths
    */
    @Test
    public void testSquashCommitsWithDifferentPaths() throws IOException, GitAPIException {

        final File parentFolder = createTempDirectory();
        System.out.println(">> Parent Forlder for the Test: "+ parentFolder.getAbsolutePath());
        final File gitFolder = new File(parentFolder, "my-local-repo.git");

        final Git origin = JGitUtil.newRepository(gitFolder, true);

        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 1!", null, null, false, new HashMap<String, File>() {
            {
                put("file1.txt", tempFile("initial content file 1"));
            }
        });
 
        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 2!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file2.txt", tempFile("initial content file 2"));
            }
        });
        Iterable<RevCommit> logs = origin.log().setMaxCount(1).all().call();
        RevCommit secondCommit = logs.iterator().next(); 

        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 3!", null, null, false, new HashMap<String, File>() {
            {
                put("file1.txt", tempFile("new content file 1"));
            }
        });

        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 4!", null, null, false, new HashMap<String, File>() {
            {
                put("path/to/file2.txt", tempFile("new content file 2"));
            }
        });
        
        commit(origin, "master", "salaboy", "salaboy@example.com", "commit 5!", null, null, false, new HashMap<String, File>() {
            {
                put("path/file3.txt", tempFile("initial content file 3"));
            }
        });
        
        for (RevCommit commit : origin.log().all().call()) {
            System.out.println(">>> Origin Commit: " + commit.getFullMessage() +  " - " + commit.toString());
        }

        assertThat(JGitUtil.checkPath(origin, "master", "pathx/").getK1()).isEqualTo(NOT_FOUND);
        assertThat(JGitUtil.checkPath(origin, "master", "file1.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin, "master", "path/to/file2.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin, "master", "path/file3.txt").getK1()).isEqualTo(FILE);
        assertThat(JGitUtil.checkPath(origin, "master", "path/to").getK1()).isEqualTo(DIRECTORY);

        System.out.println("Squashing from "+ secondCommit.getName() + "  to HEAD");
        squash(origin.getRepository(), "squashed message", secondCommit.getName());

   
        for (RevCommit commit : origin.log().all().call()) {
            System.out.println(">>> Final Commit: " + commit.getFullMessage() +  " - " + commit.toString());
        }
        
        
    }
    
    private void createAddAndCommitFile(Git git, String file) throws GitAPIException, IOException {
        File myfile = new File(git.getRepository().getDirectory().getParent(), file);
        myfile.createNewFile();

        // run the add
        git.add()
                .addFilepattern(file)
                .call();

        git.commit()
                .setMessage("Added " + file)
                .call();

    }
}
