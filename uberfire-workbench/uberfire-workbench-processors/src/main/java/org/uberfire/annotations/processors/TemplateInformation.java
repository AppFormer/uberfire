package org.uberfire.annotations.processors;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.annotations.processors.facades.WorkbenchPanelInformation;

public class TemplateInformation {

    private WorkbenchPanelInformation defaultPanel;
    private List<WorkbenchPanelInformation> templateFields = new ArrayList<WorkbenchPanelInformation>();

    public void addTemplateField( WorkbenchPanelInformation field ) {
        templateFields.add( field );
    }

    public List<WorkbenchPanelInformation> getTemplateFields() {
        return templateFields;
    }

    public void setDefaultPanel( WorkbenchPanelInformation defaultPanel ) {
        this.defaultPanel = defaultPanel;
    }

    public WorkbenchPanelInformation getDefaultPanel() {
        return defaultPanel;
    }

    public boolean thereIsTemplateFields() {
        return  (getTemplateFields().isEmpty()&&defaultPanel==null);
    }
}
