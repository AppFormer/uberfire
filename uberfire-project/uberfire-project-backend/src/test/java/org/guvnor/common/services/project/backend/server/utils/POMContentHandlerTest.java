/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.List;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Test;

import static java.util.stream.Collectors.toList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class POMContentHandlerTest {

    private static final List<ConfigurationStrategy> strategies =
            Collections.singletonList(new ConfigurationStaticStrategy());

    private static final String
            GAV_GROUP_ID_XML = "<groupId>org.guvnor</groupId>",
            GAV_ARTIFACT_ID_XML = "<artifactId>test</artifactId>",
            GAV_VERSION_XML = "<version>0.0.1</version>",
            EXISTING_PLUGIN_XML = "<plugin>"
                    + "<groupId>org.kie</groupId>"
                    + "<artifactId>kie-maven-plugin</artifactId>"
                    + "<version>7.5.0-SNAPSHOT</version>"
                    + "<extensions>true</extensions>"
                    + "</plugin>";

    @Test
    public void testPOMContentHandlerNewProject() throws IOException {
        final POMContentHandler handler = new POMContentHandler(strategies);
        final GAV gav = new GAV();
        gav.setGroupId("org.guvnor");
        gav.setArtifactId("test");
        gav.setVersion("0.0.1");
        final POM pom = new POM("name",
                                "description",
                                gav);
        final String xml = handler.toString(pom);

        assertContainsIgnoreWhitespace(GAV_GROUP_ID_XML, xml);
        assertContainsIgnoreWhitespace(GAV_ARTIFACT_ID_XML, xml);
        assertContainsIgnoreWhitespace(GAV_VERSION_XML, xml);
    }

    @Test
    public void testPOMContentHandlerExistingProject() throws IOException, XmlPullParserException {
        final POMContentHandler handler = new POMContentHandler(strategies);
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "  <modelVersion>4.0.0</modelVersion>"
                + "  <groupId>org.guvnor</groupId>"
                + "  <artifactId>test</artifactId>"
                + "  <version>0.0.1</version>"
                + "  <name>name</name>"
                + "  <description>description</description>"
                + "</project>";

        final POM pom = handler.toModel(xml);
        assertEquals("org.guvnor",
                     pom.getGav().getGroupId());
        assertEquals("test",
                     pom.getGav().getArtifactId());
        assertEquals("0.0.1",
                     pom.getGav().getVersion());
        assertEquals("name",
                     pom.getName());
        assertEquals("description",
                     pom.getDescription());

        final String enrichedXml = handler.toString(pom, xml);

        assertContainsIgnoreWhitespace(GAV_GROUP_ID_XML, enrichedXml);
        assertContainsIgnoreWhitespace(GAV_ARTIFACT_ID_XML, enrichedXml);
        assertContainsIgnoreWhitespace(GAV_VERSION_XML, enrichedXml);
    }

    @Test
    public void testPOMContentHandlerExistingJarProject() throws IOException, XmlPullParserException {
        /*
           Keep the original type
         */

        final POMContentHandler handler = new POMContentHandler(strategies);
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "  <modelVersion>4.0.0</modelVersion>"
                + "  <groupId>org.guvnor</groupId>"
                + "  <artifactId>test</artifactId>"
                + "  <version>0.0.1</version>"
                + "  <packaging>something</packaging>"
                + "  <name>name</name>"
                + "  <description>description</description>"
                + "</project>";

        final String enrichedXml = handler.toString(handler.toModel(xml),
                                                    xml);

        assertContainsIgnoreWhitespace("<packaging>kjar</packaging>",
                                       enrichedXml);
    }

    @Test
    public void testPOMContentHandlerExistingKieProject() throws IOException, XmlPullParserException {
        final POMContentHandler handler = new POMContentHandler(strategies);
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<modelVersion>4.0.0</modelVersion>"
                + "<groupId>org.guvnor</groupId>"
                + "<artifactId>test</artifactId>"
                + "<version>0.0.1</version>"
                + "<name>name</name>"
                + "<description>description</description>"
                + "<build>"
                + "  <plugins>"
                + "    <plugin>"
                + "      <groupId>org.kie</groupId>"
                + "      <artifactId>kie-maven-plugin</artifactId>"
                + "      <version>another-version</version>"
                + "      <extensions>true</extensions>"
                + "    </plugin>"
                + "  </plugins>"
                + "</build>"
                + "</project>";

        final POM pom = handler.toModel(xml);
        assertEquals("org.guvnor",
                     pom.getGav().getGroupId());
        assertEquals("test",
                     pom.getGav().getArtifactId());
        assertEquals("0.0.1",
                     pom.getGav().getVersion());
        assertEquals("name",
                     pom.getName());
        assertEquals("description",
                     pom.getDescription());

        final String enrichedXml = handler.toString(pom,
                                                    xml);

        assertContainsIgnoreWhitespace(GAV_GROUP_ID_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(GAV_ARTIFACT_ID_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(GAV_VERSION_XML,
                                       enrichedXml);
        assertContainsIgnoreWhitespace(EXISTING_PLUGIN_XML,
                                       enrichedXml);
    }

    @Test
    public void testPOMContentHandlerExistingKieProject_withDependencies() throws IOException, XmlPullParserException {
        final POMContentHandler handler = new POMContentHandler(strategies);
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "  <modelVersion>4.0.0</modelVersion>"
                + "  <groupId>org.guvnor</groupId>"
                + "  <artifactId>test</artifactId>"
                + "  <version>0.0.1</version>"
                + "  <name>name</name>"
                + "  <description>description</description>"
                + "  <build>"
                + "    <plugins>"
                + "      <plugin>"
                + "        <groupId>org.kie</groupId>"
                + "        <artifactId>kie-maven-plugin</artifactId>"
                + "        <version>another-version</version>"
                + "        <extensions>true</extensions>"
                + "      </plugin>"
                + "    </plugins>"
                + "  </build>"
                + "  <dependencies>"
                + "    <dependency>"
                + "      <groupId>org.crazy.user</groupId>"
                + "      <artifactId>users-custom-dependency</artifactId>"
                + "      <version>10.20.30</version>"
                + "    </dependency>"
                + "  </dependencies>"
                + "</project>";

        final POM pom = handler.toModel(xml);
        assertEquals("org.guvnor", pom.getGav().getGroupId());
        assertEquals("test", pom.getGav().getArtifactId());
        assertEquals("0.0.1", pom.getGav().getVersion());
        assertEquals("name", pom.getName());
        assertEquals("description", pom.getDescription());

        final String enrichedXml = handler.toString(pom, xml);

        POM enrichedPom = handler.toModel(enrichedXml);

        List<String> artifactNames = enrichedPom.getDependencies().stream()
                .map(dep -> dep.getArtifactId())
                .collect(toList());

        assertThat(artifactNames)
                .as("Previously existing dependencies should be kept and kie dependencies should be added")
                .hasSize(4)
                .containsOnly("users-custom-dependency", "kie-api", "optaplanner-core", "junit");
    }

    @Test
    public void toModel_shouldPreserveExistingParent() throws Exception {
        final POMContentHandler handler = new POMContentHandler(strategies);
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
                "  <modelVersion>4.0.0</modelVersion>"
                + "  <parent>"
                + "    <groupId>org.tadaa</groupId>"
                + "    <artifactId>tadaa</artifactId>"
                + "    <version>1.2.3</version>"
                + "  </parent>"
                + "  <artifactId>myproject</artifactId>"
                + "  <packaging>kjar</packaging>"
                + "  <name>myproject</name>"
                + "  <build>"
                + "    <plugins>"
                + "      <plugin>"
                + "        <groupId>org.kie</groupId>"
                + "        <artifactId>kie-maven-plugin</artifactId>"
                + "        <version>another-version</version>"
                + "        <extensions>true</extensions>"
                + "      </plugin>"
                + "    </plugins>"
                + "  </build>"
                + "</project>";

        final POM pom = handler.toModel(xml);

        assertNotNull(pom.getParent());
        assertEquals("org.tadaa",
                     pom.getParent().getGroupId());
        assertEquals("tadaa",
                     pom.getParent().getArtifactId());
        assertEquals("1.2.3",
                     pom.getParent().getVersion());
    }

    @Test
    public void toModel_shouldPreserveModulesAndRepositories() throws IOException, XmlPullParserException {
        final POMContentHandler handler = new POMContentHandler(strategies);
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "  <modelVersion>4.0.0</modelVersion>"
                + "  <groupId>org.mymodules</groupId>"
                + "  <artifactId>project10</artifactId>"
                + "  <version>1.5</version>"
                + "  <packaging>pom</packaging>"
                + "  <name>project-with-modules</name>"
                + "  <modules>"
                + "    <module>module1</module>"
                + "    <module>module2</module>"
                + "  </modules>"
                + "  <repositories>"
                + "    <repository>"
                + "      <id>my-cool-repo</id>"
                + "      <name>My Cool Repository</name>"
                + "      <url>http://cool.repo.org/maven2</url>"
                + "      <layout>default</layout>"
                + "      <snapshots>"
                + "        <enabled>false</enabled>"
                + "      </snapshots>"
                + "    </repository>"
                + "  </repositories>"
                + "</project>";
        POM pom = handler.toModel(xml);

        assertThat(pom.getPackaging()).isEqualTo("pom");
        assertThat(pom.getModules()).containsExactly("module1", "module2");
        assertThat(pom.getRepositories()).hasSize(1);
        assertThat(pom.getRepositories().get(0).getName()).isEqualTo("My Cool Repository");
    }

    private void assertContainsIgnoreWhitespace(final String expected,
                                                final String xml) {
        final String cleanExpected = expected.replaceAll("\\s+",
                                                         "");
        final String cleanActual = xml.replaceAll("\\s+",
                                                  "");

        assertTrue(cleanActual.contains(cleanExpected));
    }
}
