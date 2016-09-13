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

package org.uberfire.client.docks.view.bars;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.docks.UberfireDockPosition;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DocksExpandedBarTest {

    private DocksExpandedBar docksExpandedBar;

    @GwtMock
    FlowPanel titlePanel;

    @Before
    public void setup() {
        docksExpandedBar = spy( new DocksExpandedBar( UberfireDockPosition.WEST ) );
        when( docksExpandedBar.getOffsetHeight() ).thenReturn( 200 );
        when( docksExpandedBar.getOffsetWidth() ).thenReturn( 200 );
        when( titlePanel.getOffsetHeight() ).thenReturn( 100 );
    }

    @Test
    public void onResizeTest() {
        docksExpandedBar.onResize();
        verify( docksExpandedBar, times( 1 ) ).resizeTargetPanel();
    }

    @Test
    public void resizeAfterTitleSetTest() {
        when( docksExpandedBar.isTitleSet() ).thenReturn( true );
        docksExpandedBar.resizeTargetPanel();
        verify( docksExpandedBar, times( 1 ) ).setPanelSize( 200, 100 );
    }

    @Test
    public void resizeBeforeTitleSetTest() {
        when( docksExpandedBar.isTitleSet() ).thenReturn( false );
        docksExpandedBar.resizeTargetPanel();
        verify( docksExpandedBar, times( 1 ) ).setPanelSize( 200, 200 );
    }

}
