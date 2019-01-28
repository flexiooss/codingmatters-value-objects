package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.codingmatters.value.objects.js.parser.YamlSpecParser;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;

import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main( String[] args ) throws ProcessingException, GenerationException {
        String rootPath = System.getProperty( "generationTargetDir", "/home/nico/workspace/codingmatters-value-objects/cdm-value-objects-js/target/js-test" );
        if( rootPath.equals( "" ) ) {
            System.out.println( "Generation target dir property not found" );
            System.exit( 1 );
        }
        System.out.println( "Generating in " + rootPath );
        File targetDirectory = new File( rootPath );
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        for( File file : targetDirectory.listFiles() ) {
            if( file.getName().endsWith( "yaml" ) ) {
                ParsedYAMLSpec spec = loadSpec( rootPath + "/" + file.getName() );
                new SpecJsGenerator( spec, "org.generated", targetDirectory ).generate(packageBuilder);
            }
        }
        PackageFilesGenerator packageFilesGenerator = new PackageFilesGenerator( packageBuilder, rootPath );
        packageFilesGenerator.generateFiles();
    }

    static private ParsedYAMLSpec loadSpec( String resource ) {
        try {
            return new YamlSpecParser().parse( new FileInputStream( resource ) );
        } catch( Exception e ) {
            throw new RuntimeException( "error loading spec", e );
        }
    }

}
