/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.metadata.io;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

public class IOSearchServiceImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test//(timeout = 1000)
    public void testFullTextSearch() throws IOException, InterruptedException {
        final Path path1 = getBasePath(this.getClass().getSimpleName()).resolve("g.txt");
        final Path path2 = getBasePath(this.getClass().getSimpleName()).resolve("a.txt");
        final Path path3 = getBasePath(this.getClass().getSimpleName()).resolve("the.txt");

        final IOSearchServiceImpl searchIndex = new IOSearchServiceImpl(config.getSearchIndex(),
                                                                        ioService());
        observer.addInformationCallback(IndexObserverCallback.NOP);
        observer.addInformationCallback(IndexObserverCallback.NOP);
        observer.addInformationCallback(() -> assertIndex(path1.getRoot(),
                                                          searchIndex));

        ioService().write(path1,
                          "ooooo!");

        ioService().write(path2,
                          "ooooo!");

        ioService().write(path3,
                          "ooooo!");

        observer.poll();
    }

    private void assertIndex(final Path path,
                             final IOSearchServiceImpl searchIndex) throws IOException {
        {
            final List<Path> result = searchIndex.fullTextSearch("g",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 path);

            assertEquals(1,
                         result.size());
        }

        {
            final List<Path> result = searchIndex.fullTextSearch("a",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 path);

            assertEquals(1,
                         result.size());
        }

        {
            final List<Path> result = searchIndex.fullTextSearch("the",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 path);

            assertEquals(1,
                         result.size());
        }

        {
            final List<Path> result = searchIndex.fullTextSearch("",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 path);

            assertEquals(0,
                         result.size());
        }

        {
            try {
                searchIndex.fullTextSearch(null,
                                           new IOSearchService.NoOpFilter(),
                                           path);
                fail();
            } catch (final IllegalArgumentException ignored) {
            }
        }
    }
}
