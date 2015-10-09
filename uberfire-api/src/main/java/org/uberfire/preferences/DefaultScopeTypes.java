/*
 *   Copyright 2015 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.uberfire.preferences;

public enum DefaultScopeTypes implements ScopeType {
    USER {
        @Override
        public Scope toScope( final Object... params ) {
            return USER_SCOPE;
        }
    }, COMPONENT {
        @Override
        public Scope toScope( final Object... params ) {
            if ( params.length < 3 ) {
                throw new RuntimeException();
            }
            return null;
        }
    }, MODULE {
        @Override
        public Scope toScope( final Object... params ) {
            return null;
        }
    }, APP {
        @Override
        public Scope toScope( final Object... params ) {
            return null;
        }
    }, GLOBAL {
        @Override
        public Scope toScope( final Object... params ) {
            return GLOBAL_SCOPE;
        }
    };

    public abstract Scope toScope( Object... params );

    private static final Scope GLOBAL_SCOPE = new Scope() {
        @Override
        public Scope getParent() {
            return null;
        }

        @Override
        public String key() {
            return "global";
        }

        @Override
        public ScopeType getType() {
            return GLOBAL;
        }
    };

    private static final Scope USER_SCOPE = new Scope() {
        @Override
        public Scope getParent() {
            return GLOBAL_SCOPE;
        }

        @Override
        public String key() {
            return null;
        }

        @Override
        public ScopeType getType() {
            return USER;
        }
    };

}

