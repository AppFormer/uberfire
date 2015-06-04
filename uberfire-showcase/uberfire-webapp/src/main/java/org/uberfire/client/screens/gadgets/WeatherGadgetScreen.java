/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.screens.gadgets;

import javax.enterprise.context.Dependent;

import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;

@Dependent
@WorkbenchScreen( identifier = "WeatherGadget" )
public class WeatherGadgetScreen extends AbstractGadgetScreen {

    private static final String URL = "http://www.gmodules.com/ig/ifr?url=http://www.meteogroup.com/meteo/gadgets/wetter24.xml&amp;up_loc=&amp;up_loccode=&amp;up_temp_unit=&amp;synd=open&amp;w=320&amp;h=200&amp;title=Weather&amp;lang=all&amp;country=ALL&amp;border=http%3A%2F%2Fwww.gmodules.com%2Fig%2Fimages%2F&amp;output=js";

    public WeatherGadgetScreen() {
        super( URL );
    }

    @WorkbenchPartTitle
    public String getName() {
        return "Weather";
    }

}
