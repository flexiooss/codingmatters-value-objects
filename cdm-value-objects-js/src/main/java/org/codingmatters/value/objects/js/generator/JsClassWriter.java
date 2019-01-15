package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.js.error.ProcessingException;
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

    void indent( ) throws IOException {
        for( int i = 0; i < indent; i++ ) {
            writer.write( INDENTATION_UNITY );
        }
    }

    public void newLine( ) throws IOException {
        writer.newLine();
    }

    public void flush( ) throws IOException {
        writer.flush();
    }

    public void generateConstructor( List<ValueObjectProperty> properties ) throws IOException {
        newLine();
        List<String> names = properties.stream().map( prop -> prop.name() ).collect( Collectors.toList() );
        line( "constructor ( " + String.join( ", ", names ) + " ){" );
        line( String.join( "\n        ", properties.stream().map( prop -> "this." + prop.name() + " = " + prop.name() + ";" ).collect( Collectors.toList() ) ) );
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
            /*
            } else {
                TypeKind typeKind;

                String assign = "jsonObject[\"" + property.realName() + "\"] = ";
                switch( typeKind ) {
                    case JAVA_TYPE:
                        assign += "this." + property.name() + ";";
                        break;
                    case IN_SPEC_VALUE_OBJECT:
                        assign += "this." + property.name() + ".map( x=>x.toObject() ); // in spec";
                        break;
                    case EXTERNAL_VALUE_OBJECT:
                        String typeRef = property.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef();

                        if( ( "date".equals( typeRef ) ) ||
                                ( "time".equals( typeRef ) ) ||
                                ( "date-time".equals( typeRef ) ) ||
                                ( "tz-date-time".equals( typeRef ) ) ) {
                            assign += "this." + property.name() + ";";
                        } else {
                            assign += "this." + property.name() + ".map( extObj => extObj.toObject() ); // Ext";
                        }
                        break;
                    case EMBEDDED:
                        assign += "this." + property.name() + ".map( obj => obj.toObject() ); // embed";
                        break;
                    case ENUM:
                        assign += "this." + property.name() + ".map( enumeration => enumeration.name );";
                        break;
                }
                write( assign );
                newLine();
            }
            */
        }
        line( "return jsonObject;" );
        line( "}" );
    }

    public void generateToJsonMethod( ) throws IOException {
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
                String.join( ",", properties.stream().map( prop -> "this." + prop.name() ).collect( Collectors.toList() ) ) +
                ")" );
        line( "}" );
    }
}
