package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.value.objects.PumlClassFromSpecGenerator;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.reader.SpecReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Mojo(name = "puml")
public class PumlMojo extends AbstractGenerationMojo {

    @Parameter(defaultValue = "${basedir}/target/generated-resources/", alias="output-resource-dir")
    private File resourceDirectory;

    public File getResourceDirectory() {
        return resourceDirectory;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("generating puml documentation for value objects with configuration:");
        this.getLog().info("\t- destination package :" + this.getDestinationPackage());
        this.getLog().info("\t- specification file  :" + this.getInputSpecification().getAbsolutePath());
        this.getLog().info("\t- to output resource directory : " + this.getResourceDirectory().getAbsolutePath());

        try {
            SpecReader reader = new SpecReader();

            try(InputStream in = new FileInputStream(this.getInputSpecification())) {
                new PumlClassFromSpecGenerator(reader.read(in), this.getDestinationPackage(), this.getResourceDirectory()).generate();
            }
        } catch (SpecSyntaxException | LowLevelSyntaxException e) {
            throw new MojoFailureException("unparseable specification file : " + this.getInputSpecification().getAbsolutePath(), e);
        } catch (IOException e) {
            throw new MojoExecutionException("something went wrong while generating json harness for value objects at " + this.getDestinationPackage(), e);
        }
    }
}
