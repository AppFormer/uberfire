package org.uberfire.shared.propertyEditor;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.client.property.editor.api.PropertyEditorCategory;

@Remote
public interface BeanPropertyEditorBuilderService {

    PropertyEditorCategory extract( String fqcn );

    PropertyEditorCategory extract( String fqcn,
                                    Object instance );
}
