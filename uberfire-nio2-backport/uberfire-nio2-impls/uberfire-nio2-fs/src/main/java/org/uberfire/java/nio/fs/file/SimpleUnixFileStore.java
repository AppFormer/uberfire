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

package org.uberfire.java.nio.fs.file;

import java.io.File;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.Path;

public class SimpleUnixFileStore extends BaseSimpleFileStore {

    SimpleUnixFileStore( final Path path ) {
        super( path );
    }

    @Override
    public String name() {
        return "/";
    }

    @Override
    public long getTotalSpace() throws IOException {
        return File.listRoots()[ 0 ].getTotalSpace();
    }

    @Override
    public long getUsableSpace() throws IOException {
        return File.listRoots()[ 0 ].getUsableSpace();
    }

    @Override
    public long getUnallocatedSpace() throws IOException {
        throw new UnsupportedOperationException();
    }

}
