package org.codingmatters.value.objects.js.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JsEnumWriter {

    private final File targetDirectory;
    private final JsEnum enumValue;
    public final BufferedWriter writer;
    private final String fileName;
    private final String objectName;

    public JsEnumWriter( File targetDirectory, JsEnum enumValue ) throws IOException {
        if( (!targetDirectory.exists() && !targetDirectory.mkdirs()) || !targetDirectory.isDirectory() ) {
            throw new IOException( "Target directory not exist or is not a directory" );
        }
        this.targetDirectory = targetDirectory;
        this.enumValue = enumValue;
        this.objectName = Naming.firstLetterUpperCase( enumValue.name() );
        this.fileName = objectName + ".js";

        String targetFile = String.join( "/", this.targetDirectory.getPath(), fileName );
        this.writer = new BufferedWriter( new FileWriter( targetFile ) );
    }

    public void writeEnum() throws IOException {
        writer.write( "import { FlexEnum } from 'flexio-jshelpers'" );
        writer.newLine();
        writer.newLine();
        writer.write( "class " + this.objectName + " extends FlexEnum{}" );
        writer.newLine();
        writer.newLine();
        writer.write( this.objectName );
        writer.write( ".initEnum([" );
        writer.write( String.join( ",", Arrays.stream( this.enumValue.enumValues() ).map( val->"'" + val + "'" ).collect( Collectors.toList() ) ) );
        writer.write( "]);" );
        writer.newLine();
        writer.write( "export {" + this.objectName + "}" );
        writer.flush();
        writer.close();
    }

}
