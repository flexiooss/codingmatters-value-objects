package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;

import java.io.IOException;

/**
 * Created by nelt on 4/13/17.
 */
@Mojo(name = "json")
public class JsonMojo  extends AbstractGenerationMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("generating json harness for value objects with configuration:");
        this.getLog().info("\t- destination package :" + this.getDestinationPackage());
        this.getLog().info("\t- specification file  :" + this.getInputSpecification().getAbsolutePath());
        this.getLog().info("\t- to output directory : " + this.getOutputDirectory().getAbsolutePath());

        try {
            new GenerateJsonDeleguate(this.getDestinationPackage(), this.getInputSpecification(), this.getOutputDirectory()).run();
        } catch (SpecSyntaxException | LowLevelSyntaxException e) {
            throw new MojoFailureException("unparseable specification file : " + this.getInputSpecification().getAbsolutePath(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("something went wrong while generating json harness for value objects at " + this.getDestinationPackage(), e);
        }
    }
}
