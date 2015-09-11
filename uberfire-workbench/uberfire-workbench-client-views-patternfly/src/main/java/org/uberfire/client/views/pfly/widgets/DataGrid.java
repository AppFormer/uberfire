/*
 *
 *  * Copyright 2015 JBoss Inc
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

import com.google.gwt.dom.client.Element;

/**
 * Created by Cristiano Nicolai.
 */
public class DataGrid<T> extends org.gwtbootstrap3.client.ui.gwt.DataGrid<T> {

    public DataGrid(){
        setHover( true );
        setStriped( true );
        setBordered( true );
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        fixTableStyle( this.getElement() );
    }

    public native void fixTableStyle( final Element e ) /*-{
        var table = $wnd.jQuery(e).find( "table" ).first();
        table.addClass( "table" );
        table.css( "margin-bottom", "0px" );
    }-*/;


}
