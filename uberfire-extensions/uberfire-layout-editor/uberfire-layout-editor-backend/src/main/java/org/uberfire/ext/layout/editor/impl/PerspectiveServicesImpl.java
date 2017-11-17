/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.impl;

import java.util.Collection;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.plugin.backend.PluginServicesImpl;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

@Service
@ApplicationScoped
public class PerspectiveServicesImpl implements PerspectiveServices {

    private PluginServicesImpl pluginServices;
    private LayoutServicesImpl layoutServices;

    @Inject
    public PerspectiveServicesImpl(PluginServicesImpl pluginServices, LayoutServicesImpl layoutServices) {
        this.pluginServices = pluginServices;
        this.layoutServices = layoutServices;
    }

    @Override
    public Collection<LayoutTemplate> listLayoutTemplates() {
        return pluginServices.listPlugins().stream()
                .map(this::getLayoutTemplate)
                .collect(Collectors.toList());
    }

    @Override
    public LayoutTemplate getLayoutTemplate(String perspectiveName) {
        Plugin perspectivePlugin = getLayoutTemplatePlugin(perspectiveName);
        return perspectivePlugin != null ? getLayoutTemplate(perspectivePlugin) : null;
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Path perspectivePath) {
        LayoutEditorModel layoutEditorModel = pluginServices.getLayoutEditor(perspectivePath, PluginType.PERSPECTIVE_LAYOUT);
        if (layoutEditorModel.isEmptyLayout()) {
            return new LayoutTemplate(layoutEditorModel.getName());
        }
        return layoutServices.convertLayoutFromString(layoutEditorModel.getLayoutEditorModel());
    }

    @Override
    public LayoutTemplate getLayoutTemplate(Plugin perspectivePlugin) {
        return getLayoutTemplate(perspectivePlugin.getPath());
    }

    public Plugin getLayoutTemplatePlugin(String perspectiveName) {
        if (perspectiveName == null) {
            return null;
        }
        for (Plugin plugin : pluginServices.listPlugins()) {
            if (PluginType.PERSPECTIVE_LAYOUT.equals(plugin.getType()) && plugin.getName().equals(perspectiveName)) {
                return plugin;
            }
        }
        return null;
    }

    @Override
    public Path saveLayoutTemplate(Path perspectivePath, LayoutTemplate layoutTemplate, String commitMessage) {
        String layoutModel = layoutServices.convertLayoutToString(layoutTemplate);
        LayoutEditorModel plugin = new LayoutEditorModel(layoutTemplate.getName(), PluginType.PERSPECTIVE_LAYOUT, perspectivePath, layoutModel);
        pluginServices.saveLayout(plugin, commitMessage);
        return perspectivePath;
    }

    @Override
    public LayoutTemplate convertToLayoutTemplate(String layoutModel) {
        return layoutServices.convertLayoutFromString(layoutModel);
    }

    @Override
    public Path copy(Path path, String newName, String comment) {
        Path pathCopy = pluginServices.copy(path, newName, comment);
        this.setLayoutTemplateName(pathCopy, newName, comment);
        return pathCopy;
    }

    @Override
    public Path copy(Path path, String newName, Path targetDirectory, String comment) {
        Path pathCopy = pluginServices.copy(path, newName, targetDirectory, comment);
        this.setLayoutTemplateName(pathCopy, newName, comment);
        return pathCopy;
    }

    @Override
    public Path rename(Path path, String newName, String comment) {
        Path pathRenamed = pluginServices.rename(path, newName, comment);
        this.setLayoutTemplateName(pathRenamed, newName, comment);
        return pathRenamed;
    }

    @Override
    public void delete(Path path, String comment) {
        pluginServices.delete(path, comment);
    }

    private void setLayoutTemplateName(Path path, String newName, String comment) {
        LayoutTemplate layoutTemplate = getLayoutTemplate(path);
        layoutTemplate.setName(newName);

        String layoutModel = layoutServices.convertLayoutToString(layoutTemplate);
        LayoutEditorModel pluginCopy = new LayoutEditorModel(newName, PluginType.PERSPECTIVE_LAYOUT, path, layoutModel);
        pluginServices.saveLayout(pluginCopy, comment);
    }
}
