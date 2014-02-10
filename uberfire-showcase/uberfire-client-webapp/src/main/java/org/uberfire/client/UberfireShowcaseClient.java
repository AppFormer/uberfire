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
package org.uberfire.client;

import javax.annotation.PostConstruct;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.errai.ioc.client.api.EntryPoint;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import com.google.gwt.user.client.ui.Button;
import javax.inject.Inject;

@Templated("#template")
@EntryPoint
public class UberfireShowcaseClient extends Composite{

    @DataField
    private TextBox messageText = new TextBox();

    @DataField
    private TextBox resultText = new TextBox();

    @Inject
    @DataField
    private Button sendMessage;

    @PostConstruct
    public void setup()
    {
        sendMessage.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                resultText.setText( messageText.getText() );
            }
        });  
        RootPanel.get().add(this);
    }

}