/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.resteasy.client.ClientResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.CapabilityStatus;
import org.uberfire.ext.security.management.api.ContextualManager;
import org.uberfire.ext.security.management.api.GroupManager;
import org.uberfire.ext.security.management.api.GroupManagerSettings;
import org.uberfire.ext.security.management.api.UserSystemManager;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.impl.GroupManagerSettingsImpl;
import org.uberfire.ext.security.management.keycloak.client.resource.RealmResource;
import org.uberfire.ext.security.management.keycloak.client.resource.RoleResource;
import org.uberfire.ext.security.management.keycloak.client.resource.RolesResource;
import org.uberfire.ext.security.management.keycloak.client.resource.UserResource;
import org.uberfire.ext.security.management.keycloak.client.resource.UsersResource;
import org.uberfire.ext.security.management.search.GroupsRuntimeSearchEngine;
import org.uberfire.ext.security.management.search.RuntimeSearchEngine;
import org.uberfire.ext.security.management.util.SecurityManagementUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * <p>GroupsManager Service Provider Implementation for KeyCloak.</p>
 * <p>Note that roles (in keycloak server) are mapped as groups (in the workbench) for the keycloak users management provider impl.</p>
 *
 * @since 0.8.0
 */
public class KeyCloakGroupManager extends BaseKeyCloakManager implements GroupManager,
                                                                         ContextualManager {

    private static final Logger LOG = LoggerFactory.getLogger(KeyCloakGroupManager.class);
    RuntimeSearchEngine<Group> groupsSearchEngine;

    public KeyCloakGroupManager() {
    }

    @Override
    public void initialize(UserSystemManager userSystemManager) throws Exception {
        groupsSearchEngine = new GroupsRuntimeSearchEngine();
    }

    @Override
    public SearchResponse<Group> search(SearchRequest request) throws SecurityManagementException {
        // First page must be 1.
        if (request.getPage() <= 0) {
            throw new RuntimeException("First page must be 1.");
        }
        RealmResource realmResource = getRealmResource();
        RolesResource rolesResource = realmResource.roles();
        List<RoleRepresentation> roleRepresentations = rolesResource.list();
        List<Group> roles = null;
        if (roleRepresentations != null && !roleRepresentations.isEmpty()) {
            roles = new ArrayList<Group>();
            for (RoleRepresentation role : roleRepresentations) {
                final String name = role.getName();
                final Group group = createGroup(name);
                roles.add(group);
            }
        }
        return groupsSearchEngine.search(roles,
                                         request);
    }

    @Override
    public Group get(String identifier) throws SecurityManagementException {
        checkNotNull("identifier",
                     identifier);
        RealmResource realmResource = getRealmResource();
        RolesResource rolesResource = realmResource.roles();
        RoleResource roleResource = rolesResource.get(identifier);
        if (roleResource != null) {
            RoleRepresentation roleRepresentation = getRoleRepresentation(identifier,
                                                                          roleResource);
            Group g = createGroup(roleRepresentation);
            if (g != null) {
                return g;
            }
        }
        throw new GroupNotFoundException(identifier);
    }

    @Override
    public Group create(Group entity) throws SecurityManagementException {
        checkNotNull("entity",
                     entity);
        RealmResource realmResource = getRealmResource();
        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(entity.getName());
        roleRepresentation.setDescription(entity.getName());
        roleRepresentation.setScopeParamRequired(false);
        roleRepresentation.setId(entity.getName());
        roleRepresentation.setComposite(false);
        ClientResponse response = (ClientResponse) rolesResource.create(roleRepresentation);
        handleResponse(response);
        return entity;
    }

    // The Group class from Errai does not hava any attributes holder. Group name cannot be modified for a group. 
    // So currently a group cannot be updated.
    @Override
    public Group update(Group entity) throws SecurityManagementException {
        throw new UnsupportedServiceCapabilityException(Capability.CAN_UPDATE_GROUP);
    }

    @Override
    public void delete(String... identifiers) throws SecurityManagementException {
        checkNotNull("identifiers",
                     identifiers);
        RealmResource realmResource = getRealmResource();
        RolesResource rolesResource = realmResource.roles();
        for (String identifier : identifiers) {
            RoleResource roleResource = rolesResource.get(identifier);
            if (roleResource == null) {
                throw new GroupNotFoundException(identifier);
            }
            String response = roleResource.remove();
        }
    }

    @Override
    public GroupManagerSettings getSettings() {
        final Map<Capability, CapabilityStatus> capabilityStatusMap = new HashMap<Capability, CapabilityStatus>(8);
        for (final Capability capability : SecurityManagementUtils.GROUPS_CAPABILITIES) {
            capabilityStatusMap.put(capability,
                                    getCapabilityStatus(capability));
        }
        return new GroupManagerSettingsImpl(capabilityStatusMap,
                                            true);
    }

    @Override
    public void assignUsers(String name,
                            Collection<String> users) throws SecurityManagementException {
        checkNotNull("name",
                     name);
        if (users != null) {
            RealmResource realmResource = getRealmResource();
            UsersResource usersResource = realmResource.users();
            RolesResource rolesResource = realmResource.roles();
            RoleResource roleResource = rolesResource.get(name);
            List<RoleRepresentation> rolesToAdd = new ArrayList<RoleRepresentation>(1);
            rolesToAdd.add(getRoleRepresentation(name,
                                                 roleResource));
            for (String username : users) {
                UserResource userResource = getUserResource(usersResource,
                                                            username);
                if (userResource == null) {
                    throw new UserNotFoundException(username);
                }
                userResource.roles().realmLevel().add(rolesToAdd);
            }
        }
    }

    protected CapabilityStatus getCapabilityStatus(final Capability capability) {
        if (capability != null) {
            switch (capability) {
                case CAN_SEARCH_GROUPS:
                case CAN_ADD_GROUP:
                case CAN_READ_GROUP:
                case CAN_DELETE_GROUP:
                    return CapabilityStatus.ENABLED;
            }
        }
        return CapabilityStatus.UNSUPPORTED;
    }

    @Override
    public void destroy() throws Exception {
        getKeyCloakInstance().close();
    }
}
