package org.uberfire.properties.editor.temp.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.properties.editor.temp.model.PropertyEditorCategory;

@Remote
public interface BeanPropertyEditorBuilderService {

    PropertyEditorCategory extract( String fqcn );

    PropertyEditorCategory extract( String fqcn,
                                    Object instance );
}
