package org.codingmatters.value.objects.js.generator.valueObject;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesDeserializationProcessor;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesSerializationProcessor;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        if( line.endsWith( "{" ) ) {
            writeLine( line );
            indent++;
        } else if( line.equals( "}" ) ) {
            indent--;
            writeLine( line );
        } else {
            writeLine( line );
        }
    }

    public void writeLine( String line ) throws IOException {
        indent();
        writer.write( line );
        writer.newLine();
    }

    public void string( String line ) throws IOException {
        writer.write( line );
    }

    public void indent() throws IOException {
        for( int i = 0; i < indent; i++ ) {
            writer.write( INDENTATION_UNITY );
        }
    }

    public void newLine() throws IOException {
        writer.newLine();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    public void generateConstructor( List<ValueObjectProperty> properties ) throws IOException {
        newLine();
        List<String> names = properties.stream().map( prop->prop.name() ).collect( Collectors.toList() );
        line( "constructor ( " + String.join( ", ", names ) + " ){" );
        line( String.join( "\n        ", properties.stream().map( prop->"this." + prop.name() + " = " + prop.name() + ";" ).collect( Collectors.toList() ) ) );
        line( "deepFreezeSeal( this );" );
        line( "}" );
        flush();
    }

    public void generateGetters( List<ValueObjectProperty> properties ) throws IOException {
        for( ValueObjectProperty property : properties ) {
            line( property.name() + "() {" );
            line( "return this." + property.name() + ";" );
            line( "}" );
        }
    }

    public void generateToObjectMethod( List<ValueObjectProperty> properties ) throws IOException, ProcessingException {
        line( "toObject() {" );
        line( "var jsonObject = {};" );
        PropertiesSerializationProcessor propertiesSerializationProcessor = new PropertiesSerializationProcessor( this );
        for( ValueObjectProperty property : properties ) {
            propertiesSerializationProcessor.process( property );
        }
        line( "return jsonObject;" );
        line( "}" );
    }

    public void generateToJsonMethod() throws IOException {
        line( "toJSON() {" );
        line( "return this.toObject();" );
        line( "}" );
    }

    public void generateSetters( List<ValueObjectProperty> properties ) throws IOException {
        for( ValueObjectProperty property : properties ) {
            line( property.name() + "( " + property.name() + ") {" );
            line( "this." + property.name() + " = " + property.name() + ";" );
            line( "return this;" );
            line( "}" );
        }
    }

    public void generateBuildMethod( String objectName, List<ValueObjectProperty> properties ) throws IOException {
        line( "build(){" );
        line( "return new " + objectName + "(" +
                String.join( ",", properties.stream().map( prop->"this." + prop.name() ).collect( Collectors.toList() ) ) +
                ")" );
        line( "}" );
    }

    public void generateFromObjectMethod( String builderName, List<ValueObjectProperty> properties ) throws IOException, ProcessingException {
        line( "static fromObject( jsonObject ) {" );
        line( "var builder = new " + builderName + "()" );
        PropertiesDeserializationProcessor propertiesDeserializationProcessor = new PropertiesDeserializationProcessor(this);
        for( ValueObjectProperty property : properties ) {
            line( "if( jsonObject[\"" + property.name() + "\"] != undefined ){" );
            propertiesDeserializationProcessor.process( property );
            line( "}" );
        }
        line( "return builder.build();" );
        line( "}" );
    }

}
