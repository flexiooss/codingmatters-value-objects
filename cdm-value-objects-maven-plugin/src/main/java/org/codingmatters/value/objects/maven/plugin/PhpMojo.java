package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.php.generator.SpecPhpGenerator;
import org.codingmatters.value.objects.php.generator.SpecReaderPhp;
import org.codingmatters.value.objects.spec.Spec;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Mojo(name = "generate-php")
public class PhpMojo extends AbstractGenerationMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("generating PHP value objects with configuration:");
        this.getLog().info("\t- destination package :" + this.getDestinationPackage());
        this.getLog().info("\t- specification file  :" + this.getInputSpecification().getAbsolutePath());
        this.getLog().info("\t- to output directory : " + this.getOutputDirectory().getAbsolutePath());
        try(InputStream in = new FileInputStream(this.getInputSpecification())) {
            Spec spec = new SpecReaderPhp().read( in );
            new SpecPhpGenerator( spec, this.getDestinationPackage(), this.getOutputDirectory() ).generate();
        } catch( IOException e ) {
            throw new MojoFailureException("Something went wrong while reading spec: " + this.getInputSpecification().getAbsolutePath(), e);
        } catch( LowLevelSyntaxException e ) {
            throw new MojoFailureException("Unparsable specification file : " + this.getInputSpecification().getAbsolutePath(), e);
        }

    }

}
