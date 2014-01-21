package org.uberfire.backend.server.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.organizationalunit.OrganizationalUnitService;
import org.uberfire.backend.repositories.NewRepositoryEvent;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryAlreadyExistsException;
import org.uberfire.backend.repositories.RepositoryInfo;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.repositories.impl.PortableVersionRecord;
import org.uberfire.backend.server.config.ConfigGroup;
import org.uberfire.backend.server.config.ConfigItem;
import org.uberfire.backend.server.config.ConfigType;
import org.uberfire.backend.server.config.ConfigurationFactory;
import org.uberfire.backend.server.config.ConfigurationService;
import org.uberfire.backend.server.config.SystemRepositoryChangedEvent;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.server.util.TextUtil;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.FileSystem;

import static org.uberfire.backend.server.config.ConfigType.*;
import static org.uberfire.backend.server.repositories.EnvironmentParameters.*;
import static org.uberfire.backend.server.util.Paths.*;

@Service
@ApplicationScoped
public class RepositoryServiceImpl implements RepositoryService {

    private static final int HISTORY_PAGE_SIZE = 10;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("system")
    private Repository systemRepository;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private OrganizationalUnitService organizationalUnitService;

    @Inject
    private ConfigurationFactory configurationFactory;

    @Inject
    private RepositoryFactory repositoryFactory;

    @Inject
    private Event<NewRepositoryEvent> event;

    private Map<String, Repository> configuredRepositories = new HashMap<String, Repository>();
    private Map<Path, Repository> rootToRepo = new HashMap<Path, Repository>();

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void loadRepositories() {
        final List<ConfigGroup> repoConfigs = configurationService.getConfiguration( REPOSITORY );
        if ( !( repoConfigs == null || repoConfigs.isEmpty() ) ) {
            for ( final ConfigGroup config : repoConfigs ) {
                final Repository repository = repositoryFactory.newRepository( config );
                configuredRepositories.put( repository.getAlias(), repository );
                rootToRepo.put( repository.getRoot(), repository );
            }
        }

        ioService.onNewFileSystem( new IOService.NewFileSystemListener() {

            @Override
            public void execute( final FileSystem newFileSystem,
                                 final String scheme,
                                 final String name,
                                 final Map<String, ?> env ) {
                if ( getRepository( name ) == null ) {
                    createRepository( scheme, name, (Map<String, Object>) env );
                }
            }
        } );
    }

    @Override
    public Repository getRepository( final String alias ) {
        return configuredRepositories.get( alias );
    }

    public Repository getRepository( final FileSystem fs ) {
        if ( fs == null ) {
            return null;
        }

        final org.uberfire.backend.vfs.FileSystem fsystem = Paths.convert( fs );
        for ( final Repository repository : configuredRepositories.values() ) {
            if ( repository.getRoot().getFileSystem().equals( fsystem ) ) {
                return repository;
            }
        }

        if ( systemRepository.getRoot().getFileSystem().equals( fsystem ) ) {
            return systemRepository;
        }

        return null;
    }

    @Override
    public Repository getRepository( final Path root ) {
        return rootToRepo.get( root );
    }

    @Override
    public Collection<Repository> getRepositories() {
        return new ArrayList<Repository>( configuredRepositories.values() );
    }

    @Override
    public Repository createRepository( final OrganizationalUnit organizationalUnit,
                                        final String scheme,
                                        final String alias,
                                        final Map<String, Object> env ) throws RepositoryAlreadyExistsException {
        final Repository repository = createRepository( scheme, alias, env );
        if ( organizationalUnit != null ) {
            organizationalUnitService.addRepository( organizationalUnit, repository );
        }
        return repository;
    }

    @Override
    public Repository createRepository( final String scheme,
                                        final String alias,
                                        final Map<String, Object> env ) {

        if ( configuredRepositories.containsKey( alias ) || SystemRepository.SYSTEM_REPO.getAlias().equals( alias ) ) {
            throw new RepositoryAlreadyExistsException( alias );
        }
        final ConfigGroup repositoryConfig = configurationFactory.newConfigGroup( REPOSITORY, alias, "" );
        repositoryConfig.addConfigItem( configurationFactory.newConfigItem( "security:roles", new ArrayList<String>() ) );

        if ( !env.containsKey( SCHEME ) ) {
            repositoryConfig.addConfigItem( configurationFactory.newConfigItem( SCHEME, scheme ) );
        }
        for ( final Map.Entry<String, Object> entry : env.entrySet() ) {
            if ( entry.getKey().startsWith( "crypt:" ) ) {
                repositoryConfig.addConfigItem( configurationFactory.newSecuredConfigItem( entry.getKey(),
                                                                                           entry.getValue().toString() ) );
            } else {
                repositoryConfig.addConfigItem( configurationFactory.newConfigItem( entry.getKey(),
                                                                                    entry.getValue() ) );
            }
        }

        final Repository repo = createRepository( repositoryConfig );

        event.fire( new NewRepositoryEvent( repo ) );

        return repo;
    }

    //Save the definition
    private Repository createRepository( final ConfigGroup repositoryConfig ) {
        final Repository repository = repositoryFactory.newRepository( repositoryConfig );
        configurationService.addConfiguration( repositoryConfig );
        configuredRepositories.put( repository.getAlias(), repository );
        rootToRepo.put( repository.getRoot(), repository );
        return repository;
    }

