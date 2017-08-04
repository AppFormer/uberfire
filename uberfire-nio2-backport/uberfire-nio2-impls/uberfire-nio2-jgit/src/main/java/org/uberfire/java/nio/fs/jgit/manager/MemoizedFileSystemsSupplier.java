package org.uberfire.java.nio.fs.jgit.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class MemoizedFileSystemsSupplier<T> implements Supplier<T> {

    final Supplier<T> delegate;
    ConcurrentMap<Class<?>, T> map = new ConcurrentHashMap<>(1);

    private MemoizedFileSystemsSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {

        T t = this.map.computeIfAbsent(MemoizedFileSystemsSupplier.class,
                                       k -> this.delegate.get());
        return t;
    }

    public static <T> Supplier<T> of(Supplier<T> provider) {
        return new MemoizedFileSystemsSupplier<>(provider);
    }
}