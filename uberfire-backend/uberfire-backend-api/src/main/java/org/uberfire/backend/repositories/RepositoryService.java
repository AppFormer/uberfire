package org.uberfire.backend.repositories;

import java.util.Collection;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileSystem;

@Remote
public interface RepositoryService {

    Repository getRepository( final String alias );

    Repository getRepository( final Path root );

    Collection<Repository> getRepositories();

    Repository createRepository( final OrganizationalUnit organizationalUnit,
                                 final String scheme,
                                 final String alias,
                                 final Map<String, Object> env ) throws RepositoryAlreadyExistsException;

    Repository createRepository( final String scheme,
                                 final String alias,
                                 final Map<String, Object> env ) throws RepositoryAlreadyExistsException;

    String normalizeRepositoryName( final String name );

    void addRole( final Repository repository,
                  final String role );

    void removeRole( final Repository repository,
                     final String role );

    void removeRepository( final String alias );

}
