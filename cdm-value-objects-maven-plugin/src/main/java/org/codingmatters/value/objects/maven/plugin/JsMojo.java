package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;

@Mojo(name = "js")
public class JsMojo  extends AbstractGenerationMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("generating JS value object with configuration:");
        this.getLog().info("\t- destination package :" + this.getDestinationPackage());
        this.getLog().info("\t- specification file  :" + this.getInputSpecification().getAbsolutePath());
        this.getLog().info("\t- to output directory : " + this.getOutputDirectory().getAbsolutePath());

        try{
            org.codingmatters.value.objects.js.Main.main(new String[]{
                    this.getInputSpecification().getAbsolutePath(),
                    this.getOutputDirectory().getAbsolutePath(),
                    this.getDestinationPackage()
            });
        } catch( ProcessingException | GenerationException e ) {
            throw new MojoExecutionException( "Error generating js value objects", e );
        }
    }
}
