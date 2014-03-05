package org.uberfire.shared;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.shared.property_editor.PropertyEditorEvent;

@Remote
public interface PropertyEditorService {

    Map<String,String> getInformation( String FQCN );

}
