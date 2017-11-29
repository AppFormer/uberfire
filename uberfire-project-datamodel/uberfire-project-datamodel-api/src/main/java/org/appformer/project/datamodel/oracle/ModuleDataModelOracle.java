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
 *
 */

package org.appformer.project.datamodel.oracle;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ModuleDataModelOracle {

    void setModuleName(final String projectName);

    void addModuleModelFields(final Map<String, ModelField[]> modelFields);

    void addModuleFieldParametersType(final Map<String, String> fieldParametersType);

    void addModuleEventTypes(final Map<String, Boolean> eventTypes);

    void addModuleTypeSources(final Map<String, TypeSource> typeSources);

    void addModuleSuperTypes(final Map<String, List<String>> superTypes);

    void addModuleTypeAnnotations(final Map<String, Set<Annotation>> annotations);

    void addModuleTypeFieldsAnnotations(final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations);

    void addModuleJavaEnumDefinitions(final Map<String, String[]> enumDefinitions);

    void addModuleMethodInformation(final Map<String, List<MethodInfo>> methodInformation);

    void addModuleCollectionTypes(final Map<String, Boolean> collectionTypes);

    void addModulePackageNames(final List<String> packageNames);

    String getModuleName();

    Map<String, ModelField[]> getModuleModelFields();

    Map<String, String> getModuleFieldParametersType();

    Map<String, Boolean> getModuleEventTypes();

    Map<String, TypeSource> getModuleTypeSources();

    Map<String, List<String>> getModuleSuperTypes();

    Map<String, Set<Annotation>> getModuleTypeAnnotations();

    Map<String, Map<String, Set<Annotation>>> getModuleTypeFieldsAnnotations();

    Map<String, String[]> getModuleJavaEnumDefinitions();

    Map<String, List<MethodInfo>> getModuleMethodInformation();

    Map<String, Boolean> getModuleCollectionTypes();

    List<String> getModulePackageNames();
}
