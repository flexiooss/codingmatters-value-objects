package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;

import java.io.File;
import java.io.IOException;

/**
 * Created by nelt on 9/1/16.
 */
@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    @Parameter(required = true)
    private String destinationPackage;

    @Parameter(required = true)
    private File inputSpecification;

    @Parameter(defaultValue = "${basedir}/target/generated-sources/")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("generating value object with configuration:");
        this.getLog().info("\t- to package          :" + this.destinationPackage);
        this.getLog().info("\t- specification file  :" + this.inputSpecification.getAbsolutePath());
        this.getLog().info("\t- to output directory : " + this.outputDirectory.getAbsolutePath());

        try {
            new GenerateDeleguate(this.destinationPackage, this.inputSpecification, this.outputDirectory).run();
        } catch (SpecSyntaxException | LowLevelSyntaxException e) {
            throw new MojoFailureException("unparseable specification file : " + this.inputSpecification.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("something went wrong whie generating value objects at " + this.destinationPackage, e);
        }
    }

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
