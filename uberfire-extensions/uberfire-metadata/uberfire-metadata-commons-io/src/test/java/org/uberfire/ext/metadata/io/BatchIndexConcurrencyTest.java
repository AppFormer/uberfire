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
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.*;

public class BatchIndexConcurrencyTest extends BaseIndexTest {

    private static final String REPO_NAME  = "temp-repo-batch-index-concurrency-test";

    @Override
    @SuppressWarnings("unchecked")
    protected IOService ioService() {
        if (ioService == null) {
            config = new LuceneConfigBuilder()
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .build();
            ioService = new IOServiceIndexedImpl(config.getIndexEngine(),
                                                 observer);
        }
        return ioService;
    }

    @Before
    public void setup() throws IOException {
        super.setup();
        observer.addInformationCallback(IndexObserverCallback.NOP);
        ioService.createDirectory( getBasePath( REPO_NAME ) );
    }

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{REPO_NAME};
    }

    @Test(timeout = 1000)
    //See https://bugzilla.redhat.com/show_bug.cgi?id=1288132
    public void testSingleConcurrentBatchIndexExecution() throws IOException, InterruptedException {
        observer.reset();
        observer.addInformationCallback(IndexObserverCallback.NOP);
        observer.addInformationCallback(IndexObserverCallback.NOP);
        observer.addInformationCallback(() -> assertEvents(3));

        //Make multiple requests for the FileSystem. We should only have one batch index operation
        final CountDownLatch startSignal = new CountDownLatch(1);
        for (int i = 0; i < 3; i++) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        startSignal.await();
                        ioService().getFileSystem(URI.create("git://"+REPO_NAME+"/file1"));
                    } catch (InterruptedException e) {
                        fail(e.getMessage());
                    }
                }
            };
            new Thread(r).start();
        }
        startSignal.countDown();

        observer.poll();
    }

    private void assertEvents(final int informationMessageCount) {
        System.out.println(observer.getInformationMessages());

        assertEquals(informationMessageCount,
                     observer.getInformationMessages().size());
        assertEquals(0,
                     observer.getWarningMessages().size());
        assertEquals(0,
                     observer.getErrorMessages().size());

        assertContains("Starting indexing of git://master@"+REPO_NAME+"/ ...",
                       observer.getInformationMessages());
        assertContains("Completed indexing of git://master@"+REPO_NAME+"/",
                       observer.getInformationMessages());
    }

    private void assertContains(final String expected,
                                final List<String> actual) {
        for (String msg : actual) {
            if (msg.equals(expected)) {
                return;
            }
        }
        final StringBuilder sb = new StringBuilder();
        for (String msg : actual) {
            sb.append("'").append(msg).append("'\n");
        }
        fail("Expected '" + expected + "' was not found in " + sb.toString());
    }
}