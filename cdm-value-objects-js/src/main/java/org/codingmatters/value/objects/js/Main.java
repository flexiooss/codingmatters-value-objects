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
        if( args.length != 3 || args[2].isEmpty() ){
            System.out.println( "Args: <yaml spec file path> <target directory> <root package>" );
            System.out.println( "    <yaml spec file path>: Yaml file path OR directory. If dir, then all yaml files in this dir will be generated in the same package" );
            System.out.println( "    <target directory>: generation target directory" );
            System.out.println( "    <root package>: The root package of sources" );
            System.exit( 1 );
        }
        String specFilePath = args[0];
        String targetDir = args[1];
        String rootPackage = args[2];
        System.out.println( "Processing generation of " + specFilePath + " in " + targetDir + " with root package " + rootPackage );

        File specFile = new File( specFilePath );
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        if( specFile.exists() ){
            if( specFile.isDirectory() ){
                for( File file : specFile.listFiles() ){
                    if( file.getName().endsWith( ".yaml" ) ){
                        generateSpec( targetDir, rootPackage, file, packageBuilder );
                    }
                }
            } else {
                generateSpec( targetDir, rootPackage, specFile, packageBuilder );
            }
            PackageFilesGenerator packageFilesGenerator = new PackageFilesGenerator( packageBuilder, targetDir );
            packageFilesGenerator.generateFiles();
        }
    }

    private static void generateSpec( String targetDir, String rootPackage, File specFile, PackageFilesBuilder packageBuilder ) throws ProcessingException {
        File targetDirectory = new File( targetDir );
        ParsedYAMLSpec spec = loadSpec( specFile.getPath() );
        new SpecJsGenerator( spec, rootPackage, targetDirectory ).generate( packageBuilder );
    }

    static private ParsedYAMLSpec loadSpec( String resource ) {
        try {
            return new YamlSpecParser().parse( new FileInputStream( resource ) );
        } catch( Exception e ){
            throw new RuntimeException( "error loading spec", e );
        }
    }

}
