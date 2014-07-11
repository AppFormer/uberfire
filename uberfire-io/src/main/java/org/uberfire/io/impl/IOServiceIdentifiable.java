package org.uberfire.io.impl;

import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Option;

public interface IOServiceIdentifiable extends IOService {

    String getId();

}
