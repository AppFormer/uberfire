/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak.client.resource;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.keycloak.representations.idm.RoleRepresentation;

/**
 * @since 0.9.0
 */
public interface RolesResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RoleRepresentation> list();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(RoleRepresentation roleRepresentation);

    @Path("{roleName}")
    public RoleResource get(@PathParam("roleName") String roleName);

    @Path("{role-name}")
    @DELETE
    public String deleteRole(final @PathParam("role-name") String roleName);
}
