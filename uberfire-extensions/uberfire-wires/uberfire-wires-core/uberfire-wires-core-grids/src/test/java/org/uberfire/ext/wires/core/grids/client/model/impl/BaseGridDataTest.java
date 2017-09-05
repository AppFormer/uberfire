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

package org.uberfire.ext.wires.core.grids.client.model.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BaseGridDataTest {

    @Test
    public void testReindexColumns() {

        final GridColumn<?> gridColumn1 = makeGridColumn(8);
        final GridColumn<?> gridColumn2 = makeGridColumn(7);
        final GridColumn<?> gridColumn3 = makeGridColumn(6);

        final BaseGridData gridData = new BaseGridData() {{
            columns = asList(gridColumn1,
                             gridColumn2,
                             gridColumn3);
        }};

        gridData.reindexColumns();

        verify(gridColumn1).setIndex(0);
        verify(gridColumn2).setIndex(1);
        verify(gridColumn3).setIndex(2);
    }

    private GridColumn<?> makeGridColumn(final int index) {

        final GridColumn<?> mock = mock(GridColumn.class);

        doReturn(index).when(mock).getIndex();

        return mock;
    }
}
