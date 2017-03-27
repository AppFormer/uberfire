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

$registerEditor({
    "id": "dynamic editor",
    "type": "editor",
    priority: "1000",
    "templateUrl": "dynamic_editor.html",
    "resourceType": "DynamicResourceType",
    "on_concurrent_update": function () {
        alert('on_concurrent_update callback')
        $vfs_readAllString(document.getElementById('filename').innerHTML, function (a) {
            document.getElementById('editor').value = a;
        });
    },
    "on_startup": function (uri) {
        $vfs_readAllString(uri, function (a) {
            alert('sample on_startup callback')
        });
    },
    "on_open": function (uri) {
        $vfs_readAllString(uri, function (a) {
            document.getElementById('editor').value = a;
        });
        document.getElementById('filename').innerHTML = uri;
    }
});