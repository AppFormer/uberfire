package org.uberfire.java.nio.fs.jgit.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class LazyFileSystemsSupplier<T> implements Supplier<T> {

    final Supplier<T> delegate;
    ConcurrentMap<Class<?>, T> map = new ConcurrentHashMap<>(1);

    private LazyFileSystemsSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {

        T t = this.map.computeIfAbsent(LazyFileSystemsSupplier.class,
                                       k -> this.delegate.get());
        System.out.println("Lazy Supplier executed with return object: " + t.hashCode());
        return t;
    }

    public static <T> Supplier<T> of(Supplier<T> provider) {
        return new LazyFileSystemsSupplier<>(provider);
    }

}