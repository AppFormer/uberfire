package org.uberfire.ext.metadata.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaProperty;
import org.uberfire.ext.metadata.model.schema.MetaType;

public class MetaObjectImpl implements MetaObject {

    private final MetaType metaType;
    private final Set<MetaProperty> properties;

    public MetaObjectImpl(MetaType metaType,
                          Set<MetaProperty> properties) {
        this.metaType = metaType;

        if (properties == null) {
            this.properties = new HashSet<>();
        } else {
            this.properties = properties;
        }
    }

    @Override
    public MetaType getType() {
        return this.metaType;
    }

    @Override
    public Collection<MetaProperty> getProperties() {
        return this.properties;
    }

    @Override
    public Optional<MetaProperty> getProperty(String name) {
        return this.getProperties()
                .stream()
                .filter(metaProperty -> metaProperty.getName()
                        .equals(name))
                .findFirst();
    }

    @Override
    public void addProperty(MetaProperty metaProperty) {
        this.properties.add(metaProperty);
    }
}
