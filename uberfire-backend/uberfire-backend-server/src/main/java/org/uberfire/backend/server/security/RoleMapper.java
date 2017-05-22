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

package org.uberfire.backend.server.security;

import org.jboss.errai.security.shared.api.Role;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.mvp.Command;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Startup class that loads role mapping providers and uses them to create the {@link RolePrincipalsRegistry}.
 */
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class RoleMapper {

    Logger logger = LoggerFactory.getLogger(RoleMapper.class);

    @PostConstruct
    public void init() {
        WebAppListener.registerOnStartupCommand(new Command() {

            @Override
            public void execute() {
                registerRolePrincipals();
            }
        });
    }

    public void registerRolePrincipals() {
        try {
            HashMap<String, JSONObject> roleMappers = loadRoleMappers();
            for (Entry<String, JSONObject> jo : roleMappers.entrySet()) {
                // Regex role mapping provider a set of Regex patterns to be applied in determining if a user/group is authorized for a role.
                if (jo.getKey().equals("org.uberfire.regex.role_mapping")) {
                    Set<String> principalPatterns = new HashSet<String>(RoleRegistry.get().getRegisteredRoles().size());
                    String template = jo.getValue().getString("regex");
                    // create Regex pattern for each role using template found in regex provider
                    for (Role role : RoleRegistry.get().getRegisteredRoles()) {
                        String pattern = template;
                        principalPatterns.add(pattern.replaceAll("\\{role\\}", role.getName()));
                    }
                    RolePrincipalsRegistry.get().registerRolePrincipals("regex", principalPatterns);

                } else {
                    // all other mappers provide exact role to principal name mapping to be used in determining if a user/group is authorized for a role.
                    for (Role role : RoleRegistry.get().getRegisteredRoles()) {

                        JSONArray principalsJSON = jo.getValue().getJSONArray(role.getName());
                        if (principalsJSON != null) {
                            Set<String> principalList = new HashSet<String>(principalsJSON.length());
                            for (int i = 0; i < principalsJSON.length(); i++) {
                                principalList.add(principalsJSON.getString(i));
                            }
                            RolePrincipalsRegistry.get().registerRolePrincipals(role.getName(), principalList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error mapping roles to principals.", e);
        }
    }

    /*
     * Role mappers are JSON files mapping an application roles to security users and groups. The location of the mapper(s) is a system property.
     */
    protected HashMap<String, JSONObject> loadRoleMappers() throws Exception {
        try {
            final String resourceName = System.getProperty("org.uberfire.role.mappers", "role-mapping-providers.properties");
            InputStream fileIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);

            Properties roleMappigProviders = new Properties();
            roleMappigProviders.load(fileIS);

            HashMap<String, JSONObject> roleMappers = new HashMap<String, JSONObject>();
            Enumeration<String> e = (Enumeration<String>) roleMappigProviders.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                URL roleMapperURL = Thread.currentThread().getContextClassLoader().getResource(roleMappigProviders.getProperty(key));
                if (roleMapperURL != null) {
                    roleMappers.put(key, new JSONObject(new String(Files.readAllBytes(Paths.get(roleMapperURL.getPath())))));
                }
            }
            return roleMappers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
