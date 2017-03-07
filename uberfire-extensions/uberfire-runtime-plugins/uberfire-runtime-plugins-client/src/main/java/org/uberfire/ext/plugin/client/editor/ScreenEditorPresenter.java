/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.client.type.ScreenPluginResourceType;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.workbench.model.menu.Menus;

@Dependent
@WorkbenchEditor(identifier = "Screen PlugIn Editor", supportedTypes = {ScreenPluginResourceType.class}, priority = Integer.MAX_VALUE)
public class ScreenEditorPresenter
        extends RuntimePluginBaseEditor {

    @Inject
    private ScreenPluginResourceType resourceType;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    public ScreenEditorPresenter(final ScreenEditorView baseView) {
        super(baseView);
    }

    protected ClientResourceType getResourceType() {
        return resourceType;
    }

    protected PluginType getPluginType() {
        return PluginType.SCREEN;
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return CommonConstants.INSTANCE.ScreenPluginEditor() + " [" + this.plugin.getName() + "]";
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.baseView;
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose();
    }

    @Override
    ScreenEditorView view() {
        return (ScreenEditorView) baseView;
    }
}