    @Override
    public String normalizeRepositoryName( String name ) {
        return TextUtil.normalizeRepositoryName( name );
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void addRole( Repository repository,
                         String role ) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig( repository.getAlias() );

        if ( thisRepositoryConfig != null ) {
            final ConfigItem<List> roles = thisRepositoryConfig.getConfigItem( "security:roles" );
            roles.getValue().add( role );

            configurationService.updateConfiguration( thisRepositoryConfig );

            final Repository updatedRepo = repositoryFactory.newRepository( thisRepositoryConfig );
            configuredRepositories.put( updatedRepo.getAlias(), updatedRepo );
            rootToRepo.put( updatedRepo.getRoot(), updatedRepo );
        } else {
            throw new IllegalArgumentException( "Repository " + repository.getAlias() + " not found" );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void removeRole( Repository repository,
                            String role ) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig( repository.getAlias() );

        if ( thisRepositoryConfig != null ) {
            final ConfigItem<List> roles = thisRepositoryConfig.getConfigItem( "security:roles" );
            roles.getValue().remove( role );

            configurationService.updateConfiguration( thisRepositoryConfig );

            final Repository updatedRepo = repositoryFactory.newRepository( thisRepositoryConfig );
            configuredRepositories.put( updatedRepo.getAlias(), updatedRepo );
            rootToRepo.put( updatedRepo.getRoot(), updatedRepo );
        } else {
            throw new IllegalArgumentException( "Repository " + repository.getAlias() + " not found" );
        }
    }

    protected ConfigGroup findRepositoryConfig( final String alias ) {
        final Collection<ConfigGroup> groups = configurationService.getConfiguration( ConfigType.REPOSITORY );
        if ( groups != null ) {
            for ( ConfigGroup groupConfig : groups ) {
                if ( groupConfig.getName().equals( alias ) ) {
                    return groupConfig;
                }
            }
        }
        return null;
    }

    @Override
    public void removeRepository( String alias ) {
        final ConfigGroup thisRepositoryConfig = findRepositoryConfig( alias );

        if ( thisRepositoryConfig != null ) {
            configurationService.removeConfiguration( thisRepositoryConfig );
            final Repository repo = configuredRepositories.remove( alias );
            if ( repo != null ) {
                rootToRepo.remove( repo.getRoot() );
                ioService.delete( convert( repo.getRoot() ).getFileSystem().getPath( null ) );
            }
        }

        //Remove reference to Repository from Organizational Units
        final Collection<OrganizationalUnit> organizationalUnits = organizationalUnitService.getOrganizationalUnits();
        for ( OrganizationalUnit ou : organizationalUnits ) {
            for ( Repository repository : ou.getRepositories() ) {
                if ( repository.getAlias().equals( alias ) ) {
                    organizationalUnitService.removeRepository( ou,
                                                                repository );
                }
            }
        }
    }

    public RepositoryInfo getRepositoryInfo( final String alias ) {
        final Repository repo = getRepository( alias );
        String ouName = null;
        for ( final OrganizationalUnit ou : organizationalUnitService.getOrganizationalUnits() ) {
            for ( Repository repository : ou.getRepositories() ) {
                if ( repository.getAlias().equals( alias ) ) {
                    ouName = ou.getName();
                }
            }
        }

        final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( Paths.convert( repo.getRoot() ), VersionAttributeView.class );
        final List<VersionRecord> records = versionAttributeView.readAttributes().history().records();
        Collections.reverse( records );

        return new RepositoryInfo( alias, ouName, repo.getRoot(), repo.getPublicURIs(), new ArrayList<VersionRecord>( HISTORY_PAGE_SIZE ) {{
            int size = 0;
            for ( final VersionRecord record : records ) {
                add( new PortableVersionRecord( record.id(), record.author(), record.email(), record.comment(), record.date(), record.uri() ) );
                size++;
                if ( size > HISTORY_PAGE_SIZE ) {
                    break;
                }
            }
        }} );
    }

    @Override
    public List<VersionRecord> getRepositoryHistory( final String alias,
                                                     final int startIndex ) {
        final Repository repo = getRepository( alias );

        final VersionAttributeView versionAttributeView = ioService.getFileAttributeView( Paths.convert( repo.getRoot() ), VersionAttributeView.class );

        final List<VersionRecord> records = versionAttributeView.readAttributes().history().records();

        if ( records.size() <= startIndex ) {
            return Collections.emptyList();
        }

        Collections.reverse( records );
        final List<VersionRecord> result = new ArrayList<VersionRecord>( HISTORY_PAGE_SIZE );

        int size = 0;

        for ( final VersionRecord record : records.subList( startIndex, records.size() > startIndex + HISTORY_PAGE_SIZE ? startIndex + HISTORY_PAGE_SIZE : records.size() ) ) {
            result.add( new PortableVersionRecord( record.id(), record.author(), record.email(), record.comment(), record.date(), record.uri() ) );
            size++;
            if ( size > HISTORY_PAGE_SIZE ) {
                break;
            }
        }

        return result;
    }

    public void updateRegisteredRepositories(@Observes SystemRepositoryChangedEvent changedEvent) {
        configuredRepositories.clear();
        loadRepositories();
    }
}
