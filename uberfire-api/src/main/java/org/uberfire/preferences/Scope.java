package org.uberfire.preferences;


public enum Scope {
    USER, APP, MODULE, PROJECT;

    public String key() {
        return toString().toLowerCase();
    }
}
