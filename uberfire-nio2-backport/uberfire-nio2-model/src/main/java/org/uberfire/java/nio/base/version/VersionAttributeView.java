/*
 * Copyright 2013 JBoss Inc
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

package org.uberfire.java.nio.base.version;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.file.Path;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 *
 */
public abstract class VersionAttributeView<P extends Path>
        extends AbstractBasicFileAttributeView<P> {

    public static final String VERSION = "version";

    public VersionAttributeView( final P path ) {
        super( path );
    }

    @Override
    public String name() {
        return VERSION;
    }

    public abstract VersionAttributes readAttributes() throws IOException;

    @Override
    public Map<String, Object> readAttributes( final String... attributes ) {
        final VersionAttributes attrs = readAttributes();

        return new HashMap<String, Object>( super.readAttributes( attributes ) ) {{
            for ( final String attribute : attributes ) {
                checkNotEmpty( "attribute", attribute );

                if ( attribute.equals( "*" ) || attribute.equals( VERSION ) ) {
                    put( VERSION, attrs.history() );
                }

                if ( attribute.equals( "*" ) ) {
                    break;
                }
            }
        }};
    }
}
