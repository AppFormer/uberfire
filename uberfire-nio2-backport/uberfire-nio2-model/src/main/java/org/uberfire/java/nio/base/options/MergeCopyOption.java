/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.base.options;

import org.uberfire.java.nio.file.CopyOption;

/**
 * This is the CopyOption that allows to merge two branches
 * when executing copy method.
 * You have to apply it as the third parameter of FileSystemProvider.copy() method.
 */
public class MergeCopyOption implements CopyOption {

}
