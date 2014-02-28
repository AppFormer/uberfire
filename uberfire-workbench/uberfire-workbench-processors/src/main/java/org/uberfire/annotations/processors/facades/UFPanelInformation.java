package org.uberfire.annotations.processors.facades;

import java.util.List;

public class UFPanelInformation {

    private String fieldName;
    private List<String> uFParts;
    private boolean isDefault;
    private String panelType;

    public void setDefault( boolean isDefault ) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setFieldName( String fieldName ) {
        this.fieldName = fieldName;
    }

    public List<String> getuFParts() {
        return uFParts;
    }

    public void setUFParts( List<String> uFParts ) {
        this.uFParts = uFParts;
    }

    public void setPanelType( String panelType ) {
        this.panelType = panelType;
    }

    public String getPanelType() {
        return panelType;
    }

    public String getFieldName() {
        return fieldName;
    }
}
