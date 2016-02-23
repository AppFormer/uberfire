/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.authz;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.AuthorizationPolicyBuilder;

/**
 * This implementation stores the authorization policy in a properties file.
 *
 * <p>The format of the entries are:</p>
 *
 * <pre>"classifier.identifier.setting.extra=value"</pre>
 *
 * Where:
 * <ul>
 *     <li>classifier = <i>role</i>|<i>group</i></li>
 *     <li>identifier = An existing role or group identifier (depending on the classifier type)</li>
 *     <li>setting    = <i>home</i>|<i>priority</i>|<i>permission</i></li>
 *     <li>extra      = Extra setting information. Mandatory for instance to define the permission's name</li>
 *     <li>value      = The setting value (depends on the setting selected). Value expected per setting type:
 *     <ul>
 *         <li>home: An existing perspective identifier to redirect after login</li>
 *         <li>priority: An integer indicating how priority is this role|group compared to others. Used for conflict resolution.</li>
 *         <li>permission: A name representing a specific feature or capability over a given resource.</li>
 *     </ul></li>
 * </ul>
 *
 * <p>For example:
 *
 * <pre>
 * #Role "admin"
 * role.admin.home=Home
 * role.admin.priority=10
 * role.admin.permission.perspective.view=true
 * role.admin.permission.perspective.view.Dashboard=false
 *
 * # Role "user"
 * role.user.home=Dashboard
 * role.user.priority=0
 * role.user.permission.perspective.view=false
 * role.user.permission.perspective.view.Home=true
 * role.user.permission.perspective.view.Dashboard=true
 * </pre>
 *</p>
 */
@ApplicationScoped
public class AuthorizationPolicyStorage {

    private Logger logger = LoggerFactory.getLogger(AuthorizationPolicyStorage.class);

    private static final String ROLE = "role";
    private static final String GROUP = "group";
    private static final String PERMISSION = "permission";
    private static final String PRIORITY = "priority";
    private static final String HOME = "home";
    private static final String DESCRIPTION = "description";

    private PermissionManager permissionManager;

    public AuthorizationPolicyStorage() {
    }

    @Inject
    public AuthorizationPolicyStorage(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public AuthorizationPolicy loadPolicy() {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("security-policy.properties");
        return loadPolicy(fileURL);
    }

    public AuthorizationPolicy loadPolicy(URL url) {
        if (url == null) {
            return null;
        }
        AuthorizationPolicyBuilder builder = permissionManager.newAuthorizationPolicy();

        try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
            Properties p = new Properties();
            p.load(reader);
            p.forEach((x,y) -> processEntry(builder, x.toString(), y.toString()));
            return builder.build();
        }
        catch (IOException e) {
            logger.error("Authorization Policy load error", e);
            return null;
        }
    }

    public void processEntry(AuthorizationPolicyBuilder builder, String key, String value) {
        List<String> tokens = parseKey(key);

        // Role or group setting
        String type = tokens.get(0);
        String typeId = tokens.get(1);
        switch (type) {
            case ROLE:
                builder.role(typeId);
                break;

            case GROUP:
                builder.group(typeId);
                break;

            default:
                throw new IllegalArgumentException("Key must start either with 'role' or 'group': " + key);
        }

        // Attribute/value
        String attr = tokens.get(2);
        switch (attr) {

            case DESCRIPTION:
                builder.description(value);
                break;

            case HOME:
                builder.home(value);
                break;

            case PRIORITY:
                builder.priority(Integer.parseInt(value));
                break;

            case PERMISSION:
                String permission = tokens.get(3);
                if (permission.length() == 0) {
                    throw new IllegalArgumentException("Permission is incomplete: " + key);
                }
                boolean granted = Boolean.parseBoolean(value);
                builder.permission(permission, granted);
                break;

            default:
                throw new IllegalArgumentException("Unknown key: " + key);
        }
    }

    public List<String> parseKey(String key) {
        String _key = key.endsWith(".*") ? key.substring(0, key.length()-2) : key;
        List<String> result = new ArrayList<>();
        String[] tokens = _key.split("\\.");
        for (String token : tokens) {
            if (token.length() == 0) {
                throw new IllegalArgumentException("Empty token not allowed: " + key);
            }
            if (result.size() < 4) {
                result.add(token);
            } else {
                result.set(3, result.get(3) + "." + token);
            }
        }
        if (result.size() < 3) {
            throw new IllegalArgumentException("Incomplete key: " + key);
        }
        return result;
    }

    public void savePolicy(AuthorizationPolicy policy) {

    }
}
