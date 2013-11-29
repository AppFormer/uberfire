package org.uberfire.java.nio.fs.jgit.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.dircache.DirCacheIterator;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.NullProgressMonitor;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.NullOutputStream;

import static org.eclipse.jgit.lib.Constants.HEAD;

public class CustomDiffCommand extends GitCommand<List<DiffEntry>> {

    private AbstractTreeIterator oldTree;

    private AbstractTreeIterator newTree;

    private boolean cached;

    private TreeFilter pathFilter = TreeFilter.ALL;

    private boolean showNameAndStatusOnly;

    private OutputStream out;

    private int contextLines = -1;

    private String sourcePrefix;

    private String destinationPrefix;

    private ProgressMonitor monitor = NullProgressMonitor.INSTANCE;

    /**
     * @param repo
     */
    protected CustomDiffCommand( Repository repo ) {
        super( repo );
    }

    /**
     * Executes the {@code Diff} command with all the options and parameters
     * collected by the setter methods (e.g. {@link #setCached(boolean)} of this
     * class. Each instance of this class should only be used for one invocation
     * of the command. Don't call this method twice on an instance.
     * @return a DiffEntry for each path which is different
     */
    public List<DiffEntry> call() throws GitAPIException {
        final DiffFormatter diffFmt;
        if ( out != null && !showNameAndStatusOnly ) {
            diffFmt = new DiffFormatter( new BufferedOutputStream( out ) );
        } else {
            diffFmt = new DiffFormatter( NullOutputStream.INSTANCE );
        }
        diffFmt.setRepository( repo );
        diffFmt.setProgressMonitor( monitor );
        diffFmt.setDetectRenames( true );
        try {
            if ( cached ) {
                if ( oldTree == null ) {
                    ObjectId head = repo.resolve( HEAD + "^{tree}" ); //$NON-NLS-1$
                    if ( head == null ) {
                        throw new NoHeadException( JGitText.get().cannotReadTree );
                    }
                    CanonicalTreeParser p = new CanonicalTreeParser();
                    ObjectReader reader = repo.newObjectReader();
                    try {
                        p.reset( reader, head );
                    } finally {
                        reader.release();
                    }
                    oldTree = p;
                }
                newTree = new DirCacheIterator( repo.readDirCache() );
            } else {
                if ( oldTree == null ) {
                    oldTree = new DirCacheIterator( repo.readDirCache() );
                }
                if ( newTree == null ) {
                    newTree = new FileTreeIterator( repo );
                }
            }

            diffFmt.setPathFilter( pathFilter );

            List<DiffEntry> result = diffFmt.scan( oldTree, newTree );
            if ( showNameAndStatusOnly ) {
                return result;
            } else {
                if ( contextLines >= 0 ) {
                    diffFmt.setContext( contextLines );
                }
                if ( destinationPrefix != null ) {
                    diffFmt.setNewPrefix( destinationPrefix );
                }
                if ( sourcePrefix != null ) {
                    diffFmt.setOldPrefix( sourcePrefix );
                }
                diffFmt.format( result );
                diffFmt.flush();
                return result;
            }
        } catch ( IOException e ) {
            throw new JGitInternalException( e.getMessage(), e );
        } finally {
            diffFmt.release();
        }
    }

    /**
     * @param cached whether to view the changes you staged for the next commit
     * @return this instance
     */
    public CustomDiffCommand setCached( boolean cached ) {
        this.cached = cached;
        return this;
    }

    /**
     * @param pathFilter parameter, used to limit the diff to the named path
     * @return this instance
     */
    public CustomDiffCommand setPathFilter( TreeFilter pathFilter ) {
        this.pathFilter = pathFilter;
        return this;
    }

    /**
     * @param oldTree the previous state
     * @return this instance
     */
    public CustomDiffCommand setOldTree( AbstractTreeIterator oldTree ) {
        this.oldTree = oldTree;
        return this;
    }

    /**
     * @param newTree the updated state
     * @return this instance
     */
    public CustomDiffCommand setNewTree( AbstractTreeIterator newTree ) {
        this.newTree = newTree;
        return this;
    }

    /**
     * @param showNameAndStatusOnly whether to return only names and status of changed files
     * @return this instance
     */
    public CustomDiffCommand setShowNameAndStatusOnly( boolean showNameAndStatusOnly ) {
        this.showNameAndStatusOnly = showNameAndStatusOnly;
        return this;
    }

    /**
     * @param out the stream to write line data
     * @return this instance
     */
    public CustomDiffCommand setOutputStream( OutputStream out ) {
        this.out = out;
        return this;
    }

    /**
     * Set number of context lines instead of the usual three.
     * @param contextLines the number of context lines
     * @return this instance
     */
    public CustomDiffCommand setContextLines( int contextLines ) {
        this.contextLines = contextLines;
        return this;
    }

    /**
     * Set the given source prefix instead of "a/".
     * @param sourcePrefix the prefix
     * @return this instance
     */
    public CustomDiffCommand setSourcePrefix( String sourcePrefix ) {
        this.sourcePrefix = sourcePrefix;
        return this;
    }

    /**
     * Set the given destination prefix instead of "b/".
     * @param destinationPrefix the prefix
     * @return this instance
     */
    public CustomDiffCommand setDestinationPrefix( String destinationPrefix ) {
        this.destinationPrefix = destinationPrefix;
        return this;
    }

    /**
     * The progress monitor associated with the diff operation. By default, this
     * is set to <code>NullProgressMonitor</code>
     * @param monitor a progress monitor
     * @return this instance
     * @see NullProgressMonitor
     */
    public CustomDiffCommand setProgressMonitor( ProgressMonitor monitor ) {
        this.monitor = monitor;
        return this;
    }
}