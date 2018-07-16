package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.spec.Spec;

import java.io.File;
import java.io.IOException;

public class MainClass {

    static private Spec loadSpec( String resource ) {
        try {
            return new SpecReaderPhp().read( Thread.currentThread().getContextClassLoader().getResourceAsStream( resource ) );
        } catch( Exception e ) {
            throw new RuntimeException( "error loading spec", e );
        }
    }

    public static void main( String[] args ) throws IOException {

        String rootPath = System.getProperty( "generationTargetDir", "" );
        if( rootPath.equals( "" ) ) {
            rootPath = "/home/nico/workspace/codingmatters-value-objects/cdm-value-objects-php/target/test-classes";
//            System.out.println( "Generation target dir property not found" );
//            System.exit( 1 );
        }
        System.out.println( "Generating in " + rootPath );

//        Spec spec = loadSpec( "books.yaml" );
//        new SpecCodeGenerator( spec, "org.generated", new File( rootPath ) ).generate();


        Spec spec = loadSpec( "books.yaml" );
        new SpecPhpGenerator( spec, "org.generated", new File( rootPath ) ).generate();

    }

}
