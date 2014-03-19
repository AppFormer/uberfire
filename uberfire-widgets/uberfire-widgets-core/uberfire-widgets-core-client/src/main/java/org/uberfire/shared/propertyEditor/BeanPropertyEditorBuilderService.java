package org.uberfire.shared.propertyEditor;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.client.propertyEditor.api.PropertyEditorCategory;
import org.uberfire.org.uberfire.server.impl.BeanPropertyEditorBuilder;

@Remote
public interface BeanPropertyEditorBuilderService {

    PropertyEditorCategory extract( String fqcn );

    PropertyEditorCategory extract( String fqcn,
                                    Object instance );
}
