/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.backend.lucene.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.model.schema.MetaType;

/**
 *
 */
public final class KObjectUtil {

    public static KObject toKObject(final Document document) {
        return new KObject() {

            @Override
            public String getId() {
                return document.get(MetaObject.META_OBJECT_ID);
            }

            @Override
            public MetaType getType() {
                return () -> document.get(MetaObject.META_OBJECT_TYPE);
            }

            @Override
            public String getClusterId() {
                return document.get(MetaObject.META_OBJECT_CLUSTER_ID);
            }

            @Override
            public String getSegmentId() {
                return document.get(MetaObject.META_OBJECT_SEGMENT_ID);
            }

            @Override
            public String getKey() {
                return document.get(MetaObject.META_OBJECT_KEY);
            }

            @Override
            public boolean equals(final Object obj) {
                if (obj == null) {
                    return false;
                }
                if (!(obj instanceof KObject)) {
                    return false;
                }
                final KObject kobj = (KObject) obj;
                return getClusterId().equals(kobj.getClusterId()) &&
                        getId().equals(kobj.getId()) &&
                        getKey().equals(kobj.getKey()) &&
                        getType().getName().equals(kobj.getType().getName());
            }

            @Override
            public int hashCode() {
                int result = getId().hashCode();
                result = 31 * result + getClusterId().hashCode();
                result = 31 * result + getKey().hashCode();
                result = 31 * result + getType().getName().hashCode();
                return result;
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                final List<KProperty<?>> kProperties = new ArrayList<KProperty<?>>();
                for (final IndexableField indexableField : document) {
                    if (isExtension(indexableField.name())) {
                        kProperties.add(new KProperty<Object>() {
                            @Override
                            public String getName() {
                                return indexableField.name();
                            }

                            @Override
                            public Object getValue() {
                                return indexableField.stringValue();
                            }

                            @Override
                            public boolean isSearchable() {
                                return true;
                            }
                        });
                    }
                }

                return kProperties;
            }

            @Override
            public boolean fullText() {
                return true;
            }

            private boolean isExtension(final String name) {
                return !(name.equals(MetaObject.META_OBJECT_ID) ||
                        name.equals(MetaObject.META_OBJECT_TYPE) ||
                        name.equals(MetaObject.META_OBJECT_CLUSTER_ID) ||
                        name.equals(MetaObject.META_OBJECT_SEGMENT_ID) ||
                        name.equals(MetaObject.META_OBJECT_KEY));
            }
        };
    }
}
