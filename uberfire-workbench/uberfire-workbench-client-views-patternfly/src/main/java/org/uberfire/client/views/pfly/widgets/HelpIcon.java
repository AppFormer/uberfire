/*
 *
 *  * Copyright 2012 JBoss Inc
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  * use this file except in compliance with the License. You may obtain a copy of
 *  * the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations under
 *  * the License.
 *
 */

package org.uberfire.client.views.pfly.widgets;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.IconType;

/**
 * Created by Cristiano Nicolai.
 */
public class HelpIcon extends Composite {

    final Icon icon = new Icon( IconType.INFO_CIRCLE );
    final Popover popover = new Popover( icon );

    public HelpIcon(){
        initWidget( popover.asWidget() );
        addStyleName( "uf-help-icon" );
    }

    public void setHelpTitle( final String title ){
        popover.setTitle( title );
    }

    public void setHelpContent( final String content ) {
        popover.setContent( content );
    }

}
