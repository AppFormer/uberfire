package org.uberfire.backend.server.impl;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.shared.PropertyEditorService;

@Service
@ApplicationScoped
public class PropertyEditorServiceImpl implements PropertyEditorService {

    @Override
    public String getInformation( String FQCN ) {
        return FQCN;
    }

}
