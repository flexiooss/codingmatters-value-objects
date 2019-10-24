package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.generator.GenerationException;

import java.util.HashMap;
import java.util.Map;

public class PackageFilesBuilder {

    private final Map<String, PackageConfiguration> packagesConfiguration;

    public PackageFilesBuilder() {
        packagesConfiguration = new HashMap<>();
    }

    public void addClass( String currentPackage, String objectName, boolean generateList ) throws GenerationException {
        String rootPackage = getRootPackage( currentPackage );
        packagesConfiguration.putIfAbsent( rootPackage, new PackageConfiguration( currentPackage ) );
        packagesConfiguration.get( rootPackage ).addClass( currentPackage, objectName, generateList );
    }

    private String getRootPackage( String currentPackage ) {
        return currentPackage.split( "\\." )[0];
    }

    public void addEnum( String currentPackage, String objectName, boolean generateList ) throws GenerationException {
        String rootPackage = getRootPackage( currentPackage );
        packagesConfiguration.putIfAbsent( rootPackage, new PackageConfiguration( currentPackage ) );
        packagesConfiguration.get( rootPackage ).addEnum( currentPackage, objectName, generateList );
    }

    public Map<String, PackageConfiguration> packageConfig() {
        return this.packagesConfiguration;
    }
}
