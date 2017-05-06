package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Created by nelt on 4/13/17.
 */
public abstract class AbstractGenerationMojo extends AbstractMojo {

    @Parameter(required = true, alias = "destination-package")
    private String destinationPackage;

    @Parameter(required = true, alias = "input-spec")
    private File inputSpecification;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/", alias="output-dir")
    private File outputDirectory;

    public File getInputSpecification() {
        return inputSpecification;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getDestinationPackage() {
        return this.destinationPackage;
    }
}
