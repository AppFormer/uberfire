/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ErrorPopupTest {

    @Mock
    Modal modal;

    @Mock
    InlineNotification notification;

    @InjectMocks
    ErrorPopup popup;

    @Test
    public void testInlineNotificationType() {
        popup.init();

        verify(notification).setType(InlineNotification.InlineNotificationType.DANGER);
    }

    @Test
    public void testShowError() {
        final String error = "errormessage";
        popup.showError(error);

        verify(notification).setMessage(error);
        verify(modal).show();
    }

    @Test
    public void testClose() {
        popup.onCloseClick(null);

        verify(modal).hide();
    }

    @Test
    public void testOk() {
        popup.onOkClick(null);

        verify(modal).hide();
    }
}