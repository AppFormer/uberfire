package org.uberfire.commons.lifecycle;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PriorityDisposableRegistry {

    private static Set<PriorityDisposable> disposables = new HashSet<PriorityDisposable>();
    private static Map<String, Object> registry = new HashMap<String, Object>();

    private PriorityDisposableRegistry() {
    }

    public static void register( final PriorityDisposable priorityDisposable ) {
        disposables.add( priorityDisposable );
    }

    public static void clear() {
        disposables.clear();
        registry.clear();
    }

    public static Collection<PriorityDisposable> getDisposables() {
        return disposables;
    }

    public static void register( final String refName,
                                 final Object disposable ) {
        registry.put( refName, disposable );
    }

    public static Object get( final String refName ) {
        return registry.get( refName );
    }
}
