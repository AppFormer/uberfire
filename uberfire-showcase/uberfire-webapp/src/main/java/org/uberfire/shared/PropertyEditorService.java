package org.uberfire.shared;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.shared.property_editor.PropertyEditorEvent;

@Remote
public interface PropertyEditorService {

    String getInformation( String FQCN );

}
