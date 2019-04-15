package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesGenerator;
import org.codingmatters.value.objects.js.parser.YamlSpecParser;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Locale;

public class Main {

    public static void main( String[] args ) throws ProcessingException, GenerationException {
        if( args.length < 3 || args[2].isEmpty() ){
            System.out.println( "Args: <yaml spec file path> <target directory> <root package> [--no-sub-package]" );
            System.out.println( "    <yaml spec file path>: Yaml file path OR directory. If dir, then all yaml files in this dir will be generated in the same package" );
            System.out.println( "    <target directory>: generation target directory" );
            System.out.println( "    <root package>: The root package of sources" );
            System.out.println( "    [file extension]: Optional yaml file extension to use ( default: *.yaml *.yml )" );
            System.out.println( "    --no-sub-package Optional argument to prevent multiple yaml file to be generated in different subpackage" );
            System.exit( 1 );
        }
        String specFilePath = args[0];
        String targetDir = args[1];
        String rootPackage = args[2];

        boolean deactivateSubPackage = false;
        String[] extension = { ".yaml", ".yml" };
        if( args.length == 4 ){
            if( "--no-sub-package".equals( args[3] ) ){
                deactivateSubPackage = true;
            } else {
                extension = args[3].split( "," );
            }
        }
        if( args.length == 5 ){
            extension = args[3].split( "," );
            if( "--no-sub-package".equals( args[4] ) ){
                deactivateSubPackage = true;
            }
        }

        System.out.println( "Processing generation of " + specFilePath + " in " + targetDir + " with root package " + rootPackage );
        System.out.println( "Included extensions: " + String.join( " ", extension ) );
        File specFile = new File( specFilePath );
        PackageFilesBuilder packageBuilder = new PackageFilesBuilder();
        if( specFile.exists() )

        {
            if( specFile.isDirectory() ){
                for( File file : specFile.listFiles() ){
                    if( Arrays.stream( extension ).anyMatch( ext->file.getName().endsWith( ext ) ) ){
                        String targetRootPackage = rootPackage;
                        if( !deactivateSubPackage ){
                            targetRootPackage += "." + file.getName().substring( 0, file.getName().lastIndexOf( "." ) ).toLowerCase( Locale.ENGLISH );
                        }
                        generateSpec( targetDir, targetRootPackage, file, packageBuilder );
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
        ParsedYAMLSpec spec = loadSpec( specFile.getPath(), rootPackage );
        new SpecJsGenerator( spec, rootPackage, targetDirectory ).generate( packageBuilder );
    }

    static private ParsedYAMLSpec loadSpec( String resource, String typesPackage ) {
        try {
            YamlSpecParser yamlSpecParser = new YamlSpecParser( typesPackage );
            return yamlSpecParser.parse( new FileInputStream( resource ) );
        } catch( Exception e ){
            throw new RuntimeException( "error loading spec", e );
        }
    }

}
