/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.util.commands;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class SimpleRefUpdateCommand {

    private final Git git;
    private final String name;
    private final RevCommit commit;

    public SimpleRefUpdateCommand(final Git git,
                                  final String branchName,
                                  final RevCommit commit) {
        this.git = git;
        this.name = branchName;
        this.commit = commit;
    }

    public void execute() throws IOException, ConcurrentRefUpdateException {
        final ObjectId headId = git.getLastCommit(Constants.R_HEADS + name);
        final RefUpdate ru = git.getRepository().updateRef(Constants.R_HEADS + name);
        if (headId == null) {
            ru.setExpectedOldObjectId(ObjectId.zeroId());
        } else {
            ru.setExpectedOldObjectId(headId);
        }
        ru.setNewObjectId(commit.getId());
        ru.setRefLogMessage(commit.getShortMessage(),
                            false);
        forceUpdate(ru,
                    commit.getId());
    }

    protected void forceUpdate(final RefUpdate ru,
                               final ObjectId id) throws java.io.IOException, ConcurrentRefUpdateException {
        final RefUpdate.Result rc = ru.forceUpdate();
        switch (rc) {
            case NEW:
            case FORCED:
            case FAST_FORWARD:
                break;
            case REJECTED:
            case LOCK_FAILURE:
                throw new ConcurrentRefUpdateException(JGitText.get().couldNotLockHEAD,
                                                       ru.getRef(),
                                                       rc);
            default:
                throw new JGitInternalException(MessageFormat.format(JGitText.get().updatingRefFailed,
                                                                     Constants.HEAD,
                                                                     id.toString(),
                                                                     rc));
        }
    }

    //
//    private void update( final Repository _repo,
//                         final String _name,
//                         final RevCommit _commit )
//            throws IOException {
//        commit( _repo, _commit, ( reader, refTree ) -> {
//            final Ref old = refTree.exactRef( reader, _name );
//            final List<Command> n = new ArrayList<>( 1 );
//            try ( RevWalk rw = new RevWalk( _repo ) ) {
//                n.add( new Command( old, toRef( rw, _commit, _name, true ) ) );
//                if ( git.isKetchEnabled() ) {
//                    proposeKetch( n, _commit );
//                }
//            } catch ( final IOException | InterruptedException e ) {
//                String msg = JGitText.get().transactionAborted;
//                for ( Command cmd : n ) {
//                    if ( cmd.getResult() == NOT_ATTEMPTED ) {
//                        cmd.setResult( REJECTED_OTHER_REASON, msg );
//                    }
//                }
//                throw new GitException( "Error" );
//                //log.error(msg, e);
//            }
//            return refTree.apply( n );
//        } );
//    }
//
//    private void proposeKetch( final List<Command> n,
//                               final RevCommit _commit ) throws IOException, InterruptedException {
//        final Proposal proposal = new Proposal( n )
//                .setAuthor( _commit.getAuthorIdent() )
//                .setMessage( "push" );
//        git.getKetchLeader().queueProposal( proposal );
//        if ( proposal.isDone() ) {
//            // This failed fast, e.g. conflict or bad precondition.
//            throw new GitException( "Error" );
//        }
//        if ( proposal.getState() == QUEUED ) {
//            waitForQueue( proposal );
//        }
//        if ( !proposal.isDone() ) {
//            waitForPropose( proposal );
//        }
//    }
//
//    private void waitForQueue( final Proposal proposal )
//            throws InterruptedException {
//        while ( !proposal.awaitStateChange( QUEUED, 250, MILLISECONDS ) ) {
//            System.out.println( "waiting queue..." );
//        }
//        switch ( proposal.getState() ) {
//            case RUNNING:
//            default:
//                break;
//
//            case EXECUTED:
//                break;
//
//            case ABORTED:
//                break;
//        }
//    }
//
//    private void waitForPropose( final Proposal proposal ) throws InterruptedException {
//        while ( !proposal.await( 250, MILLISECONDS ) ) {
//            System.out.println( "waiting propose..." );
//        }
//    }
//
//    private static Ref toRef( final RevWalk rw,
//                              final ObjectId id,
//                              final String name,
//                              final boolean mustExist ) throws IOException {
//        if ( ObjectId.zeroId().equals( id ) ) {
//            return null;
//        }
//
//        try {
//            RevObject o = rw.parseAny( id );
//            if ( o instanceof RevTag ) {
//                RevObject p = rw.peel( o );
//                return new ObjectIdRef.PeeledTag( NETWORK, name, id, p.copy() );
//            }
//            return new ObjectIdRef.PeeledNonTag( NETWORK, name, id );
//        } catch ( MissingObjectException e ) {
//            if ( mustExist ) {
//                throw e;
//            }
//            return new ObjectIdRef.Unpeeled( NETWORK, name, id );
//        }
//    }
//
//    interface BiFunction {
//
//        boolean apply(final ObjectReader reader,
//                      final RefTree refTree) throws IOException;
//    }
//
//    private void commit( final Repository repo,
//                         final RevCommit original,
//                         final BiFunction fun ) throws IOException {
//        try ( final ObjectReader reader = repo.newObjectReader();
//              final ObjectInserter inserter = repo.newObjectInserter();
//              final RevWalk rw = new RevWalk( reader ) ) {
//
//            final RefTreeDatabase refdb = (RefTreeDatabase) repo.getRefDatabase();
//            final RefDatabase bootstrap = refdb.getBootstrap();
//            final RefUpdate refUpdate = bootstrap.newUpdate( refdb.getTxnCommitted(), false );
//
//            final CommitBuilder cb = new CommitBuilder();
//            final Ref ref = bootstrap.exactRef( refdb.getTxnCommitted() );
//            final RefTree tree;
//            if ( ref != null && ref.getObjectId() != null ) {
//                tree = RefTree.read( reader, rw.parseTree( ref.getObjectId() ) );
//                cb.setParentId( ref.getObjectId() );
//                refUpdate.setExpectedOldObjectId( ref.getObjectId() );
//            } else {
//                tree = RefTree.newEmptyTree();
//                refUpdate.setExpectedOldObjectId( ObjectId.zeroId() );
//            }
//
//            if ( fun.apply( reader, tree ) ) {
//                final Ref ref2 = bootstrap.exactRef( refdb.getTxnCommitted() );
//                if ( ref2 == null || ref2.getObjectId().equals( ref != null ? ref.getObjectId() : null ) ) {
//                    cb.setTreeId( tree.writeTree( inserter ) );
//                    if ( original != null ) {
//                        cb.setAuthor( original.getAuthorIdent() );
//                        cb.setCommitter( original.getAuthorIdent() );
//                    } else {
//                        final PersonIdent personIdent = new PersonIdent( "user", "user@example.com" );
//                        cb.setAuthor( personIdent );
//                        cb.setCommitter( personIdent );
//                    }
//                    refUpdate.setNewObjectId( inserter.insert( cb ) );
//                    inserter.flush();
//                    final RefUpdate.Result result = refUpdate.update( rw );
//                    switch ( result ) {
//                        case NEW:
//                        case FAST_FORWARD:
//                            break;
//                        default:
//                            throw new RuntimeException( repo.getDirectory() + " -> " + result.toString() + " : " + refUpdate.getName() );
//                    }
//                    final File commited = new File( repo.getDirectory(), refdb.getTxnCommitted() );
//                    final File accepted = new File( repo.getDirectory(), refdb.getTxnNamespace() + "accepted" );
//                    Files.copy( commited.toPath(), accepted.toPath(), StandardCopyOption.REPLACE_EXISTING );
//                }
//            }
//        }
//    }
}
