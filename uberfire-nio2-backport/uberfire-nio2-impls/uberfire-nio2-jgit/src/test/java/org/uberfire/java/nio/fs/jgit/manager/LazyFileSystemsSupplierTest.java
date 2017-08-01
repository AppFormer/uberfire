package org.uberfire.java.nio.fs.jgit.manager;

import java.util.function.Supplier;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LazyFileSystemsSupplierTest {

    public static int instanceCount = 0;

    @Test
    public void supplierTest() {

        getSupplier().get();
        getSupplier().get();
        assertEquals(2,
                     instanceCount);

        final Supplier<DummyObject> supplier = getLazySupplier();
        supplier.get();
        supplier.get();
        supplier.get();
        assertEquals(3,
                     instanceCount);

    }

    Supplier<DummyObject> getLazySupplier() {
        return LazyFileSystemsSupplier.of(getSupplier());
    }

    Supplier<DummyObject> getSupplier() {
        return () -> new DummyObject();
    }

    private class DummyObject {

        public DummyObject() {
            test();
            instanceCount++;
        }

        public void test() {
            System.out.println("new Instance");
        }
    }
}