/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.maven.integration.embedder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenProjectLoader {
	   
    private static final Logger log = LoggerFactory.getLogger(MavenProjectLoader.class);
    
    private static final String DUMMY_POM =
            "    <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "      xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "      <modelVersion>4.0.0</modelVersion>\n" +
            "     \n" +
            "      <groupId>myGroupId</groupId>\n" +
            "      <artifactId>myArtifactId</artifactId>\n" +
            "      <version>1.0-SNAPSHOT</version>\n" +
            "    </project>";
 
    static MavenProject mavenProject;
 
    public static MavenProject parseMavenPom(File pomFile) {
        return parseMavenPom(pomFile, false);
    }
 
    public static MavenProject parseMavenPom(File pomFile, boolean offline) {
        boolean hasPom = pomFile.exists();
 
        MavenRequest mavenRequest = createMavenRequest(offline);
        if (hasPom) {
            mavenRequest.setPom( pomFile.getAbsolutePath() );
        }
        MavenEmbedder mavenEmbedder = null;
        try {
            mavenEmbedder = new MavenEmbedder( mavenRequest );
            return hasPom ?
                    mavenEmbedder.readProject( pomFile ) :
                    mavenEmbedder.readProject( new ByteArrayInputStream(DUMMY_POM.getBytes( StandardCharsets.UTF_8 )) );
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (mavenEmbedder != null) {
                mavenEmbedder.dispose();
            }
        }
    }
        
    public static MavenProject parseMavenPom(InputStream pomStream) {
        return parseMavenPom(pomStream, false);
    }

    public static MavenProject parseMavenPom(InputStream pomStream, boolean offline) {
        MavenEmbedder mavenEmbedder = null;
        try {
            mavenEmbedder = newMavenEmbedder(offline);
            return mavenEmbedder.readProject( pomStream );
        } catch (Exception e) {
            log.error("Unable to create MavenProject from InputStream", e);
            throw new RuntimeException(e);
        } finally {
            if (mavenEmbedder != null) {
                mavenEmbedder.dispose();
            }
        }
    }

    public static MavenEmbedder newMavenEmbedder(boolean offline) {
        MavenRequest mavenRequest = createMavenRequest(offline);
        MavenEmbedder mavenEmbedder;
            try {
                mavenEmbedder = new MavenEmbedder( mavenRequest );
            } catch (MavenEmbedderException e) {
                log.error("Unable to create new MavenEmbedder", e);
                throw new RuntimeException(e);
            }
        return mavenEmbedder;
    }

    public static MavenRequest createMavenRequest(boolean offline) {
        MavenRequest mavenRequest = new MavenRequest();
        mavenRequest.setLocalRepositoryPath( MavenSettings.getSettings().getLocalRepository() );
        mavenRequest.setUserSettingsSource(MavenSettings.getUserSettingsSource());

        // BZ-1007894: If dependency is not resolvable and maven project builder does not complain about it,
        // then a <code>java.lang.NullPointerException</code> is thrown to the client.
        // So, the user will se an exception message "null", not descriptive about the real error.
        mavenRequest.setResolveDependencies( true );
        mavenRequest.setOffline( offline );
        return mavenRequest;
    }

    public static synchronized MavenProject loadMavenProject() {
        return loadMavenProject(false);
    }

    public static synchronized MavenProject loadMavenProject(boolean offline) {
        if (mavenProject == null) {
            File pomFile = new File( "pom.xml" );
            try {
                mavenProject = parseMavenPom(pomFile, offline);
            } catch (Exception e) {
                log.warn("Unable to parse pom.xml file of the running project: " + e.getMessage());
            }
        }
        return mavenProject;
    }
}
