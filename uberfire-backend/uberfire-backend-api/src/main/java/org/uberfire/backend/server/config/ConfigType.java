package org.uberfire.backend.server.config;

public enum ConfigType {

    GLOBAL( ".global" ),
    REPOSITORY( ".repository" ),
    ORGANIZATIONAL_UNIT( ".organizationalunit" ),
    PROJECT( ".project" ),
    EDITOR( ".editor" ),
    DEPLOYMENT( ".deployment" );

    private String ext;

    public String getExt() {
        return this.ext;
    }

    private ConfigType( String ext ) {
        this.ext = ext;
    }
}
