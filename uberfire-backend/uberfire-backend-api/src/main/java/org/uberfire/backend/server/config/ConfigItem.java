package org.uberfire.backend.server.config;

public class ConfigItem<T> {

    private String name;
    private T value;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue( T value ) {
        this.value = value;
    }

}
