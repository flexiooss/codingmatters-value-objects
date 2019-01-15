package org.codingmatters.value.objects.js.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsClassWriter {

    public static final String INDENTATION_UNITY = "    ";
    private final BufferedWriter writer;
    private int indent;

    public JsClassWriter( String filePath ) throws IOException {
        File file = new File( filePath );
        file.getParentFile().mkdirs();
        if( !file.exists() ) {
            file.createNewFile();
        }
        this.writer = new BufferedWriter( new FileWriter( file ) );
        this.indent = 0;
    }

    public void line( String line ) throws IOException {
        writer.write( line );
        writer.newLine();
        if( line.endsWith( "{" ) ) {
            indent++;
        } else if( line.equals( "}" ) ) {
            indent--;
        }
    }

    public void string( String line ) throws IOException {
        writer.write( line );
    }

    private void indent() throws IOException {
        for( int i = 0; i < indent; i++ ) {
            writer.write( INDENTATION_UNITY );
        }
    }

    public void newLine() throws IOException {
        writer.newLine();
    }

}
