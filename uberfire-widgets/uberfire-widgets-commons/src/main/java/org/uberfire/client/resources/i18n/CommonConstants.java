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

package org.uberfire.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;
import org.uberfire.mvp.Command;

/**
 *
 */
public interface CommonConstants
        extends Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String OK();

    String YES();

    String NO();

    String Information();

    String Close();

    String Error();

    String Warning();

    String ShowDetail();

    String AbstractTableOpen();

    String AbstractTablePleaseSelectAnItemToDelete();

    String AbstractTableRefreshList();

    String AbstractTableOpenSelected();

    String AbstractTableFileURI();

    String ReOpen();

    String Ignore();

    String ForceSave();

    String Cancel();

    String ConcurrentIssue();

    String ConcurrentUpdate( String identity,
                             String pathURI );

    String ConcurrentRename( String identity,
                             String sourceURI,
                             String targetURI );

    String ConcurrentDelete( String identity,
                             String pathURI );

    String ChooseFile();

    String Upload();

    String More();
    String Active();
}
