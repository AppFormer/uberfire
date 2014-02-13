package org.uberfire.security.server.cdi;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Qualifier
@Retention(RUNTIME)
@Target({ FIELD, TYPE, METHOD })
public @interface AppResourcesAuthz {

}
