package org.uberfire.shared.propertyEditor;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.client.propertyEditor.PropertyEditorException;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;

@Remote
public interface BeanPropertyEditorBuilderService {

    Map<String, List<String>> extract( String fqcn ) ;

    PropertyEditorCategory extractCategories( String fqcn ) ;
}
