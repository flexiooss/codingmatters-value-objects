package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;

import java.io.IOException;

/**
 * Created by nelt on 9/1/16.
 */
@Mojo(name = "generate")
public class GenerateMojo extends AbstractGenerationMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("generating value object with configuration:");
        this.getLog().info("\t- to package          :" + this.getDestinationPackage());
        this.getLog().info("\t- specification file  :" + this.getInputSpecification().getAbsolutePath());
        this.getLog().info("\t- to output directory : " + this.getOutputDirectory().getAbsolutePath());

        try {
            new GenerateDeleguate(this.getDestinationPackage(), this.getInputSpecification(), this.getOutputDirectory()).run();
        } catch (SpecSyntaxException | LowLevelSyntaxException e) {
            throw new MojoFailureException("unparseable specification file : " + this.getInputSpecification().getAbsolutePath(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("something went wrong whie generating value objects at " + this.getDestinationPackage(), e);
        }
    }
}
