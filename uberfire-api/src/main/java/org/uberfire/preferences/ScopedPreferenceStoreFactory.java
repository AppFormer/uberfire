package org.uberfire.preferences;

public interface ScopedPreferenceStoreFactory {
    PreferenceStore.ScopedPreferenceStore forScope(Scope scope);
}
