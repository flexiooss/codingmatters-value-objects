package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.php.generator.SpecPhpGenerator;
import org.codingmatters.value.objects.php.generator.SpecReaderPhp;
import org.codingmatters.value.objects.spec.Spec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainClass {

    static private Spec loadSpec( String resource ) {
        try {
            return new SpecReaderPhp().read( new FileInputStream( resource ) );
        } catch( Exception e ) {
            throw new RuntimeException( "error loading spec", e );
        }
    }

    public static void main( String[] args ) throws IOException {
        String rootPath = System.getProperty( "generationTargetDir", "" );
        if( rootPath.equals( "" ) ) {
            System.out.println( "Generation target dir property not found" );
            System.exit( 1 );
        }
        System.out.println( "Generating in " + rootPath );

        for( File file : new File( rootPath ).listFiles() ) {
            if( file.getName().endsWith( "yaml" ) ) {
                Spec spec = loadSpec( rootPath + "/" + file.getName() );
                new SpecPhpGenerator( spec, "org.generated", new File( rootPath ) ).generate();
            }
        }
    }

}
