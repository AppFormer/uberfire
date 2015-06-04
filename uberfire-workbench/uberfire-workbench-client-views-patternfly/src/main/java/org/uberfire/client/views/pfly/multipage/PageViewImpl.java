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

package org.uberfire.client.views.pfly.multipage;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import org.uberfire.client.workbench.widgets.multipage.PageView;

/**
 * Created by Cristiano Nicolai.
 */
public class PageViewImpl extends SimpleLayoutPanel implements PageView {

    private final PageImpl presenter;
    private final ScrollPanel sp = new ScrollPanel();

    public PageViewImpl( PageImpl presenter,
                         IsWidget widget ) {
        this.presenter = presenter;
        sp.setWidget( widget );
        setWidget( sp );
    }

    public void onFocus() {
        presenter.onFocus();
    }

    public void onLostFocus() {
        presenter.onLostFocus();
    }

    public ScrollPanel getSp() {
        return sp;
    }

}
