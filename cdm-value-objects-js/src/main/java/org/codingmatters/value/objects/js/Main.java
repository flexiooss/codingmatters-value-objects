package org.codingmatters.value.objects.js;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.YamlSpecParser;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;

import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main( String[] args ) throws ProcessingException {
        String rootPath = System.getProperty( "generationTargetDir", "" );
        if( rootPath.equals( "" ) ) {
            System.out.println( "Generation target dir property not found" );
            System.exit( 1 );
        }
        System.out.println( "Generating in " + rootPath );
        for( File file : new File( rootPath ).listFiles() ) {
            if( file.getName().endsWith( "yaml" ) ) {
                ParsedYAMLSpec spec = loadSpec( rootPath + "/" + file.getName() );
                new SpecJsGenerator( spec, "org.generated", new File( rootPath ) ).generate();
            }
        }
    }

    static private ParsedYAMLSpec loadSpec( String resource ) {
        try {
            return new YamlSpecParser().parse( new FileInputStream( resource ) );
        } catch( Exception e ) {
            throw new RuntimeException( "error loading spec", e );
        }
    }

}
