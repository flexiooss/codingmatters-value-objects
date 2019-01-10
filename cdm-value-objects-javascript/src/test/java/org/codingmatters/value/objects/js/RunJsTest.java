package org.codingmatters.value.objects.js;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RunJsTest {

    private static ProcessBuilder processBuilder;

    @BeforeClass
    public static void setUp() throws Exception {
        String dir = System.getProperty( "project.build.directory" ) + "/js-test";
        processBuilder = new ProcessBuilder();
        processBuilder.directory( new File( dir ) );
        processBuilder.command( "yarn", "install" );
        /*
        // BIND LOCAL SOURCE
        Process process = processBuilder.start();
        process.waitFor( 60, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );
        new ProcessBuilder().directory( new File( dir + "/js-test" ) );
        processBuilder.command( "yarn", "link", "flexio-jshelpers" );
        process = processBuilder.start();
        process.waitFor( 60, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );
        */
    }

    private static void printError( Process process ) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try( InputStream stream = process.getInputStream() ) {
            while( stream.read( buffer ) != -1 ) {
                out.write( buffer );
            }
            System.out.println( "Out = " + new String( out.toByteArray() ) );
        }
        try( InputStream stream = process.getErrorStream() ) {
            while( stream.read( buffer ) != -1 ) {
                out.write( buffer );
            }
            System.out.println( "Error = " + new String( out.toByteArray() ) );
        }
    }

    @Test
    public void whenName_then() throws Exception {
        processBuilder.command( "yarn", "test" );
        Process process = processBuilder.start();
        process.waitFor( 60, TimeUnit.SECONDS );
        if( process.exitValue() != 0 ) {
            printError( process );
        }
        assertThat( process.exitValue(), is( 0 ) );
        System.out.println( "EXIT == " + process.exitValue() );
    }
}
