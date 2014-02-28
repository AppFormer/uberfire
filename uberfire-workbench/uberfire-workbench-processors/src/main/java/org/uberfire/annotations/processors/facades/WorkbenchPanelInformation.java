package org.uberfire.annotations.processors.facades;

import java.util.List;

public class WorkbenchPanelInformation {

    private String fieldName;
    private List<String> wbParts;
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

    @SuppressWarnings( "unused" )
    public List<String> getWbParts() {
        return wbParts;
    }

    public void setWbParts( List<String> uFParts ) {
        this.wbParts = uFParts;
    }

    public void setPanelType( String panelType ) {
        this.panelType = panelType;
    }

    public String getPanelType() {
        return panelType;
    }

    @SuppressWarnings( "unused" )
    public String getFieldName() {
        return fieldName;
    }
}
