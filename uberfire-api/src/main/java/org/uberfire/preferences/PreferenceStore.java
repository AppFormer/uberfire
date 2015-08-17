package org.uberfire.preferences;

public interface PreferenceStore {

    ScopedPreferenceStore forScope(Scope scope);

    Object get(String key);

    void put(String key, Object value);

    interface ScopedPreferenceStore {

        void put(String key, Object value);

        Object get(String key);


    }


}
