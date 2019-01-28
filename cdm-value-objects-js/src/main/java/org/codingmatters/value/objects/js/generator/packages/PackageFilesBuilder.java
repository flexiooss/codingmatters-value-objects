package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;

import java.util.HashMap;
import java.util.Map;

public class PackageFilesBuilder {

    private final Map<String, PackageConfiguration> packagesConfiguration;

    public PackageFilesBuilder() {
        packagesConfiguration = new HashMap<>();
    }

    public void addClass( String currentPackage, String objectName ) throws GenerationException {
        String rootPackage = getRootPackage( currentPackage );
        packagesConfiguration.putIfAbsent( rootPackage, new PackageConfiguration( currentPackage ) );
        packagesConfiguration.get( rootPackage ).addClass( currentPackage, objectName );
    }

    private String getRootPackage( String currentPackage ) {
        return currentPackage.split( "\\." )[0];
    }

    public void addList( String currentPackage, String objectName ) throws GenerationException {
        String rootPackage = getRootPackage( currentPackage );
        packagesConfiguration.putIfAbsent( rootPackage, new PackageConfiguration( currentPackage ) );
        packagesConfiguration.get( rootPackage ).addList( currentPackage, objectName );
    }

    public Map<String, PackageConfiguration> packageConfig() {
        return this.packagesConfiguration;
    }
}
