package org.uberfire.annotations.processors;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.annotations.processors.facades.UFPanelInformation;

public class TemplateInformation {

    private UFPanelInformation defaultPanel;
    private List<UFPanelInformation> templateFields = new ArrayList<UFPanelInformation>();

    public void addTemplateField( UFPanelInformation field ) {
        templateFields.add( field );
    }

    public List<UFPanelInformation> getTemplateFields() {
        return templateFields;
    }

    public void setDefaultPanel( UFPanelInformation defaultPanel ) {
        this.defaultPanel = defaultPanel;
    }

    public UFPanelInformation getDefaultPanel() {
        return defaultPanel;
    }

    public boolean thereIsTemplateFields() {
        return  (getTemplateFields().isEmpty()&&defaultPanel==null);
    }
}
