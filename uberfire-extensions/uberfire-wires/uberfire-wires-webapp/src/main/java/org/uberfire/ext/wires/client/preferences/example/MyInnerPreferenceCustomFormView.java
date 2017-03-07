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

package org.uberfire.ext.wires.client.preferences.example;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.wires.shared.preferences.bean.MyInnerPreference;

@Dependent
@Templated
public class MyInnerPreferenceCustomFormView implements IsElement,
                                                        MyInnerPreferenceCustomForm.View {

    @Inject
    @DataField("text")
    TextInput text;
    private MyInnerPreferenceCustomForm presenter;

    @Override
    public void init(final MyInnerPreferenceCustomForm presenter) {
        this.presenter = presenter;

        text.setValue(presenter.getPreference().getText());
    }

    public void updatePreference(final MyInnerPreference preference) {
        preference.setText(text.getValue());
    }
}
