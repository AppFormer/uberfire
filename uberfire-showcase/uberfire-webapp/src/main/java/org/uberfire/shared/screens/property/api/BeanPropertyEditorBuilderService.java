package org.uberfire.shared.screens.property.api;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface BeanPropertyEditorBuilderService {

    public Map<String,List<String>> extract( String fqcn );
}
