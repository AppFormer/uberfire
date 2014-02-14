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

package ${packageName};

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Generated;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.TemplatePanelDefinitionImpl;
import org.uberfire.client.workbench.TemplatePerspectiveDefinitionImpl;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

<#if getMenuBarMethodName??>
import org.uberfire.workbench.model.menu.Menus;

</#if>
<#if getToolBarMethodName??>
import org.uberfire.workbench.model.toolbar.ToolBar;

</#if>
@Dependent
@Generated("org.uberfire.annotations.processors.WorkbenchPerspectiveProcessor")
@Named("${identifier}")
/*
 * WARNING! This class is generated. Do not modify.
 */
public class ${className} extends AbstractWorkbenchPerspectiveActivity {

    <#if rolesList??>
    private static final Collection<String> ROLES = Arrays.asList(${rolesList});
    <#else>
    private static final Collection<String> ROLES = Collections.emptyList();
    </#if>

    <#if securityTraitList??>
    private static final Collection<String> TRAITS = Arrays.asList(${securityTraitList});
    <#else>
    private static final Collection<String> TRAITS = Collections.emptyList();
    </#if>

    @Inject
    private ${realClassName} realPresenter;

    @Inject
    //Constructor injection for testing
    public ${className}(final PlaceManager placeManager) {
        super( placeManager );
    }

    @Override
    public String getIdentifier() {
        return "${identifier}";
    }

    <#if isDefault>
    @Override
    public boolean isDefault() {
        return true;
    }

    </#if>
    <#if onStartup1ParameterMethodName??>
    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup( place );
        realPresenter.${onStartup1ParameterMethodName}( place );
    }

    <#elseif onStartup0ParameterMethodName??>
    @Override
    public void onStartup(final PlaceRequest place) {
        super.onStartup();
        realPresenter.${onStartup0ParameterMethodName}();
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
    <#if getPerspectiveMethodName??>
    @Override
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition p = new TemplatePerspectiveDefinitionImpl( this );
        p.setName( getClass().getName() );

        PanelDefinition panelDefinition = new TemplatePanelDefinitionImpl( this, PanelType.STATIC );
        panelDefinition.addPart(
        new PartDefinitionImpl(
        new DefaultPlaceRequest( "HelloWorldScreen1" ) ) {
        }
        );
        panelDefinition.addPart(
        new PartDefinitionImpl(
        new DefaultPlaceRequest( "HelloWorldScreen2" ) ) {
        }
        );
        p.getRoot().appendChild( Position.EAST, panelDefinition );

        return p;
    }

    @Override
    public void setWidget( String fieldName,
        Widget widget ) {
        if ( fieldName.equalsIgnoreCase( "hello1" ) ) {
           realPresenter.hello1.add( widget.asWidget() );
        }
        if ( fieldName.equalsIgnoreCase( "hello2" ) ) {
           realPresenter.hello2.add( widget.asWidget() );
        }
        if ( fieldName.equalsIgnoreCase( "hello3" ) ) {
           realPresenter.hello3.add( widget.asWidget() );
        }
    }

    @Override
    public Widget getRealPresenterWidget( ) {
        return realPresenter.asWidget();
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
    @Override
    public Collection<String> getRoles() {
        return ROLES;
    }

    @Override
    public Collection<String> getTraits() {
        return TRAITS;
    }

    @Override
    public String getSignatureId() {
        return "${packageName}.${className}";
    }
}
