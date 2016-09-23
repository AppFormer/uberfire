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

package org.uberfire.ext.preferences.backend;

import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreference;

@WorkbenchPreference(root = false, bundleKey = "MyInnerPreference2.Label")
public class MyInnerPreference2 implements BasePreference<MyInnerPreference2> {

    @Property(bundleKey = "MyInnerPreference2.Text")
    String text;

    @Property(inherited = true, bundleKey = "MyInnerPreference2.MyInheritedPreference2")
    MyInheritedPreference2 myInheritedPreference2;
}
