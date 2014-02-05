/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.workbench.events;

import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.UberFireEvent;

/**
 * An event raised before a Place is closed
 */
public class BeforeClosePlaceEvent extends UberFireEvent {

    private final PlaceRequest place;
    private final boolean force;

    public BeforeClosePlaceEvent( final PlaceRequest place ) {
        this.place = place;
        this.force = false;
    }

    public BeforeClosePlaceEvent( final PlaceRequest place,
                                  final boolean force ) {
        this.place = place;
        this.force = force;
    }

    public PlaceRequest getPlace() {
        return place;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public String toString() {
      return "BeforeClosePlaceEvent [place=" + place + "]";
    }

}
