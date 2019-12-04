package org.codingmatters.value.objects.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codingmatters.value.objects.js.generator.packages.JsonPackageGenerator;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.codingmatters.value.objects.js.generator.visitor.JsValueObjectGenerator;
import org.codingmatters.value.objects.js.parser.YamlSpecParser;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mojo(name = "js")
public class JsMojo extends AbstractGenerationMojo {

    @Parameter(required = true)
    private String vendor;

    @Parameter(defaultValue = "${project.artifactId}")
    private String artifactId;

    @Parameter(defaultValue = "${project.version}")
    private String version;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info( "generating JS value object with configuration:" );
        this.getLog().info( "\t- destination package :" + this.getDestinationPackage() );
        this.getLog().info( "\t- specification file  :" + this.getInputSpecification().getAbsolutePath() );
        this.getLog().info( "\t- to output directory : " + this.getOutputDirectory().getAbsolutePath() );

        try{
            File specFile = new File( this.getInputSpecification().getAbsolutePath() );
            PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
            YamlSpecParser parser = new YamlSpecParser( getDestinationPackage() );
            if( specFile.exists() ){
                File[] files = specFile.listFiles();
                if( specFile.isDirectory() ){
                    if( files != null ){
                        List<File> yamlFiles = Arrays.stream( files ).filter( file -> file.getName().endsWith( ".yaml" ) || file.getName().endsWith( ".yml" ) ).collect( Collectors.toList() );
                        for( File file : yamlFiles ){
                            ParsedYAMLSpec parsed = parser.parse( new FileInputStream( file ) );
                            new JsValueObjectGenerator( getOutputDirectory(), getDestinationPackage(), packageBuilder )
                                    .process( parsed );
                        }
                    } else {
                        throw new IOException( "Spec file not found" );
                    }
                } else {
                    ParsedYAMLSpec parsed = parser.parse( new FileInputStream( specFile ) );
                    new JsValueObjectGenerator( getOutputDirectory(), getDestinationPackage(), packageBuilder )
                            .process( parsed );
                }
                PackageFilesGenerator packageFilesGenerator = new PackageFilesGenerator( packageBuilder, getOutputDirectory().getAbsolutePath() );
                packageFilesGenerator.generateFiles();
                new JsonPackageGenerator( getOutputDirectory() ).generatePackageJson( vendor, artifactId, version, getDestinationPackage().split( "\\." )[0] );
            } else {
                throw new IOException( "Spec file not found" );
            }
        } catch( Exception e ) {
            throw new MojoExecutionException( "Error generating js value objects", e );
        }
    }
}
