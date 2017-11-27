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

package org.uberfire.client.workbench.panels.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.QualifierUtil;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ContextActivity;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.util.MockIOCBeanDef;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.CustomPanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlaceManagerTest {

    /**
     * Returned by the mock activityManager for the special "workbench.activity.notfound" place.
     */
    private final Activity notFoundActivity = mock(Activity.class);
    /**
     * The setup method makes this the current place.
     */
    private final PlaceRequest kansas = new DefaultPlaceRequest("kansas");
    /**
     * The setup method links this activity with the kansas PlaceRequest.
     */
    private final WorkbenchScreenActivity kansasActivity = mock(WorkbenchScreenActivity.class);
    /**
     * This panel will always be returned from panelManager.getRoot().
     */
    private final PanelDefinition rootPanel = new PanelDefinitionImpl(
            MultiListWorkbenchPanelPresenter.class.getName());
    @Mock
    PerspectiveActivity defaultPerspective;
    @Mock
    SyncBeanManager iocManager;
    @Mock
    UberfireDocks uberfireDock;
    @Mock
    Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;
    @Mock
    Event<ClosePlaceEvent> workbenchPartCloseEvent;
    @Mock
    Event<PlaceLostFocusEvent> workbenchPartLostFocusEvent;
    @Mock
    Event<NewSplashScreenActiveEvent> newSplashScreenActiveEvent;
    @Mock
    ActivityManager activityManager;
    @Mock
    PlaceHistoryHandler placeHistoryHandler;
    @Mock
    Event<SelectPlaceEvent> selectWorkbenchPartEvent;
    @Mock
    PanelManager panelManager;
    @Mock
    PerspectiveManager perspectiveManager;
    @Mock
    WorkbenchLayout workbenchLayout;
    @Mock
    LayoutSelection layoutSelection;
    @Mock
    PanelDefinition panelMock;
    /**
     * This is the thing we're testing. Weeee!
     */
    @InjectMocks
    PlaceManagerImpl placeManager;

    @Before
    public void setup() {
        ((SyncBeanManagerImpl) IOC.getBeanManager()).reset();

        when(placeHistoryHandler.getPerspectiveFromPlace(any())).then(AdditionalAnswers.returnsFirstArg());

        when(defaultPerspective.getIdentifier())
                .thenReturn("DefaultPerspective");
        when(defaultPerspective.isType(any(String.class)))
                .thenReturn(true);
        when(perspectiveManager.getCurrentPerspective())
                .thenReturn(defaultPerspective);

        when(activityManager.getActivities(any(PlaceRequest.class))).thenReturn(singleton(notFoundActivity));

        // for now (and this will have to change for UF-61), PathPlaceRequest performs an IOC lookup for ObservablePath in its constructor
        // as part of UF-61, we'll need to refactor ObservablePath and PathFactory so they ask for any beans they need as constructor params.
        final ObservablePath mockObservablePath = mock(ObservablePath.class);
        when(mockObservablePath.wrap(any(Path.class))).thenReturn(mockObservablePath);
        IOC.getBeanManager().registerBean(new MockIOCBeanDef<ObservablePath, ObservablePath>(mockObservablePath,
                                                                                             ObservablePath.class,
                                                                                             Dependent.class,
                                                                                             new HashSet<Annotation>(
                                                                                                     Arrays.asList(
                                                                                                             QualifierUtil.DEFAULT_QUALIFIERS)),
                                                                                             "ObservablePath",
                                                                                             true));

        // every test starts in Kansas, with no side effect interactions recorded
        when(activityManager.getActivities(kansas)).thenReturn(singleton(kansasActivity));
        setupPanelManagerMock();

        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(kansasActivity.isDynamic()).thenReturn(false);

        placeManager.goTo(kansas,
                          mock(PanelDefinition.class));
        resetInjectedMocks();
        reset(kansasActivity);

        when(kansasActivity.onMayClose()).thenReturn(true);
        when(kansasActivity.preferredWidth()).thenReturn(123);
        when(kansasActivity.preferredHeight()).thenReturn(456);

        when(placeHistoryHandler.getPerspectiveFromPlace(any()))
                .thenAnswer(i -> i.getArgument(0));

        // arrange for the mock PerspectiveManager to invoke the doWhenFinished callbacks
        doAnswer(new Answer<Void>() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ParameterizedCommand callback = (ParameterizedCommand) invocation.getArguments()[2];
                PerspectiveActivity perspectiveActivity = (PerspectiveActivity) invocation.getArguments()[1];
                callback.execute(perspectiveActivity.getDefaultPerspectiveLayout());
                return null;
            }
        }).when(perspectiveManager).switchToPerspective(any(PlaceRequest.class),
                                                        any(PerspectiveActivity.class),
                                                        any(ParameterizedCommand.class));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Command callback = (Command) invocation.getArguments()[0];
                callback.execute();
                return null;
            }
        }).when(perspectiveManager).savePerspectiveState(any(Command.class));
        doReturn(new DefaultPlaceRequest("lastPlaceRequest"))
                .when(defaultPerspective).getPlace();
        doReturn(defaultPerspective).when(perspectiveManager)
                .getCurrentPerspective();
    }

    /**
     * Resets all the mocks that were injected into the PlaceManager under test. This should probably only be used in
     * the setup method.
     */
    @SuppressWarnings("unchecked")
    private void resetInjectedMocks() {
        reset(workbenchPartBeforeCloseEvent);
        reset(workbenchPartCloseEvent);
        reset(workbenchPartLostFocusEvent);
        reset(newSplashScreenActiveEvent);
        reset(activityManager);
        reset(placeHistoryHandler);
        reset(selectWorkbenchPartEvent);
        reset(panelManager);
        reset(perspectiveManager);
        reset(workbenchLayout);

        setupPanelManagerMock();
    }

    private void setupPanelManagerMock() {
        when(panelManager.getRoot()).thenReturn(rootPanel);
        when(panelManager.addWorkbenchPanel(any(PanelDefinition.class),
                                            any(Position.class),
                                            any(Integer.class),
                                            any(Integer.class),
                                            any(Integer.class),
                                            any(Integer.class)))
                .thenAnswer(new Answer<PanelDefinition>() {
                    @Override
                    public PanelDefinition answer(InvocationOnMock invocation) throws Throwable {
                        return (PanelDefinition) invocation.getArguments()[0];
                    }
                });
        when(panelManager.addWorkbenchPanel(any(PanelDefinition.class),any(PanelDefinition.class), any(Position.class)))
                .thenAnswer(new Answer<PanelDefinition>() {
                    @Override
                    public PanelDefinition answer(InvocationOnMock invocation) throws Throwable {
                        return (PanelDefinition) invocation.getArguments()[0];
                    }
                });
    }

    @Test
    public void testPlaceManagerGetsInitializedToADefaultPlace() throws Exception {
        placeManager.initPlaceHistoryHandler();

        verify(placeHistoryHandler).initialize(any(PlaceManager.class),
                                             any(EventBus.class),
                                             any(PlaceRequest.class));
    }

    @Test
    public void testGoToConditionalPlaceById() throws Exception {

        PlaceRequest dora = new ConditionalPlaceRequest("dora").when(p -> true)
                .orElse(new DefaultPlaceRequest("other"));

        WorkbenchScreenActivity doraActivity = mock(WorkbenchScreenActivity.class);
        when(doraActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(activityManager.getActivities(dora)).thenReturn(singleton((Activity) doraActivity));

        placeManager.goTo(dora);

        verifyActivityLaunchSideEffects(dora,
                                        doraActivity,
                                        null);
    }

    @Test
    public void testGoToConditionalPlaceByIdOrElse() throws Exception {

        DefaultPlaceRequest other = new DefaultPlaceRequest("other");
        PlaceRequest dora = new ConditionalPlaceRequest("dora").when(p -> false)
                .orElse(other);

        WorkbenchScreenActivity doraActivity = mock(WorkbenchScreenActivity.class);
        WorkbenchScreenActivity otherActivity = mock(WorkbenchScreenActivity.class);
        when(doraActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(otherActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(activityManager.getActivities(dora)).thenReturn(singleton((Activity) doraActivity));
        when(activityManager.getActivities(other)).thenReturn(singleton((Activity) otherActivity));

        placeManager.goTo(dora);

        verify(doraActivity,
               never()).onOpen();
        verify(otherActivity).onOpen();

        verifyActivityLaunchSideEffects(other,
                                        otherActivity,
                                        null);
    }

    @Test
    public void testGoToNewPlaceById() throws Exception {
        PlaceRequest oz = new DefaultPlaceRequest("oz");
        WorkbenchScreenActivity ozActivity = mock(WorkbenchScreenActivity.class);
        when(ozActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(ozActivity.isDynamic()).thenReturn(false);
        when(ozActivity.preferredWidth()).thenReturn(-1);
        when(ozActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(oz)).thenReturn(singleton((Activity) ozActivity));


        placeManager.goTo(oz,
                          panelMock);

        verifyActivityLaunchSideEffects(oz,
                                        ozActivity,
                                        panelMock);
    }

    @Test
    public void testGoToPlaceWeAreAlreadyAt() throws Exception {
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        placeManager.goTo(kansas,
                          (PanelDefinition) null);

        // note "refEq" tests equality field by field using reflection. don't read it as "reference equals!" :)
        verify(selectWorkbenchPartEvent).fire(refEq(new SelectPlaceEvent(kansas)));

        verifyNoActivityLaunchSideEffects(kansas,
                                          kansasActivity);
    }

    @Test
    public void testGoToPartWeAreAlreadyAt() throws Exception {
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        placeManager.goTo(new PartDefinitionImpl(kansas),
                          null);

        // note "refEq" tests equality field by field using reflection. don't read it as "reference equals!" :)
        verify(selectWorkbenchPartEvent).fire(refEq(new SelectPlaceEvent(kansas)));

        verifyNoActivityLaunchSideEffects(kansas,
                                          kansasActivity);
    }

    @Test
    public void testGoToNowhereDoesNothing() throws Exception {
        placeManager.goTo(PlaceRequest.NOWHERE,
                          (PanelDefinition) null);

        verifyNoActivityLaunchSideEffects(kansas,
                                          kansasActivity);
    }

    // XXX would like to remove this behaviour (should throw NPE) but too many things are up in the air right now
    @Test
    public void testGoToNullDoesNothing() throws Exception {

        placeManager.goTo((PlaceRequest) null,
                          (PanelDefinition) null);

        verifyNoActivityLaunchSideEffects(kansas,
                                          kansasActivity);
    }

    @Test
    public void testGoToPlaceByPath() throws Exception {

        PathPlaceRequest yellowBrickRoad = new FakePathPlaceRequest(mock(ObservablePath.class));
        WorkbenchScreenActivity ozActivity = mock(WorkbenchScreenActivity.class);

        when(ozActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(activityManager.getActivities(yellowBrickRoad)).thenReturn(singleton((Activity) ozActivity));

        placeManager.goTo(yellowBrickRoad,
                          panelMock);

        verifyActivityLaunchSideEffects(yellowBrickRoad,
                                        ozActivity,
                                        panelMock);

        // special contract just for path-type place requests (subject to preference)
        verify(yellowBrickRoad.getPath(),
               never()).onDelete(any(Command.class));
    }

    @Test
    public void testNormalCloseExistingScreenActivity() throws Exception {
        when(kansasActivity.onMayClose()).thenReturn(true);
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.closePlace(kansas);

        verify(workbenchPartBeforeCloseEvent).fire(refEq(new BeforeClosePlaceEvent(kansas,
                                                                                   false,
                                                                                   true)));
        verify(workbenchPartCloseEvent).fire(refEq(new ClosePlaceEvent(kansas)));
        verify(kansasActivity).onMayClose();
        verify(kansasActivity).onClose();
        verify(kansasActivity,
               never()).onShutdown();
        verify(activityManager).destroyActivity(kansasActivity);
        verify(panelManager).removePartForPlace(kansas);

        assertEquals(PlaceStatus.CLOSE,
                     placeManager.getStatus(kansas));
        assertNull(placeManager.getActivity(kansas));
        assertFalse(placeManager.getActivePlaceRequests().contains(kansas));
    }

    @Test
    public void testCanceledCloseExistingScreenActivity() throws Exception {
        when(kansasActivity.onMayClose()).thenReturn(false);
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.closePlace(kansas);

        verify(workbenchPartBeforeCloseEvent).fire(refEq(new BeforeClosePlaceEvent(kansas,
                                                                                   false,
                                                                                   true)));
        verify(workbenchPartCloseEvent,
               never()).fire(refEq(new ClosePlaceEvent(kansas)));
        verify(kansasActivity).onMayClose();
        verify(kansasActivity,
               never()).onClose();
        verify(kansasActivity,
               never()).onShutdown();
        verify(activityManager,
               never()).destroyActivity(kansasActivity);
        verify(panelManager,
               never()).removePartForPlace(kansas);

        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(kansas));
        assertSame(kansasActivity,
                   placeManager.getActivity(kansas));
        assertTrue(placeManager.getActivePlaceRequests().contains(kansas));
    }

    @Test
    public void testForceCloseExistingScreenActivity() throws Exception {
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.forceClosePlace(kansas);

        verify(workbenchPartBeforeCloseEvent).fire(refEq(new BeforeClosePlaceEvent(kansas,
                                                                                   true,
                                                                                   true)));
        verify(workbenchPartCloseEvent).fire(refEq(new ClosePlaceEvent(kansas)));
        verify(kansasActivity,
               never()).onMayClose();
        verify(kansasActivity).onClose();
        verify(kansasActivity,
               never()).onShutdown();
        verify(activityManager).destroyActivity(kansasActivity);
        verify(panelManager).removePartForPlace(kansas);

        assertEquals(PlaceStatus.CLOSE,
                     placeManager.getStatus(kansas));
        assertNull(placeManager.getActivity(kansas));
        assertFalse(placeManager.getActivePlaceRequests().contains(kansas));
    }

    /**
     * Tests the basics of launching a perspective. We call it "empty" because this perspective doesn't have any panels
     * or parts in its definition.
     */
    @Test
    public void testLaunchingEmptyPerspective() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock(PerspectiveActivity.class);
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest("oz_perspective");
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when(activityManager.getActivities(ozPerspectivePlace))
                .thenReturn(singleton((Activity) ozPerspectiveActivity));
        when(ozPerspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(ozPerspectiveDef);
        when(ozPerspectiveActivity.getPlace()).thenReturn(ozPerspectivePlace);
        when(ozPerspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        placeManager.goTo(ozPerspectivePlace);

        // verify perspective changed to oz
        verify(perspectiveManager).savePerspectiveState(any(Command.class));
        verify(perspectiveManager).switchToPerspective(any(PlaceRequest.class),
                                                       eq(ozPerspectiveActivity),
                                                       any(ParameterizedCommand.class));
        verify(ozPerspectiveActivity).onOpen();
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(ozPerspectivePlace));
        assertTrue(placeManager.getActivePlaceRequests().contains(ozPerspectivePlace));
        assertEquals(ozPerspectiveActivity,
                     placeManager.getActivity(ozPerspectivePlace));
        verify(workbenchLayout).onResize();
    }

    @Test
    public void testSwitchingPerspectives() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock(PerspectiveActivity.class);
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest("oz_perspective");
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when(activityManager.getActivities(ozPerspectivePlace))
                .thenReturn(singleton((Activity) ozPerspectiveActivity));
        when(ozPerspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(ozPerspectiveDef);
        when(ozPerspectiveActivity.getPlace()).thenReturn(ozPerspectivePlace);
        when(ozPerspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        // we'll pretend we started in kansas
        PerspectiveActivity kansasPerspectiveActivity = mock(PerspectiveActivity.class);
        when(perspectiveManager.getCurrentPerspective()).thenReturn(kansasPerspectiveActivity);

        placeManager.goTo(ozPerspectivePlace);

        // verify proper shutdown of kansasPerspective and its contents
        InOrder inOrder = inOrder(activityManager,
                                  kansasPerspectiveActivity,
                                  kansasActivity,
                                  workbenchLayout);

        // shut down the screens first
        inOrder.verify(kansasActivity).onClose();
        inOrder.verify(activityManager).destroyActivity(kansasActivity);

        // then the perspective
        inOrder.verify(kansasPerspectiveActivity).onClose();
        inOrder.verify(activityManager).destroyActivity(kansasPerspectiveActivity);
        inOrder.verify(workbenchLayout).onResize();
    }

    @Test
    public void testSwitchingFromPerspectiveToSelf() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock(PerspectiveActivity.class);
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest("oz_perspective");
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when(activityManager.getActivities(ozPerspectivePlace))
                .thenReturn(singleton((Activity) ozPerspectiveActivity));
        when(ozPerspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(ozPerspectiveDef);
        when(ozPerspectiveActivity.getPlace()).thenReturn(ozPerspectivePlace);

        // we'll pretend we started in oz
        when(perspectiveManager.getCurrentPerspective()).thenReturn(ozPerspectiveActivity);

        placeManager.goTo(ozPerspectivePlace);

        // verify no side effects (should stay put)
        verify(ozPerspectiveActivity,
               never()).onOpen();
        verify(perspectiveManager,
               never()).savePerspectiveState(any(Command.class));
        verify(perspectiveManager,
               never())
                .switchToPerspective(any(PlaceRequest.class),
                                     any(PerspectiveActivity.class),
                                     any(ParameterizedCommand.class));
    }

    /**
     * This test verifies that when launching a screen which is "owned by" a perspective other than the current one, the
     * PlaceManager first switches to the owning perspective and then launches the requested screen.
     */
    @Test
    public void testLaunchingActivityTiedToDifferentPerspective() throws Exception {
        PerspectiveActivity ozPerspectiveActivity = mock(PerspectiveActivity.class);
        PlaceRequest ozPerspectivePlace = new DefaultPlaceRequest("oz_perspective");
        PerspectiveDefinition ozPerspectiveDef = new PerspectiveDefinitionImpl();

        when(activityManager.getActivities(ozPerspectivePlace))
                .thenReturn(singleton((Activity) ozPerspectiveActivity));
        when(ozPerspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(ozPerspectiveDef);
        when(ozPerspectiveActivity.getPlace()).thenReturn(ozPerspectivePlace);
        when(ozPerspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = mock(WorkbenchScreenActivity.class);
        when(activityManager.getActivities(emeraldCityPlace))
                .thenReturn(singleton((Activity) emeraldCityActivity));
        when(emeraldCityActivity.getOwningPlace()).thenReturn(ozPerspectivePlace);
        when(emeraldCityActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.goTo(emeraldCityPlace,
                          panelMock);

        // verify perspective changed to oz
        verify(perspectiveManager).savePerspectiveState(any(Command.class));
        verify(perspectiveManager).switchToPerspective(any(PlaceRequest.class),
                                                       eq(ozPerspectiveActivity),
                                                       any(ParameterizedCommand.class));
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(ozPerspectivePlace));

        // verify perspective opened before the activity that launches inside it
        InOrder inOrder = inOrder(ozPerspectiveActivity,
                                  emeraldCityActivity);
        inOrder.verify(ozPerspectiveActivity).onOpen();
        inOrder.verify(emeraldCityActivity).onOpen();

        // and the workbench activity should have launched (after the perspective change)
        verifyActivityLaunchSideEffects(emeraldCityPlace,
                                        emeraldCityActivity,
                                        panelMock);
    }

    @Test
    public void testPerspectiveLaunchWithSplashScreen() throws Exception {
        final PlaceRequest perspectivePlace = new DefaultPlaceRequest("Somewhere");
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        final PerspectiveDefinition perspectiveDef = new PerspectiveDefinitionImpl(
                SimpleWorkbenchPanelPresenter.class.getName());
        when(perspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(perspectiveDef);
        when(activityManager.getActivities(perspectivePlace))
                .thenReturn(singleton((Activity) perspectiveActivity));

        final SplashScreenActivity splashScreenActivity = mock(SplashScreenActivity.class);
        when(activityManager.getSplashScreenInterceptor(perspectivePlace)).thenReturn(splashScreenActivity);
        when(perspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(splashScreenActivity.isType(ActivityResourceType.SPLASH.name())).thenReturn(true);

        placeManager.goTo(perspectivePlace);

        // splash screen should be open and registered as an active splash screen
        verify(splashScreenActivity,
               never()).onStartup(any(PlaceRequest.class));

        InOrder inOrder = inOrder(splashScreenActivity,
                                  newSplashScreenActiveEvent);
        inOrder.verify(splashScreenActivity).onOpen();
        inOrder.verify(newSplashScreenActiveEvent).fire(any(NewSplashScreenActiveEvent.class));

        assertTrue(placeManager.getActiveSplashScreens().contains(splashScreenActivity));

        // perspective should be open, and should be the activity registered for its own place
        verify(perspectiveActivity,
               never()).onStartup(any(PlaceRequest.class));
        verify(perspectiveActivity).onOpen();
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(perspectivePlace));
        assertSame(perspectiveActivity,
                   placeManager.getActivity(perspectivePlace));
    }

    @Test
    public void testProperSplashScreenShutdownOnPerspectiveSwitch() throws Exception {
        final PlaceRequest perspectivePlace = new DefaultPlaceRequest("Somewhere");
        final PerspectiveActivity perspectiveActivity = mock(PerspectiveActivity.class);
        final PerspectiveDefinition perspectiveDef = new PerspectiveDefinitionImpl(
                SimpleWorkbenchPanelPresenter.class.getName());
        when(perspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(perspectiveDef);
        when(perspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(activityManager.getActivities(perspectivePlace))
                .thenReturn(singleton((Activity) perspectiveActivity));

        // first splash screen: linked to the perspective itself
        final SplashScreenActivity splashScreenActivity1 = mock(SplashScreenActivity.class);
        when(activityManager.getSplashScreenInterceptor(perspectivePlace)).thenReturn(splashScreenActivity1);
        when(splashScreenActivity1.isType(ActivityResourceType.SPLASH.name())).thenReturn(true);

        // second splash screen: linked to a screen that we will display in the perspective
        final SplashScreenActivity splashScreenActivity2 = mock(SplashScreenActivity.class);
        when(activityManager.getSplashScreenInterceptor(kansas)).thenReturn(splashScreenActivity2);
        when(activityManager.getActivities(kansas)).thenReturn(singleton((Activity) kansasActivity));
        when(splashScreenActivity2.isType(ActivityResourceType.SPLASH.name())).thenReturn(true);
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.goTo(perspectivePlace);
        placeManager.goTo(kansas);

        assertTrue(placeManager.getActiveSplashScreens().contains(splashScreenActivity1));
        assertTrue(placeManager.getActiveSplashScreens().contains(splashScreenActivity2));

        // now switch to another perspective and ensure both kinds of splash screens got closed
        final PlaceRequest otherPerspectivePlace = new DefaultPlaceRequest("Elsewhere");
        final PerspectiveActivity otherPerspectiveActivity = mock(PerspectiveActivity.class);
        final PerspectiveDefinition otherPerspectiveDef = new PerspectiveDefinitionImpl(
                SimpleWorkbenchPanelPresenter.class.getName());
        when(otherPerspectiveActivity.getDefaultPerspectiveLayout()).thenReturn(otherPerspectiveDef);
        when(otherPerspectiveActivity.isType(ActivityResourceType.PERSPECTIVE.name())).thenReturn(true);
        when(activityManager.getActivities(otherPerspectivePlace))
                .thenReturn(singleton((Activity) otherPerspectiveActivity));

        placeManager.goTo(otherPerspectivePlace);

        assertTrue(placeManager.getActiveSplashScreens().isEmpty());
        verify(splashScreenActivity1).closeIfOpen();
        verify(splashScreenActivity2).closeIfOpen();

        // splash screens are Application Scoped, but we still "destroy" them (activity manager will call their onShutdown)
        verify(activityManager).destroyActivity(splashScreenActivity1);
        verify(activityManager).destroyActivity(splashScreenActivity2);
    }

    @Test
    public void testPartLaunchWithSplashScreen() throws Exception {
        PlaceRequest oz = new DefaultPlaceRequest("oz");
        WorkbenchScreenActivity ozActivity = mock(WorkbenchScreenActivity.class);
        when(activityManager.getActivities(oz)).thenReturn(singleton((Activity) ozActivity));
        when(ozActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        final SplashScreenActivity lollipopGuildActivity = mock(SplashScreenActivity.class);
        when(activityManager.getSplashScreenInterceptor(oz)).thenReturn(lollipopGuildActivity);
        when(lollipopGuildActivity.isType(ActivityResourceType.SPLASH.name())).thenReturn(true);

        placeManager.goTo(oz,
                          panelMock);

        assertTrue(placeManager.getActiveSplashScreens().contains(lollipopGuildActivity));
        verify(lollipopGuildActivity,
               never()).onStartup(any(PlaceRequest.class));

        InOrder inOrder = inOrder(lollipopGuildActivity,
                                  newSplashScreenActiveEvent);
        inOrder.verify(lollipopGuildActivity).onOpen();
        inOrder.verify(newSplashScreenActiveEvent).fire(any(NewSplashScreenActiveEvent.class));
    }

    @Test
    public void testProperSplashScreenShutdownOnPartClose() throws Exception {
        PlaceRequest oz = new DefaultPlaceRequest("oz");
        WorkbenchScreenActivity ozActivity = mock(WorkbenchScreenActivity.class);
        when(activityManager.getActivities(oz)).thenReturn(singleton((Activity) ozActivity));

        final SplashScreenActivity lollipopGuildActivity = mock(SplashScreenActivity.class);
        when(lollipopGuildActivity.isType(ActivityResourceType.SPLASH.name())).thenReturn(true);
        when(activityManager.getSplashScreenInterceptor(oz)).thenReturn(lollipopGuildActivity);
        when(ozActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.goTo(oz,
                          panelMock);
        placeManager.closePlace(oz);

        assertTrue(placeManager.getActiveSplashScreens().isEmpty());
        verify(lollipopGuildActivity).closeIfOpen();

        // splash screens are Application Scoped, but we still "destroy" them (activity manager will call their onShutdown)
        verify(activityManager).destroyActivity(lollipopGuildActivity);
    }

    /**
     * Ensures that splash screens can't be launched on their own (they should only launch as a side effect of launching
     * a place that they intercept). This test came from the original test suite, and may not be all that relevant
     * anymore: it assumes that the ActivityManager might resolve a PlaceRequest to a SplashScreenActivity, and this is
     * currently not in the ActivityManager contract.
     */
    @Test
    public void testSplashScreenActivityShouldNotLaunchOnItsOwn() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest("Somewhere");

        final SplashScreenActivity splashScreenActivity = mock(SplashScreenActivity.class);
        when(activityManager.getActivities(somewhere)).thenReturn(singleton((Activity) splashScreenActivity));

        placeManager.goTo(somewhere);

        verify(splashScreenActivity,
               never()).onStartup(eq(somewhere));
        verify(splashScreenActivity,
               never()).onOpen();
        verify(newSplashScreenActiveEvent,
               never()).fire(any(NewSplashScreenActiveEvent.class));
        assertFalse(placeManager.getActiveSplashScreens().contains(splashScreenActivity));
    }

    /**
     * Ensures that context activities can't be launched on their own (they should only launch as a side effect of launching
     * a place that they relate to). This test was moved here from the original test suite.
     */
    @Test
    public void testContextActivityShouldNotLaunchOnItsOwn() throws Exception {
        final PlaceRequest somewhere = new DefaultPlaceRequest("Somewhere");

        final ContextActivity activity = mock(ContextActivity.class);
        when(activityManager.getActivities(somewhere)).thenReturn(singleton((Activity) activity));

        placeManager.goTo(somewhere);

        verify(activity,
               never()).onStartup(eq(somewhere));
        verify(activity,
               never()).onOpen();
    }

    @Test
    public void testLaunchingPopup() throws Exception {

        final PlaceRequest popupPlace = new DefaultPlaceRequest("Somewhere");
        final AbstractPopupActivity popupActivity = mock(AbstractPopupActivity.class);

        when(activityManager.getActivities(popupPlace)).thenReturn(singleton((Activity) popupActivity));
        when(popupActivity.isType(ActivityResourceType.POPUP.name())).thenReturn(true);

        placeManager.goTo(popupPlace);

        verify(popupActivity,
               never()).onStartup(any(PlaceRequest.class));
        verify(popupActivity,
               times(1)).onOpen();

        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(popupPlace));

        // TODO this test was moved here from the old test suite. it may not verify all required side effects of launching a popup.
    }

    @Test
    public void testLaunchingPopupThatIsAlreadyOpen() throws Exception {

        final PlaceRequest popupPlace = new DefaultPlaceRequest("Somewhere");
        final AbstractPopupActivity popupActivity = mock(AbstractPopupActivity.class);

        when(activityManager.getActivities(popupPlace)).thenReturn(singleton((Activity) popupActivity));
        when(popupActivity.isType(ActivityResourceType.POPUP.name())).thenReturn(true);

        placeManager.goTo(popupPlace);
        placeManager.goTo(popupPlace);

        verify(popupActivity,
               never()).onStartup(any(PlaceRequest.class));
        verify(popupActivity,
               times(1)).onOpen();
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(popupPlace));
    }

    @Test
    public void testReLaunchingClosedPopup() throws Exception {

        final PlaceRequest popupPlace = new DefaultPlaceRequest("Somewhere");
        final AbstractPopupActivity popupActivity = mock(AbstractPopupActivity.class);
        when(popupActivity.onMayClose()).thenReturn(true);
        when(popupActivity.isType(ActivityResourceType.POPUP.name())).thenReturn(true);
        when(activityManager.getActivities(popupPlace)).thenReturn(singleton((Activity) popupActivity));

        placeManager.goTo(popupPlace);
        placeManager.closePlace(popupPlace);
        placeManager.goTo(popupPlace);

        verify(popupActivity,
               times(2)).onOpen();
        verify(popupActivity,
               times(1)).onClose();
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(popupPlace));
    }

    @Test
    public void testPopupCancelsClose() throws Exception {

        final PlaceRequest popupPlace = new DefaultPlaceRequest("Somewhere");
        final AbstractPopupActivity popupActivity = mock(AbstractPopupActivity.class);
        when(popupActivity.onMayClose()).thenReturn(false);
        when(popupActivity.isType(ActivityResourceType.POPUP.name())).thenReturn(true);
        when(activityManager.getActivities(popupPlace)).thenReturn(singleton((Activity) popupActivity));

        placeManager.goTo(popupPlace);
        placeManager.closePlace(popupPlace);

        verify(popupActivity,
               never()).onClose();
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(popupPlace));
    }

    @Test
    public void testLaunchActivityInCustomPanel() throws Exception {
        HasWidgets any = any(HasWidgets.class);
        CustomPanelDefinitionImpl customPanelDef = new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                any);
        when(panelManager.addCustomPanel(any,
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(emeraldCityPlace))
                .thenReturn(singleton((Activity) emeraldCityActivity));
        when(emeraldCityActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        HasWidgets customContainer = mock(HasWidgets.class);

        placeManager.goTo(emeraldCityPlace,
                          customContainer);

        verifyActivityLaunchSideEffects(emeraldCityPlace,
                                        emeraldCityActivity,
                                        customPanelDef);
        verify(panelManager).addWorkbenchPart(eq(emeraldCityPlace),
                                              eq(new PartDefinitionImpl(emeraldCityPlace)),
                                              eq(customPanelDef),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              isNull(Integer.class),
                                              isNull(Integer.class));
        assertNull(customPanelDef.getParent());
    }

    @Test
    public void testLaunchActivityInCustomHasWidgetsPanelShouldCloseExistingOnes() throws Exception {
        PlaceManagerImpl placeManagerSpy = spy(this.placeManager);
        HasWidgets panel = mock(HasWidgets.class);
        CustomPanelDefinitionImpl customPanelDef = spy(new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                panel));
        when(panelManager.addCustomPanel(eq(panel),
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(emeraldCityPlace))
                .thenReturn(singleton((Activity) emeraldCityActivity));
        when(emeraldCityActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        PlaceRequest emeraldCityPlace2 = new DefaultPlaceRequest("emerald_city2");
        WorkbenchScreenActivity emeraldCityActivity2 = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(emeraldCityPlace2))
                .thenReturn(singleton((Activity) emeraldCityActivity2));
        when(emeraldCityActivity2.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManagerSpy.goTo(emeraldCityPlace,
                               panel);

        verifyActivityLaunchSideEffects(emeraldCityPlace,
                                        emeraldCityActivity,
                                        customPanelDef);
        verify(panelManager).addWorkbenchPart(eq(emeraldCityPlace),
                                              eq(new PartDefinitionImpl(emeraldCityPlace)),
                                              eq(customPanelDef),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              isNull(Integer.class),
                                              isNull(Integer.class));
        assertNull(customPanelDef.getParent());

        Set<PartDefinition> parts = new HashSet<>();
        PartDefinition part = mock(PartDefinition.class);
        parts.add(part);
        when(part.getPlace()).thenReturn(emeraldCityPlace);
        when(customPanelDef.getParts()).thenReturn(parts);

        placeManagerSpy.goTo(emeraldCityPlace2,
                               panel);

        verifyActivityLaunchSideEffects(emeraldCityPlace2,
                                        emeraldCityActivity2,
                                        customPanelDef);

        verify(panelManager).addWorkbenchPart(eq(emeraldCityPlace2),
                                              eq(new PartDefinitionImpl(emeraldCityPlace2)),
                                              eq(customPanelDef),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              isNull(Integer.class),
                                              isNull(Integer.class));

        verify(placeManagerSpy).closePlace(emeraldCityPlace);

        assertNull(customPanelDef.getParent());
    }

    @Test
    public void testLaunchActivityInCustomHTMLElementPanelShouldCloseExistingOnes() throws Exception {
        PlaceManagerImpl placeManagerSpy = spy(this.placeManager);
        HTMLElement panel = mock(HTMLElement.class);
        CustomPanelDefinitionImpl customPanelDef = spy(new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                panel));
        when(panelManager.addCustomPanel(eq(panel),
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(emeraldCityPlace))
                .thenReturn(singleton((Activity) emeraldCityActivity));
        when(emeraldCityActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        PlaceRequest emeraldCityPlace2 = new DefaultPlaceRequest("emerald_city2");
        WorkbenchScreenActivity emeraldCityActivity2 = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(emeraldCityPlace2))
                .thenReturn(singleton((Activity) emeraldCityActivity2));
        when(emeraldCityActivity2.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManagerSpy.goTo(emeraldCityPlace,
                             panel);

        verifyActivityLaunchSideEffects(emeraldCityPlace,
                                        emeraldCityActivity,
                                        customPanelDef);
        verify(panelManager).addWorkbenchPart(eq(emeraldCityPlace),
                                              eq(new PartDefinitionImpl(emeraldCityPlace)),
                                              eq(customPanelDef),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              isNull(Integer.class),
                                              isNull(Integer.class));
        assertNull(customPanelDef.getParent());

        Set<PartDefinition> parts = new HashSet<>();
        PartDefinition part = mock(PartDefinition.class);
        parts.add(part);
        when(part.getPlace()).thenReturn(emeraldCityPlace);
        when(customPanelDef.getParts()).thenReturn(parts);

        placeManagerSpy.goTo(emeraldCityPlace2,
                             panel);

        verifyActivityLaunchSideEffects(emeraldCityPlace2,
                                        emeraldCityActivity2,
                                        customPanelDef);

        verify(panelManager).addWorkbenchPart(eq(emeraldCityPlace2),
                                              eq(new PartDefinitionImpl(emeraldCityPlace2)),
                                              eq(customPanelDef),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              isNull(Integer.class),
                                              isNull(Integer.class));

        verify(placeManagerSpy).closePlace(emeraldCityPlace);

        assertNull(customPanelDef.getParent());
    }

    @Test
    public void testLaunchExistingActivityInCustomPanel() throws Exception {
        HasWidgets customContainer = mock(HasWidgets.class);

        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        placeManager.goTo(kansas,
                          customContainer);

        verify(panelManager,
               never())
                .addCustomPanel(customContainer,
                                StaticWorkbenchPanelPresenter.class.getName());
        verifyNoActivityLaunchSideEffects(kansas,
                                          kansasActivity);
        verify(selectWorkbenchPartEvent).fire(refEq(new SelectPlaceEvent(kansas)));
    }

    @Test
    public void testClosingActivityInCustomPanel() throws Exception {
        HasWidgets any = any(HasWidgets.class);
        CustomPanelDefinitionImpl customPanelDef = new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                any);
        when(panelManager.addCustomPanel(any,
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        createWorkbenchScreenActivity(emeraldCityPlace);

        HasWidgets customContainer = mock(HasWidgets.class);

        placeManager.goTo(emeraldCityPlace,
                          customContainer);
        placeManager.closePlace(emeraldCityPlace);

        assertTrue(customPanelDef.getParts().isEmpty());
        verify(panelManager).removeWorkbenchPanel(customPanelDef);
    }

    @Test
    public void testClosingAllPlacesIncludesCustomPanels() throws Exception {
        HasWidgets any = any(HasWidgets.class);
        CustomPanelDefinitionImpl customPanelDef = new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                any);
        when(panelManager.addCustomPanel(any,
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        createWorkbenchScreenActivity(emeraldCityPlace);

        HasWidgets customContainer = mock(HasWidgets.class);

        placeManager.goTo(emeraldCityPlace,
                          customContainer);
        placeManager.closeAllPlaces();

        assertTrue(customPanelDef.getParts().isEmpty());
        verify(panelManager).removeWorkbenchPanel(customPanelDef);
    }

    @Test
    public void testLaunchActivityInCustomPanelInsideHTMLElement() throws Exception {
        HTMLElement any = any(HTMLElement.class);
        CustomPanelDefinitionImpl customPanelDef = new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                any);
        when(panelManager.addCustomPanel(any,
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(activityManager.getActivities(emeraldCityPlace))
                .thenReturn(singleton((Activity) emeraldCityActivity));
        when(emeraldCityActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        HTMLElement customContainer = mock(HTMLElement.class);

        placeManager.goTo(emeraldCityPlace,
                          customContainer);

        verifyActivityLaunchSideEffects(emeraldCityPlace,
                                        emeraldCityActivity,
                                        customPanelDef);
        verify(panelManager).addWorkbenchPart(eq(emeraldCityPlace),
                                              eq(new PartDefinitionImpl(emeraldCityPlace)),
                                              eq(customPanelDef),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              isNull(Integer.class),
                                              isNull(Integer.class));
        assertNull(customPanelDef.getParent());
    }

    @Test
    public void testLaunchExistingActivityInCustomPanelInsideHTMLElement() throws Exception {
        HTMLElement customContainer = mock(HTMLElement.class);

        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        placeManager.goTo(kansas,
                          customContainer);

        verify(panelManager,
               never())
                .addCustomPanel(customContainer,
                                StaticWorkbenchPanelPresenter.class.getName());
        verifyNoActivityLaunchSideEffects(kansas,
                                          kansasActivity);
        verify(selectWorkbenchPartEvent).fire(refEq(new SelectPlaceEvent(kansas)));
    }

    @Test
    public void testClosingActivityInCustomPanelInsideHTMLElement() throws Exception {
        HTMLElement any = any(HTMLElement.class);
        CustomPanelDefinitionImpl customPanelDef = new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                any);
        when(panelManager.addCustomPanel(any,
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        createWorkbenchScreenActivity(emeraldCityPlace);

        HTMLElement customContainer = mock(HTMLElement.class);

        placeManager.goTo(emeraldCityPlace,
                          customContainer);
        placeManager.closePlace(emeraldCityPlace);

        assertTrue(customPanelDef.getParts().isEmpty());
        verify(panelManager).removeWorkbenchPanel(customPanelDef);
    }

    @Test
    public void testClosingAllPlacesIncludesCustomPanelsInsideHTMLElements() throws Exception {
        HTMLElement any = any(HTMLElement.class);
        CustomPanelDefinitionImpl customPanelDef = new CustomPanelDefinitionImpl(
                UnanchoredStaticWorkbenchPanelPresenter.class.getName(),
                any);
        when(panelManager.addCustomPanel(any,
                                         eq(UnanchoredStaticWorkbenchPanelPresenter.class.getName())))
                .thenReturn(customPanelDef);

        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        createWorkbenchScreenActivity(emeraldCityPlace);

        HTMLElement customContainer = mock(HTMLElement.class);

        placeManager.goTo(emeraldCityPlace,
                          customContainer);
        placeManager.closeAllPlaces();

        assertTrue(customPanelDef.getParts().isEmpty());
        verify(panelManager).removeWorkbenchPanel(customPanelDef);
    }

    @Test
    public void testGetActivitiesForResourceType_NoMatches() throws Exception {
        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest yellowBrickRoad = new FakePathPlaceRequest(path);
        final WorkbenchScreenActivity ozActivity = mock(WorkbenchScreenActivity.class);

        when(activityManager.getActivities(yellowBrickRoad)).thenReturn(singleton((Activity) ozActivity));
        when(ozActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.goTo(yellowBrickRoad);

        verifyActivityLaunchSideEffects(yellowBrickRoad,
                                        ozActivity,
                                        null);

        final ResourceTypeDefinition resourceType = mock(ResourceTypeDefinition.class);
        when(resourceType.accept(path)).thenReturn(false);

        final Collection<PathPlaceRequest> resolvedActivities = placeManager
                .getActivitiesForResourceType(resourceType);
        assertNotNull(resolvedActivities);
        assertEquals(0,
                     resolvedActivities.size());
    }

    @Test
    public void testGetActivitiesForResourceType_Matches() throws Exception {
        final ObservablePath path = mock(ObservablePath.class);
        final PathPlaceRequest yellowBrickRoad = new FakePathPlaceRequest(path);
        final WorkbenchScreenActivity ozActivity = mock(WorkbenchScreenActivity.class);

        when(activityManager.getActivities(yellowBrickRoad)).thenReturn(singleton((Activity) ozActivity));
        when(ozActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.goTo(yellowBrickRoad);

        verifyActivityLaunchSideEffects(yellowBrickRoad,
                                        ozActivity,
                                        panelMock);

        final ResourceTypeDefinition resourceType = mock(ResourceTypeDefinition.class);
        when(resourceType.accept(path)).thenReturn(true);

        final Collection<PathPlaceRequest> resolvedActivities = placeManager
                .getActivitiesForResourceType(resourceType);
        assertNotNull(resolvedActivities);
        assertEquals(1,
                     resolvedActivities.size());

        try {
            resolvedActivities.clear();

            fail("PlaceManager.getActivitiesForResourceType() should return an unmodifiable collection.");
        } catch (UnsupportedOperationException uoe) {
            //This is correct. The result should be an unmodifiable collection
        }
    }

    @Test
    public void testCloseAllPlacesOrNothingSucceeds() throws Exception {
        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = createWorkbenchScreenActivity(emeraldCityPlace);
        placeManager.goTo(emeraldCityPlace);

        when(kansasActivity.onMayClose()).thenReturn(true);
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.closeAllPlacesOrNothing();

        verifyPlaceClosed(kansas,
                          kansasActivity);
        verifyPlaceClosed(emeraldCityPlace,
                          emeraldCityActivity);
    }

    @Test
    public void testCloseAllPlacesOrNothingFails() throws Exception {
        PlaceRequest emeraldCityPlace = new DefaultPlaceRequest("emerald_city");
        WorkbenchScreenActivity emeraldCityActivity = createWorkbenchScreenActivity(emeraldCityPlace);
        doReturn(false).when(emeraldCityActivity).onMayClose();
        placeManager.goTo(emeraldCityPlace);

        when(kansasActivity.onMayClose()).thenReturn(true);
        when(kansasActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);

        placeManager.closeAllPlacesOrNothing();

        verifyPlaceNotClosed(kansas,
                             kansasActivity);
        verifyPlaceNotClosed(emeraldCityPlace,
                             emeraldCityActivity);
    }

    private WorkbenchScreenActivity createWorkbenchScreenActivity(final PlaceRequest emeraldCityPlace) {
        WorkbenchScreenActivity emeraldCityActivity = mock(WorkbenchScreenActivity.class);
        when(emeraldCityActivity.onMayClose()).thenReturn(true);
        when(emeraldCityActivity.preferredWidth()).thenReturn(555);
        when(emeraldCityActivity.preferredHeight()).thenReturn(-1);
        when(emeraldCityActivity.isType(ActivityResourceType.SCREEN.name())).thenReturn(true);
        when(activityManager.getActivities(emeraldCityPlace))
                .thenReturn(singleton((Activity) emeraldCityActivity));
        return emeraldCityActivity;
    }

    private void verifyPlaceClosed(final PlaceRequest place,
                                   final WorkbenchScreenActivity screenActivity) {
        verify(workbenchPartBeforeCloseEvent).fire(refEq(new BeforeClosePlaceEvent(place,
                                                                                   true,
                                                                                   true)));
        verify(workbenchPartCloseEvent).fire(refEq(new ClosePlaceEvent(place)));
        verify(screenActivity).onMayClose();
        verify(screenActivity).onClose();
        verify(screenActivity,
               never()).onShutdown();
        verify(activityManager).destroyActivity(screenActivity);
        verify(panelManager).removePartForPlace(place);

        assertEquals(PlaceStatus.CLOSE,
                     placeManager.getStatus(place));
        assertNull(placeManager.getActivity(place));
        assertFalse(placeManager.getActivePlaceRequests().contains(place));
    }

    private void verifyPlaceNotClosed(final PlaceRequest place,
                                      final WorkbenchScreenActivity screenActivity) {
        verify(workbenchPartBeforeCloseEvent,
               never()).fire(refEq(new BeforeClosePlaceEvent(place,
                                                             true,
                                                             true)));
        verify(workbenchPartCloseEvent,
               never()).fire(refEq(new ClosePlaceEvent(place)));
        verify(screenActivity,
               never()).onClose();
        verify(screenActivity,
               never()).onShutdown();
        verify(activityManager,
               never()).destroyActivity(screenActivity);
        verify(panelManager,
               never()).removePartForPlace(place);

        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(place));
        assertNotNull(placeManager.getActivity(place));
        assertTrue(placeManager.getActivePlaceRequests().contains(place));
    }

    /**
     * Verifies that all the expected side effects of a screen or editor activity launch have happened.
     * @param placeRequest The place request that was passed to some variant of PlaceManager.goTo().
     * @param activity <b>A Mockito mock<b> of the activity that was resolved for <tt>placeRequest</tt>.
     */
    private void verifyActivityLaunchSideEffects(PlaceRequest placeRequest,
                                                 WorkbenchActivity activity,
                                                 PanelDefinition expectedPanel) {

        // as of UberFire 0.4. this event only happens if the place is already visible.
        // it might be be better if the event was fired unconditionally. needs investigation.
        verify(selectWorkbenchPartEvent,
               never()).fire(any(SelectPlaceEvent.class));

        // we know the activity was created (or we wouldn't be here), but should verify that only happened one time
        verify(activityManager,
               times(1)).getActivities(placeRequest);

        // contract between PlaceManager and PanelManager
        Integer preferredWidth = activity.preferredWidth();
        Integer preferredHeight = activity.preferredHeight();
        Integer expectedPartWidth;
        Integer expectedPartHeight;
        if (expectedPanel == null) {
            PanelDefinition rootPanel = panelManager.getRoot();
            verify(panelManager).addWorkbenchPanel(rootPanel,
                                                   null,
                                                   preferredHeight,
                                                   preferredWidth,
                                                   null,
                                                   null);
            expectedPartWidth = null;
            expectedPartHeight = null;
        } else {
            expectedPartWidth = expectedPanel.getWidth();
            expectedPartHeight = expectedPanel.getHeight();
        }
        verify(panelManager).addWorkbenchPart(eq(placeRequest),
                                              eq(new PartDefinitionImpl(placeRequest)),
                                              expectedPanel == null ? any(PanelDefinition.class) : eq(
                                                      expectedPanel),
                                              isNull(Menus.class),
                                              any(UIPart.class),
                                              isNull(String.class),
                                              eq(expectedPartWidth),
                                              eq(expectedPartHeight));

        // contract between PlaceManager and PlaceHistoryHandler

        // state changes in PlaceManager itself (contract between PlaceManager and everyone)
        assertTrue("Actual place requests: " + placeManager.getActivePlaceRequests(),
                   placeManager.getActivePlaceRequests().contains(placeRequest));
        assertSame(activity,
                   placeManager.getActivity(placeRequest));
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(placeRequest));

        // contract between PlaceManager and Activity
        verify(activity,
               never()).onStartup(any(PlaceRequest.class)); // this is ActivityManager's job
        verify(activity,
               times(1)).onOpen();
    }

    // TODO test going to an unresolvable/unknown place

    // TODO test going to a place with a specific target panel (part of the PerspectiveManager/PlaceManager contract)

    // TODO test closing all panels when there are a variety of different types of panels open

    // TODO compare/contrast closeAllPlaces with closeAllCurrentPanels (former is public API; latter is called before launching a new perspective)

    /**
     * Verifies that the "place change" side effects have not happened, and that the given activity is still current.
     * @param expectedCurrentPlace The place request that placeManager should still consider "current."
     * @param activity <b>A Mockito mock<b> of the activity tied to <tt>expectedCurrentPlace</tt>.
     */
    private void verifyNoActivityLaunchSideEffects(PlaceRequest expectedCurrentPlace,
                                                   WorkbenchScreenActivity activity) {

        // contract between PlaceManager and PanelManager
        verify(panelManager,
               never()).addWorkbenchPanel(eq(panelManager.getRoot()),
                                          any(Position.class),
                                          any(Integer.class),
                                          any(Integer.class),
                                          any(Integer.class),
                                          any(Integer.class));

        verify(panelManager,
               never()).addWorkbenchPanel(eq(panelManager.getRoot()),
                                          any(PanelDefinition.class),
                                          any(Position.class));

        // state changes in PlaceManager itself (contract between PlaceManager and everyone)
        assertTrue(
                "Actual place requests: " + placeManager.getActivePlaceRequests(),
                placeManager.getActivePlaceRequests().contains(expectedCurrentPlace));
        assertSame(activity,
                   placeManager.getActivity(expectedCurrentPlace));
        assertEquals(PlaceStatus.OPEN,
                     placeManager.getStatus(expectedCurrentPlace));

        // contract between PlaceManager and Activity
        verify(activity,
               never()).onStartup(any(PlaceRequest.class));
        verify(activity,
               never()).onOpen();
    }

    class FakePathPlaceRequest extends PathPlaceRequest {

        final ObservablePath path;

        FakePathPlaceRequest(ObservablePath path) {
            this.path = path;
        }

        @Override
        public ObservablePath getPath() {
            return path;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }
}
