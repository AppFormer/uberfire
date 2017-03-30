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

package org.uberfire.client.mvp;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

import static java.util.Collections.sort;

/**
 *
 */
@ApplicationScoped
public class ActivityBeansCache {

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;

    @Inject
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent;

    /**
     * All active activity beans mapped by their CDI bean name (names are mandatory for activity beans).
     */
    private final Map<String, IOCBeanDef<Activity>> activitiesById = new HashMap<String, IOCBeanDef<Activity>>();

    /**
     * All active Activities that have an {@link AssociatedResources} annotation and are not splash screens.
     */
    private final List<ActivityAndMetaInfo> resourceActivities = new ArrayList<ActivityAndMetaInfo>();

    /**
     * All active activities that are splash screens.
     */
    private final List<SplashScreenActivity> splashActivities = new ArrayList<SplashScreenActivity>();

    @PostConstruct
    void init() {
        final Collection<IOCBeanDef<Activity>> availableActivities = getAvailableActivities();

        for ( final IOCBeanDef<Activity> baseBean : availableActivities ) {
            final IOCBeanDef<Activity> activityBean = reLookupBean( baseBean );

            final String id = activityBean.getName();

            if ( activitiesById.keySet().contains( id ) ) {
                throw new RuntimeException( "Conflict detected. Activity '" + id + "' already exists. " + activityBean.getBeanClass().toString() );
            }

            activitiesById.put( id, activityBean );

            if ( isSplashScreen( activityBean.getQualifiers() ) ) {
                splashActivities.add( (SplashScreenActivity) activityBean.getInstance() );
            } else {
                final Pair<Integer, List<String>> metaInfo = generateActivityMetaInfo( activityBean );
                if ( metaInfo != null ) {
                    getResourceActivities().add( new ActivityAndMetaInfo( activityBean, metaInfo.getK1(), metaInfo.getK2() ) );
                }
            }
        }

        sortResourceActivitiesByPriority();
    }

    /**
     * Returns all activities in this cache that have an associated resource type.
     */
    List<ActivityAndMetaInfo> getResourceActivities() {
        return resourceActivities;
    }

    void sortResourceActivitiesByPriority() {
        sort( getResourceActivities(), new Comparator<ActivityAndMetaInfo>() {
            @Override
            public int compare( final ActivityAndMetaInfo o1,
                                final ActivityAndMetaInfo o2 ) {

                if ( o1.getPriority() < o2.getPriority() ) {
                    return 1;
                } else if ( o1.getPriority() > o2.getPriority() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } );
    }

    /**
     * {porcelli} TODO workaround an Errai bug - it doesn't attach bean names when you lookup by a type that's not the concrete bean type.
     */
    IOCBeanDef<Activity> reLookupBean( IOCBeanDef<Activity> baseBean ) {
        return (IOCBeanDef<Activity>) iocManager.lookupBean( baseBean.getBeanClass() );
    }

    Collection<IOCBeanDef<Activity>> getAvailableActivities() {
        Collection<IOCBeanDef<Activity>> activeBeans = new ArrayList<IOCBeanDef<Activity>>();
        for ( IOCBeanDef<Activity> bean : iocManager.lookupBeans( Activity.class ) ) {
            if ( bean.isActivated() ) {
                activeBeans.add( bean );
            }
        }
        return activeBeans;
    }

    private boolean isSplashScreen( final Set<Annotation> qualifiers ) {
        for ( final Annotation qualifier : qualifiers ) {
            if ( qualifier instanceof IsSplashScreen ) {
                return true;
            }
        }
        return false;
    }

    public void removeActivity( String id ) {
        activitiesById.remove(id);
    }

    /** Used for runtime plugins. */
    public void addNewScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        newWorkbenchScreenEventEvent.fire( new NewWorkbenchScreenEvent( id ) );
    }

    /** Used for runtime plugins. */
    public void addNewPerspectiveActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        newPerspectiveEventEvent.fire( new NewPerspectiveEvent( id ) );
    }

    /** Used for runtime plugins. */
    public void addNewEditorActivity( final IOCBeanDef<Activity> activityBean,
                                      String priority,
                                      String resourceTypeName ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        resourceActivities.add( new ActivityAndMetaInfo(activityBean, Integer.valueOf(priority),
                                                        Arrays.asList(resourceTypeName) ) );
        sortResourceActivitiesByPriority();
    }

