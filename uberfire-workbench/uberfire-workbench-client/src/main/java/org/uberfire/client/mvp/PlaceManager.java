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

import java.util.Collection;

import com.google.gwt.user.client.ui.HasWidgets;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.util.Layouts;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

/**
 * A Workbench-centric abstraction over the browser's history mechanism. Allows the application to initiate navigation
 * to any displayable thing: a {@link WorkbenchPerspective}, a {@link WorkbenchScreen}, a {@link WorkbenchPopup}, a
 * {@link WorkbenchEditor}, a {@link WorkbenchPart} within a screen or editor, or the editor associated with a VFS file
 * located at a particular {@link Path}.
 */
@JsType
public interface PlaceManager {

    @JsMethod(name = "goToId")
    void goTo(final String identifier);

    @JsMethod(name = "goToPlace")
    void goTo(final PlaceRequest place);

    @JsMethod(name = "goToPath")
    void goTo(final Path path);

    @JsMethod(name = "goToPathAndPlace")
    void goTo(final Path path,
              final PlaceRequest place);

    @JsMethod(name = "goToPartWithPanel")
    void goTo(final PartDefinition part,
              final PanelDefinition panel);

    @JsMethod(name = "goToIdWithPanel")
    void goTo(final String identifier,
              final PanelDefinition panel);

    @JsMethod(name = "goToPlaceWithPanel")
    void goTo(final PlaceRequest place,
              final PanelDefinition panel);

    @JsMethod(name = "goToPathWithPanel")
    void goTo(final Path path,
              final PanelDefinition panel);

    @JsMethod(name = "goToPathAndPlaceWithPanel")
    void goTo(final Path path,
              final PlaceRequest place,
              final PanelDefinition panel);

    /**
     * Locates the Activity associated with the given place, and if that activity is not already part of the workbench,
     * starts it and adds its view to the given widget container. If the activity is already part of the current
     * workbench, it will be selected, and it will not be moved from its current location.
     * <p>
     * The activity will be properly shut down in any of the following scenarios:
     * <ol>
     * <li>by a call to one of the PlaceManager methods for closing a place: {@link #closePlace(PlaceRequest)},
     * {@link #closePlace(String)}, or {@link #closeAllPlaces()}
     * <li>by switching to another perspective, which has the side effect of closing all places
     * <li>by removing the activity's view from the DOM, either using the GWT Widget API, or by direct DOM manipulation.
     * </ol>
     * @param place
     * @param addTo The container to add the widget's view to. Its corresponding DOM element must have a CSS
     * <tt>position</tt> setting of <tt>relative</tt> or <tt>absolute</tt> and an explicit size set. This can
     * be accomplished through direct use of CSS, or through the
     * {@link Layouts#setToFillParent(com.google.gwt.user.client.ui.Widget)} call.
     */
    @JsIgnore
    void goTo(final PlaceRequest place,
              final HasWidgets addTo);

    @JsIgnore
    void goTo(final PlaceRequest place,
              final HTMLElement addTo);

    /**
     * Finds the <i>currently open</i> activity that handles the given PlaceRequest by ID. No attempt is made to match
     * by path, but see {@link ActivityManagerImpl#resolveExistingParts(PlaceRequest)} for a variant that does.
     * (TODO: should this method care about paths? if not, should the other method be added to the interface?)
     * @param place the PlaceRequest whose activity to search for
     * @return the activity that currently exists in service of the given PlaceRequest's ID. Null if no current activity
     * handles the given PlaceRequest.
     */
    Activity getActivity(final PlaceRequest place);

    @JsMethod(name = "getStatusById")
    PlaceStatus getStatus(final String id);

    @JsMethod(name = "getStatusByPlaceRequest")
    PlaceStatus getStatus(final PlaceRequest place);

    default void executeOnOpenCallback(final PlaceRequest place) {
        checkNotNull("place",
                     place);

        final Command callback = getOpenCallback(place);
        if (callback != null) {
            callback.execute();
        }
    }

    public Command getOpenCallback(PlaceRequest place);

    @JsMethod(name = "closePlaceById")
    void closePlace(final String id);

    void closePlace(final PlaceRequest placeToClose);

    void tryClosePlace(final PlaceRequest placeToClose,
                       final Command onAfterClose);

    @JsMethod(name = "forceCloseById")
    void forceClosePlace(final String id);

    @JsMethod(name = "forceCloseByPlaceRequest")
    void forceClosePlace(final PlaceRequest place);

    void closeAllPlaces();

    void forceCloseAllPlaces();

    boolean closeAllPlacesOrNothing();

    void registerOnOpenCallback(final PlaceRequest place,
                                final Command command);

    void unregisterOnOpenCallback(final PlaceRequest place);

    @JsIgnore
    Collection<SplashScreenActivity> getActiveSplashScreens();

    /**
     * Finds the <i>currently open</i> PlaceRequests for Activities that handle the given ResourceTypeDefinition.
     * @param type the ResourceTypeDefinition whose activity to search for
     * @return an unmodifiable collection of PlaceRequests for the <i>currently open</i> WorkbenchEditorActivities that
     * can handle the ResourceTypeDefinition. Returns an empty collection if no match was found.
     */
    @JsIgnore
    Collection<PathPlaceRequest> getActivitiesForResourceType(final ResourceTypeDefinition type);
}
