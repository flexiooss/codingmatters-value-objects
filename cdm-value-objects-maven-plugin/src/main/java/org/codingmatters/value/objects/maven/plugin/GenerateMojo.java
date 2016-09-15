package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Created by nelt on 9/1/16.
 */
@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    @Parameter(required = true)
    private File inputSpecification;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.printf("Will eventually generate:\n\t- spec: %s\n\t- output directory: %s\n", this.inputSpecification, this.outputDirectory);
    }

    public File getInputSpecification() {
        return inputSpecification;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }
}
