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

package org.guvnor.common.services.project.backend.server.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

@ApplicationScoped
public class POMContentHandler {

    private static final String COMPILE = "compile";
    private static final String COMPILER_ID = "compilerId";
    private static final String CONFIGURATION = "configuration";
    private static final String COMPILER = "javac";
    private static final String KJAR = "kjar";
    private static final String TRUE = "true";
    private static final String MAVEN_SKIP = "skip";
    private static final String MAVEN_SKIP_MAIN = "skipMain";
    private static final String MAVEN_DEFAULT_COMPILE = "default-compile";
    private static final String MAVEN_PHASE_NONE = "none";
    private static final String MAVEN_PLUGIN_CONFIGURATION = "configuration";

    private final List<ConfigurationStrategy> configurationStrategies = new ArrayList<>();

    public POMContentHandler() {
    }

    @Inject
    public POMContentHandler(final Instance<ConfigurationStrategy> configuration) {
        this(stream(configuration.spliterator(), false)
                     .collect(toList()));
    }

    public POMContentHandler(Collection<ConfigurationStrategy> configurations) {
        this.configurationStrategies.addAll(configurations);
    }

    public String toString(final POM pomModel)
            throws IOException {
        return toString(pomModel,
                        new Model());
    }

    private String toString(final POM pom,
                            final Model model) throws IOException {
        final ConfigurationStrategy configurationStrategy = configurationStrategies.get(0);
        final Map<ConfigurationKey, String> conf = configurationStrategy.loadConfiguration();

        addDependencies(pom, model, conf);

        Build build = new Build();
        model.setBuild(build);
        build.addPlugin(getKieMavenPlugin(conf));
        build.addPlugin(getNewCompilerPlugin(conf));
        build.addPlugin(getDisableMavenCompiler(conf));
        model.setPackaging(KJAR);

        model.setName(pom.getName());
        model.setDescription(pom.getDescription());
        model.setArtifactId(pom.getGav().getArtifactId());
        model.setModelVersion(pom.getModelVersion());
        model.setGroupId(pom.getGav().getGroupId());
        model.setVersion(pom.getGav().getVersion());
        model.setParent(getParent(pom));
        model.setModules(getModules(pom));
        model.setRepositories(getRepositories(pom));

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter,
                                    model);
        return stringWriter.toString();
    }

    private void addDependencies(final POM pom, final Model model, final Map<ConfigurationKey, String> conf) {
        String kieVersion = conf.get(ConfigurationKey.KIE_VERSION);
        if (pom.getDependencies().isEmpty()) {
            List<Dependency> dependencies = new ArrayList<>();
            dependencies.add(getDependency("org.kie", "kie-api", kieVersion, "provided"));
            dependencies.add(getDependency("org.optaplanner", "optaplanner-core", kieVersion, "provided"));
            dependencies.add(getDependency("junit", "junit", "4.12", "test"));
            model.setDependencies(dependencies);
        } else {
            pom.getDependencies().add(getGuvDependency(conf, "org.kie", "kie-api", kieVersion, "provided"));
            pom.getDependencies().add(getGuvDependency(conf, "org.optaplanner", "optaplanner-core", kieVersion, "provided"));
            pom.getDependencies().add(getGuvDependency(conf, "junit", "junit", "4.12", "test"));
            new DependencyUpdater(model.getDependencies()).updateDependencies(pom.getDependencies());
        }
    }

    private Dependency getDependency(String groupID, String artifactID, String version, String scope) {
        Dependency dep = new Dependency();
        dep.setGroupId(groupID);
        dep.setArtifactId(artifactID);
        dep.setVersion(version);
        dep.setScope(scope);
        return dep;
    }

    private org.guvnor.common.services.project.model.Dependency getGuvDependency(Map<ConfigurationKey, String> conf, String groupID, String artifactID, String version, String scope) {
        org.guvnor.common.services.project.model.Dependency dep = new org.guvnor.common.services.project.model.Dependency();
        dep.setGroupId(groupID);
        dep.setArtifactId(artifactID);
        dep.setVersion(version);
        dep.setScope(scope);
        return dep;
    }

    protected Plugin getNewCompilerPlugin(Map<ConfigurationKey, String> conf) {

        Plugin newCompilerPlugin = new Plugin();
        newCompilerPlugin.setGroupId(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_GROUP));
        newCompilerPlugin.setArtifactId(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_ARTIFACT));
        newCompilerPlugin.setVersion(conf.get(ConfigurationKey.TAKARI_COMPILER_PLUGIN_VERSION));

        PluginExecution execution = new PluginExecution();
        execution.setId(COMPILE);
        execution.setGoals(Collections.singletonList(COMPILE));
        execution.setPhase(COMPILE);

        Xpp3Dom compilerId = new Xpp3Dom(COMPILER_ID);
        compilerId.setValue(COMPILER);
        Xpp3Dom configuration = new Xpp3Dom(CONFIGURATION);
        configuration.addChild(compilerId);

        execution.setConfiguration(configuration);
        newCompilerPlugin.setExecutions(Collections.singletonList(execution));

        return newCompilerPlugin;
    }

    protected Plugin getKieMavenPlugin(Map<ConfigurationKey, String> conf) {
        String kieVersion = conf.get(ConfigurationKey.KIE_VERSION);
        Plugin kieMavenPlugin = new Plugin();
        kieMavenPlugin.setGroupId("org.kie");
        kieMavenPlugin.setArtifactId("kie-maven-plugin");
        kieMavenPlugin.setVersion(kieVersion);
        kieMavenPlugin.setExtensions(true);
        return kieMavenPlugin;
    }

    protected Plugin getDisableMavenCompiler(Map<ConfigurationKey, String> conf) {
        Plugin plugin = new Plugin();
        plugin.setArtifactId(conf.get(ConfigurationKey.MAVEN_COMPILER_PLUGIN_ARTIFACT));

        Xpp3Dom skipMain = new Xpp3Dom(MAVEN_SKIP_MAIN);
        skipMain.setValue(TRUE);
        Xpp3Dom skip = new Xpp3Dom(MAVEN_SKIP);
        skip.setValue(TRUE);

        Xpp3Dom configuration = new Xpp3Dom(MAVEN_PLUGIN_CONFIGURATION);
        configuration.addChild(skipMain);
        configuration.addChild(skip);

        plugin.setConfiguration(configuration);

        PluginExecution exec = new PluginExecution();
        exec.setId(MAVEN_DEFAULT_COMPILE);
        exec.setPhase(MAVEN_PHASE_NONE);
        plugin.setExecutions(Collections.singletonList(exec));
        return plugin;
    }

    private List<Repository> getRepositories(final POM pom) {
        return pom.getRepositories().stream()
                .map(this::fromClientModelToPom)
                .collect(toList());
    }

    private List<String> getModules(final POM pom) {
        if (pom.getModules() != null) {
            return new ArrayList<>(pom.getModules());
        } else {
            return Collections.emptyList();
        }
    }

    private Parent getParent(final POM pom) {
        if (pom.getParent() == null) {
            return null;
        } else {
            Parent parent = new Parent();
            parent.setGroupId(pom.getParent().getGroupId());
            parent.setArtifactId(pom.getParent().getArtifactId());
            parent.setVersion(pom.getParent().getVersion());
            return parent;
        }
    }

    /**
     * @param gavModel The model that is saved
     * @param originalPomAsText The original pom in text form, since the guvnor POM model does not cover all the pom.xml features.
     * @return pom.xml for saving, The original pom.xml with the fields edited in gavModel replaced.
     * @throws IOException
     */
    public String toString(final POM gavModel,
                           final String originalPomAsText) throws IOException, XmlPullParserException {

        return toString(gavModel,
                        new MavenXpp3Reader().read(new StringReader(originalPomAsText)));
    }

    private Repository fromClientModelToPom(final org.guvnor.common.services.project.model.Repository from) {
        Repository to = new Repository();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());
        return to;
    }

    public POM toModel(final String pomAsString) throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new StringReader(pomAsString));

        POM pomModel = new POM(
                model.getName(),
                model.getDescription(),
                new GAV(
                        (model.getGroupId() == null ? model.getParent().getGroupId() : model.getGroupId()),
                        (model.getArtifactId() == null ? model.getParent().getArtifactId() : model.getArtifactId()),
                        (model.getVersion() == null ? model.getParent().getVersion() : model.getVersion())
                )
        );

        pomModel.setPackaging(model.getPackaging());

        if (model.getParent() != null) {
            pomModel.setParent(new GAV(model.getParent().getGroupId(),
                                       model.getParent().getArtifactId(),
                                       model.getParent().getVersion()));
        }

        pomModel.getModules().clear();
        for (String module : model.getModules()) {
            pomModel.getModules().add(module);
            pomModel.setPackaging("pom");
        }
        for (Repository repository : model.getRepositories()) {
            pomModel.addRepository(fromPomModelToClientModel(repository));
        }

        pomModel.setDependencies(new DependencyContentHandler().fromPomModelToClientModel(model.getDependencies()));

        pomModel.setBuild(new BuildContentHandler().fromPomModelToClientModel(model.getBuild()));

        return pomModel;
    }

    private org.guvnor.common.services.project.model.Repository fromPomModelToClientModel(final Repository from) {
        org.guvnor.common.services.project.model.Repository to = new org.guvnor.common.services.project.model.Repository();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }
}
