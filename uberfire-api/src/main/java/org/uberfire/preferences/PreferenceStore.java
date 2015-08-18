package org.uberfire.preferences;

public interface PreferenceStore extends ScopedPreferenceStoreFactory {

    Object get(String key);

    void put(String key, Object value);

    interface ScopedPreferenceStore {

        void put(String key, Object value);

        Object get(String key);


    }


}
