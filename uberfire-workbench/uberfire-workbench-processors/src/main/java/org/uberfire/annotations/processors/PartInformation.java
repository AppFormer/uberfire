package org.uberfire.annotations.processors;

import java.util.Map;

public class PartInformation {

    private final String partName;
    private Map<String, String> parameters;

    public PartInformation( String partName) {
        this.partName = partName;
        this.parameters = parameters;
    }

    public PartInformation( String partName,
                            Map<String, String> parameters ) {
        this.partName = partName;
        if ( parameters==null||parameters.isEmpty() ) {
            this.parameters = null;
        } else {

            this.parameters = parameters;
        }
    }

    @SuppressWarnings("unused")
    public String getPartName() {
        return partName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
