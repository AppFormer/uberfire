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

package org.appformer.project.datamodel.commons.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.appformer.project.datamodel.oracle.Annotation;
import org.appformer.project.datamodel.oracle.MethodInfo;
import org.appformer.project.datamodel.oracle.ModelField;
import org.appformer.project.datamodel.oracle.ModuleDataModelOracle;
import org.appformer.project.datamodel.oracle.TypeSource;

/**
 * Default implementation of DataModelOracle
 */
public class ModuleDataModelOracleImpl implements ModuleDataModelOracle {

    //Module name
    protected String moduleName;

    //Fact Types and their corresponding fields
    protected Map<String, ModelField[]> moduleModelFields = new HashMap<String, ModelField[]>();

    //Map of the field that contains the parametrized type of a collection
    //for example given "List<String> name", key = "name" value = "String"
    protected Map<String, String> moduleFieldParametersType = new HashMap<String, String>();

    //Map {factType, isEvent} to determine which Fact Type can be treated as events.
    protected Map<String, Boolean> moduleEventTypes = new HashMap<String, Boolean>();

    //Map {factType, TypeSource} to determine where a Fact Type as defined.
    protected Map<String, TypeSource> moduleTypeSources = new HashMap<String, TypeSource>();

    //Map {factType, superType} to determine the Super Type of a FactType.
    protected Map<String, List<String>> moduleSuperTypes = new HashMap<String, List<String>>();

    //Map {factType, Set<Annotation>} containing the FactType's annotations.
    protected Map<String, Set<Annotation>> moduleTypeAnnotations = new HashMap<String, Set<Annotation>>();

    //Map {factType, Map<fieldName, Set<Annotation>>} containing the FactType's Field annotations.
    protected Map<String, Map<String, Set<Annotation>>> moduleTypeFieldsAnnotations = new HashMap<String, Map<String, Set<Annotation>>>();

    // Scoped (current package and imports) map of { TypeName.field : String[] } - where a list is valid values to display in a drop down for a given Type.field combination.
    protected Map<String, String[]> moduleJavaEnumDefinitions = new HashMap<String, String[]>();

    //Method information used (exclusively) by ExpressionWidget and ActionCallMethodWidget
    protected Map<String, List<MethodInfo>> moduleMethodInformation = new HashMap<String, List<MethodInfo>>();

    // A map of FactTypes {factType, isCollection} to determine which Fact Types are Collections.
    protected Map<String, Boolean> moduleCollectionTypes = new HashMap<String, Boolean>();

    // List of available package names
    private List<String> modulePackageNames = new ArrayList<String>();

    @Override
    public void setModuleName(final String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public void addModuleModelFields(final Map<String, ModelField[]> modelFields) {
        this.moduleModelFields.putAll(modelFields);
    }

    @Override
    public void addModuleFieldParametersType(final Map<String, String> fieldParametersType) {
        this.moduleFieldParametersType.putAll(fieldParametersType);
    }

    @Override
    public void addModuleEventTypes(final Map<String, Boolean> eventTypes) {
        this.moduleEventTypes.putAll(eventTypes);
    }

    @Override
    public void addModuleTypeSources(final Map<String, TypeSource> typeSources) {
        this.moduleTypeSources.putAll(typeSources);
    }

    @Override
    public void addModuleSuperTypes(final Map<String, List<String>> superTypes) {
        this.moduleSuperTypes.putAll(superTypes);
    }

    @Override
    public void addModuleTypeAnnotations(final Map<String, Set<Annotation>> annotations) {
        this.moduleTypeAnnotations.putAll(annotations);
    }

    @Override
    public void addModuleTypeFieldsAnnotations(final Map<String, Map<String, Set<Annotation>>> typeFieldsAnnotations) {
        this.moduleTypeFieldsAnnotations.putAll(typeFieldsAnnotations);
    }

    @Override
    public void addModuleJavaEnumDefinitions(final Map<String, String[]> dataEnumLists) {
        this.moduleJavaEnumDefinitions.putAll(dataEnumLists);
    }

    @Override
    public void addModuleMethodInformation(final Map<String, List<MethodInfo>> methodInformation) {
        this.moduleMethodInformation.putAll(methodInformation);
    }

    @Override
    public void addModuleCollectionTypes(final Map<String, Boolean> collectionTypes) {
        this.moduleCollectionTypes.putAll(collectionTypes);
    }

    @Override
    public void addModulePackageNames(final List<String> packageNames) {
        this.modulePackageNames.addAll(packageNames);
    }

    @Override
    public String getModuleName() {
        return this.moduleName;
    }

    @Override
    public Map<String, ModelField[]> getModuleModelFields() {
        return this.moduleModelFields;
    }

    @Override
    public Map<String, String> getModuleFieldParametersType() {
        return this.moduleFieldParametersType;
    }

    @Override
    public Map<String, Boolean> getModuleEventTypes() {
        return this.moduleEventTypes;
    }

    @Override
    public Map<String, TypeSource> getModuleTypeSources() {
        return this.moduleTypeSources;
    }

    @Override
    public Map<String, List<String>> getModuleSuperTypes() {
        return this.moduleSuperTypes;
    }

    @Override
    public Map<String, Set<Annotation>> getModuleTypeAnnotations() {
        return this.moduleTypeAnnotations;
    }

    @Override
    public Map<String, Map<String, Set<Annotation>>> getModuleTypeFieldsAnnotations() {
        return this.moduleTypeFieldsAnnotations;
    }

    @Override
    public Map<String, String[]> getModuleJavaEnumDefinitions() {
        return this.moduleJavaEnumDefinitions;
    }

    @Override
    public Map<String, List<MethodInfo>> getModuleMethodInformation() {
        return this.moduleMethodInformation;
    }

    @Override
    public Map<String, Boolean> getModuleCollectionTypes() {
        return this.moduleCollectionTypes;
    }

    @Override
    public List<String> getModulePackageNames() {
        return this.modulePackageNames;
    }
}

