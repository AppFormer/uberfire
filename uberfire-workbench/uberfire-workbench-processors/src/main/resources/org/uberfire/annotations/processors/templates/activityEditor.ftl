/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package ${packageName};

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Generated;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

<#if hasUberView>
import javax.annotation.PostConstruct;
import org.uberfire.client.mvp.UberView;

</#if>
import javax.inject.Named;
<#if associatedResources??>
import org.uberfire.client.workbench.annotations.AssociatedResources;
</#if>
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.mvp.AbstractWorkbenchEditorActivity;
import org.uberfire.client.mvp.PlaceManager;

<#if getDefaultPositionMethodName??>
import org.uberfire.workbench.model.Position;

</#if>
import org.uberfire.mvp.PlaceRequest;
<#if owningPlace??>
import org.uberfire.mvp.impl.DefaultPlaceRequest;
</#if>

<#if getMenuBarMethodName??>
import org.uberfire.workbench.model.menu.Menus;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.workbench.model.toolbar.ToolBar;

</#if>
import org.uberfire.backend.vfs.ObservablePath;

import com.google.gwt.user.client.ui.IsWidget;

<#if beanActivatorClass??>
import org.jboss.errai.ioc.client.api.ActivatedBy;

</#if>
<#if lockingStrategy??>
import org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy;
import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.*;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchEditorProcessor")
@Named("${identifier}")
<#if associatedResources??>
${associatedResources}
</#if>
@Priority(${priority})
<#if beanActivatorClass??>
@ActivatedBy(${beanActivatorClass}.class)
</#if>
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractWorkbenchEditorActivity {

    @Inject
    private ${realClassName} realPresenter;

    @Inject
    //Constructor injection for testing
    public ${className}(final PlaceManager placeManager) {
        super( placeManager );
    }
    <#if hasUberView>

    @PostConstruct
    public void init() {
        ((UberView) realPresenter.${getWidgetMethodName}()).init( realPresenter );
    }
    </#if>
    <#if preferredHeight??>

    @Override
    public Integer preferredHeight() {
       return ${preferredHeight};
    }
    </#if>
    <#if preferredWidth??>

    @Override
    public Integer preferredWidth() {
       return ${preferredWidth};
    }
    </#if>
    <#if onStartup2ParameterMethodName??>

    @Override
    public void onStartup(final ObservablePath path,
                        final PlaceRequest place) {
        super.onStartup( path, place );
        realPresenter.${onStartup2ParameterMethodName}( path, place );
    }
    <#elseif onStartup1ParameterMethodName??>

    @Override
    public void onStartup(final ObservablePath path,
                        final PlaceRequest place) {
        super.onStartup( path, place );
        realPresenter.${onStartup1ParameterMethodName}( path );
    }
    </#if>
    <#if onMayCloseMethodName??>

    @Override
    public boolean onMayClose() {
        return realPresenter.${onMayCloseMethodName}();
    }
    </#if>
    <#if onCloseMethodName??>

    @Override
    public void onClose() {
        super.onClose();
        realPresenter.${onCloseMethodName}();
    }
    </#if>
    <#if onShutdownMethodName??>

    @Override
    public void onShutdown() {
        super.onShutdown();
        realPresenter.${onShutdownMethodName}();
    }
    </#if>
    <#if onOpenMethodName??>

    @Override
    public void onOpen() {
        super.onOpen();
        realPresenter.${onOpenMethodName}();
    }
    </#if>
    <#if onLostFocusMethodName??>

    @Override
    public void onLostFocus() {
        super.onLostFocus();
        realPresenter.${onLostFocusMethodName}();
    }
    </#if>
    <#if onFocusMethodName??>

    @Override
    public void onFocus() {
        super.onFocus();
        realPresenter.${onFocusMethodName}();
    }
    </#if>
    <#if owningPlace??>

    @Override
    public PlaceRequest getOwningPlace() {
        return new DefaultPlaceRequest("${owningPlace}");
    }
    </#if>
    <#if getTitleWidgetMethodName??>

    @Override
    public IsWidget getTitleDecoration() {
        return realPresenter.${getTitleWidgetMethodName}();
    }
    </#if>
    <#if getTitleMethodName??>

    @Override
    public String getTitle() {
        return realPresenter.${getTitleMethodName}();
    }
    </#if>
    <#if getWidgetMethodName??>

    @Override
    public IsWidget getWidget() {
        return realPresenter.${getWidgetMethodName}();
    }
    <#elseif isWidget>

    @Override
    public IsWidget getWidget() {
        return realPresenter;
    }
    </#if>
    <#if getDefaultPositionMethodName??>

    @Override
    public Position getDefaultPosition() {
        return realPresenter.${getDefaultPositionMethodName}();
    }
    </#if>
    <#if isDirtyMethodName??>

    @Override
    public boolean isDirty() {
        return realPresenter.${isDirtyMethodName}();
    }
    </#if>
    <#if onSaveMethodName??>

    @Override
    public void onSave() {
        super.onSave();
        realPresenter.${onSaveMethodName}();
    }
    </#if>
    <#if getMenuBarMethodName??>

    @Override
    public Menus getMenus() {
        return realPresenter.${getMenuBarMethodName}();
    }
    </#if>
    <#if getToolBarMethodName??>

    @Override
    public ToolBar getToolBar() {
        return realPresenter.${getToolBarMethodName}();
    }
    </#if>
    <#if getContextIdMethodName??>

    @Override
    public String contextId() {
        return realPresenter.${getContextIdMethodName}();
    }
    </#if>
    <#if lockingStrategy??>

    @Override
    public LockingStrategy getLockingStrategy() {
        return ${lockingStrategy};
    }
    </#if>

    @Override
    public String getIdentifier() {
        return "${identifier}";
    }
}