    public void addNewSplashScreenActivity( final IOCBeanDef<Activity> activityBean ) {
        final String id = activityBean.getName();

        if ( activitiesById.keySet().contains( id ) ) {
            throw new RuntimeException( "Conflict detected. Activity Id already exists. " + activityBean.getBeanClass().toString() );
        }

        activitiesById.put( id, activityBean );
        splashActivities.add( (SplashScreenActivity) activityBean.getInstance() );
    }

    /**
     * Returns all active splash screen activities in this cache.
     */
    public List<SplashScreenActivity> getSplashScreens() {
        return splashActivities;
    }

    /**
     * Returns the activity with the given CDI bean name from this cache, or null if there is no such activity or the
     * activity with the given name is not an activated bean.
     *
     * @param id
     *            the CDI name of the bean (see {@link Named}), or in the case of runtime plugins, the name the activity
     *            was registered under.
     */
    public IOCBeanDef<Activity> getActivity( final String id ) {
        return activitiesById.get( id );
    }

    /**
     * Returns the activated activity with the highest priority that can handle the given file. Returns null if no
     * activated activity can handle the path.
     *
     * @param path
     *            the file to find a path-based activity for (probably a {@link WorkbenchEditorActivity}, but this cache
     *            makes no guarantees).
     */
    public IOCBeanDef<Activity> getActivity( final Path path ) {

        for ( final ActivityAndMetaInfo currentActivity : getResourceActivities() ) {
            for ( final ClientResourceType resourceType : currentActivity.getResourceTypes() ) {
                if ( resourceType.accept( path ) ) {
                    return currentActivity.getActivityBean();
                }
            }
        }

        throw new EditorResourceTypeNotFound();
    }

    Pair<Integer, List<String>> generateActivityMetaInfo(IOCBeanDef<Activity> activityBean ) {
        return ActivityMetaInfo.generate( activityBean );
    }

    public List<String> getActivitiesById() {
      return new ArrayList<String>(activitiesById.keySet());
    }

    class ActivityAndMetaInfo {

        private final IOCBeanDef<Activity> activityBean;
        private final int priority;
        final List<String> resourceTypesNames;
        private  ClientResourceType[] resourceTypes;

        ActivityAndMetaInfo( final IOCBeanDef<Activity> activityBean,
                             final int priority,
                             final List<String> resourceTypesNames ) {
            this.activityBean = activityBean;
            this.priority = priority;
            this.resourceTypesNames = resourceTypesNames;
        }

        public IOCBeanDef<Activity> getActivityBean() {
            return activityBean;
        }

        public int getPriority() {
            return priority;
        }

        public ClientResourceType[] getResourceTypes() {
            if (resourceTypes == null) {
                dynamicLookupResourceTypes();
            }
            return resourceTypes;
        }

        //this lookup need to be dynamic, because we don't
        //want to force a js registration order (resource -> editor)
        private void dynamicLookupResourceTypes() {
            this.resourceTypes = new ClientResourceType[resourceTypesNames.size()];
            for (int i = 0; i < resourceTypesNames.size(); i++) {
                final String resourceTypeIdentifier = resourceTypesNames.get(i);
                final Collection<IOCBeanDef> resourceTypeBeans = iocManager.lookupBeans(resourceTypeIdentifier);
                if (isADynamicResource(resourceTypeBeans)) {
                    this.resourceTypes[i] = (ClientResourceType) resourceTypeBeans.iterator().next().getInstance();
                } else {
                    IOCBeanDef<ClientResourceType> resourceTypeClass = getResourceTypeClass(resourceTypeIdentifier);
                    if (resourceTypeClass == null) {
                        throw new RuntimeException("ClientResourceType " + resourceTypeIdentifier + " not found");
                    }
                    this.resourceTypes[i] = resourceTypeClass.getInstance();
                }
            }
        }

        private boolean isADynamicResource(Collection<IOCBeanDef> resourceTypeBeans) {
            return !resourceTypeBeans.isEmpty();
        }

        private IOCBeanDef<ClientResourceType> getResourceTypeClass(String resourceName) {

            Collection<IOCBeanDef<ClientResourceType>> iocBeanDefs = iocManager.lookupBeans(ClientResourceType.class);
            for (IOCBeanDef<ClientResourceType> iocBeanDef : iocBeanDefs) {
                String beanClassName = iocBeanDef.getBeanClass().getName();
                if (beanClassName.equalsIgnoreCase(resourceName)) {
                    return iocBeanDef;
                }
            }
            return null;
        }
    }

    private class EditorResourceTypeNotFound extends RuntimeException {

    }
}
