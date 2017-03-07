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

package org.uberfire.client.splash;

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.workbench.model.SplashScreenFilter;
import org.uberfire.workbench.model.impl.SplashScreenFilterImpl;

import static java.util.Collections.emptyList;

@Dependent
public class JSNativeSplashScreen extends JSNativePlugin {

    private Integer bodyHeight = null;

    @Inject
    private WorkbenchServicesProxy wbServices;

    private static native boolean getDisplayNextTimeFunctionResult(final JavaScriptObject o) /*-{
        var result = o.display_next_time();
        if (typeof result === "boolean") {
            return result;
        }
        return false;
    }-*/;

    private static native boolean getDisplayNextTime(final JavaScriptObject o) /*-{
        return o.display_next_time;
    }-*/;

    private static native JsArrayString getInterceptionPointsFunctionResult(final JavaScriptObject o) /*-{
        var result = o.interception_points();
        if (result instanceof Array) {
            return result;
        }
        return [];
    }-*/;

    private static native JsArrayString getInterceptionPoints(final JavaScriptObject o) /*-{
        return o.interception_points;
    }-*/;

    private static native int getBodyHeight(final JavaScriptObject o) /*-{
        return o.body_height;
    }-*/;

    private static native boolean getIsEnabled(final JavaScriptObject o) /*-{
        return o.enabled;
    }-*/;

    public WorkbenchServicesProxy getWbServices() {
        return wbServices;
    }

    protected void buildElement() {
        super.buildElement();

        if (hasIntProperty(obj,
                           "body_height")) {
            bodyHeight = getBodyHeight(obj);
        } else {
            bodyHeight = null;
        }
    }

    public Integer getBodyHeight() {
        return bodyHeight;
    }

    public SplashScreenFilter buildFilter() {
        boolean displayNextTime = true;
        JsArrayString interceptionPoints = null;

        if (hasMethod(obj,
                      "display_next_time")) {
            displayNextTime = getDisplayNextTimeFunctionResult(obj);
        } else if (hasBooleanProperty(obj,
                                      "display_next_time")) {
            displayNextTime = getDisplayNextTime(obj);
        }

        if (hasMethod(obj,
                      "interception_points")) {
            interceptionPoints = getInterceptionPointsFunctionResult(obj);
        } else {
            interceptionPoints = getInterceptionPoints(obj);
        }

        return new SplashScreenFilterImpl(getId(),
                                          displayNextTime,
                                          toCollection(interceptionPoints));
    }

    public boolean isEnabled() {
        if (hasBooleanProperty(obj,
                               "enabled")) {
            return getIsEnabled(obj);
        }
        return true;
    }

    private Collection<String> toCollection(final JsArrayString interceptionPoints) {
        if (interceptionPoints == null || interceptionPoints.length() == 0) {
            return emptyList();
        }

        final Collection<String> result = new ArrayList<String>();
        for (int i = 0; i < interceptionPoints.length(); i++) {
            result.add(interceptionPoints.get(i));
        }

        return result;
    }
}
