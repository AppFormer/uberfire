package org.uberfire.backend.server.impl;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.propertyEditor.BeanInformation;
import org.uberfire.shared.PropertyEditorService;

@Service
@ApplicationScoped
public class PropertyEditorServiceImpl implements PropertyEditorService {

    @Override
    public Map<String,String> getInformation( String FQCN ) {
        BeanInformation actual = new BeanInformation( FQCN ).extract();
        return actual.toMap();
    }

}
