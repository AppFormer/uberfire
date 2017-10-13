/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.io.attribute;

import java.util.HashMap;
import java.util.Map;

import static org.kie.soup.commons.validation.Preconditions.checkNotEmpty;
import static org.uberfire.io.attribute.DublinCoreView.CONTRIBUTOR;
import static org.uberfire.io.attribute.DublinCoreView.COVERAGE;
import static org.uberfire.io.attribute.DublinCoreView.CREATION_TIME;
import static org.uberfire.io.attribute.DublinCoreView.CREATOR;
import static org.uberfire.io.attribute.DublinCoreView.DESCRIPTION;
import static org.uberfire.io.attribute.DublinCoreView.FORMAT;
import static org.uberfire.io.attribute.DublinCoreView.IDENTIFIER;
import static org.uberfire.io.attribute.DublinCoreView.LANGUAGE;
import static org.uberfire.io.attribute.DublinCoreView.LAST_ACCESS_TIME;
import static org.uberfire.io.attribute.DublinCoreView.LAST_MODIFIED_TIME;
import static org.uberfire.io.attribute.DublinCoreView.PUBLISHER;
import static org.uberfire.io.attribute.DublinCoreView.RELATION;
import static org.uberfire.io.attribute.DublinCoreView.RIGHTS;
import static org.uberfire.io.attribute.DublinCoreView.SOURCE;
import static org.uberfire.io.attribute.DublinCoreView.SUBJECT;
import static org.uberfire.io.attribute.DublinCoreView.TITLE;
import static org.uberfire.io.attribute.DublinCoreView.TYPE;

/**
 *
 */
public final class DublinCoreAttributesUtil {

    private DublinCoreAttributesUtil() {

    }

    public static Map<String, Object> cleanup(final Map<String, Object> _attrs) {
        final Map<String, Object> attrs = new HashMap<String, Object>(_attrs);

        for (final String key : _attrs.keySet()) {
            if (key.startsWith(TITLE) || key.startsWith(CREATOR) ||
                    key.startsWith(SUBJECT) || key.startsWith(DESCRIPTION) ||
                    key.startsWith(PUBLISHER) || key.startsWith(CONTRIBUTOR) ||
                    key.startsWith(TYPE) || key.startsWith(FORMAT) ||
                    key.startsWith(IDENTIFIER) || key.startsWith(SOURCE) ||
                    key.startsWith(LANGUAGE) || key.startsWith(RELATION) ||
                    key.startsWith(COVERAGE) || key.startsWith(RIGHTS)) {
                attrs.put(key,
                          null);
            }
        }

        return attrs;
    }

    public static Map<String, Object> toMap(final DublinCoreAttributes attrs,
                                            final String... attributes) {

        return new HashMap<String, Object>() {{
            for (final String attribute : attributes) {
                checkNotEmpty("attribute",
                              attribute);

                if (attribute.equals("*") || attribute.equals(TITLE)) {
                    for (int i = 0; i < attrs.titles().size(); i++) {
                        final String content = attrs.titles().get(i);
                        put(buildAttrName(TITLE,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(CREATOR)) {
                    for (int i = 0; i < attrs.creators().size(); i++) {
                        final String content = attrs.creators().get(i);
                        put(buildAttrName(CREATOR,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(SUBJECT)) {
                    for (int i = 0; i < attrs.subjects().size(); i++) {
                        final String content = attrs.subjects().get(i);
                        put(buildAttrName(SUBJECT,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(DESCRIPTION)) {
                    for (int i = 0; i < attrs.descriptions().size(); i++) {
                        final String content = attrs.descriptions().get(i);
                        put(buildAttrName(DESCRIPTION,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(PUBLISHER)) {
                    for (int i = 0; i < attrs.publishers().size(); i++) {
                        final String content = attrs.publishers().get(i);
                        put(buildAttrName(PUBLISHER,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(CONTRIBUTOR)) {
                    for (int i = 0; i < attrs.contributors().size(); i++) {
                        final String content = attrs.contributors().get(i);
                        put(buildAttrName(CONTRIBUTOR,
                                          i),
                            content);
                    }
                }

                if (attribute.equals("*") || attribute.equals(LAST_MODIFIED_TIME)) {
                    put(LAST_MODIFIED_TIME,
                        null);
                }
                if (attribute.equals("*") || attribute.equals(LAST_ACCESS_TIME)) {
                    put(LAST_ACCESS_TIME,
                        null);
                }
                if (attribute.equals("*") || attribute.equals(CREATION_TIME)) {
                    put(CREATION_TIME,
                        null);
                }

                if (attribute.equals("*") || attribute.equals(TYPE)) {
                    for (int i = 0; i < attrs.types().size(); i++) {
                        final String content = attrs.types().get(i);
                        put(buildAttrName(TYPE,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(FORMAT)) {
                    for (int i = 0; i < attrs.formats().size(); i++) {
                        final String content = attrs.formats().get(i);
                        put(buildAttrName(FORMAT,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(IDENTIFIER)) {
                    for (int i = 0; i < attrs.identifiers().size(); i++) {
                        final String content = attrs.identifiers().get(i);
                        put(buildAttrName(IDENTIFIER,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(SOURCE)) {
                    for (int i = 0; i < attrs.sources().size(); i++) {
                        final String content = attrs.sources().get(i);
                        put(buildAttrName(SOURCE,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(LANGUAGE)) {
                    for (int i = 0; i < attrs.languages().size(); i++) {
                        final String content = attrs.languages().get(i);
                        put(buildAttrName(LANGUAGE,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(RELATION)) {
                    for (int i = 0; i < attrs.relations().size(); i++) {
                        final String content = attrs.relations().get(i);
                        put(buildAttrName(RELATION,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(COVERAGE)) {
                    for (int i = 0; i < attrs.coverages().size(); i++) {
                        final String content = attrs.coverages().get(i);
                        put(buildAttrName(COVERAGE,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*") || attribute.equals(RIGHTS)) {
                    for (int i = 0; i < attrs.rights().size(); i++) {
                        final String content = attrs.rights().get(i);
                        put(buildAttrName(RIGHTS,
                                          i),
                            content);
                    }
                }
                if (attribute.equals("*")) {
                    break;
                }
            }
        }};
    }

    private static String buildAttrName(final String title,
                                        final int i) {
        return title + "[" + i + "]";
    }
}
