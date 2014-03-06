package org.uberfire.shared;

import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface PropertyEditorService {

    Map<String,String> getInformation( String FQCN );

}
