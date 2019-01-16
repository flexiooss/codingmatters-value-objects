package org.codingmatters.value.objects.js.generator.valueObject;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesDeserializationProcessor;
import org.codingmatters.value.objects.js.generator.visitor.PropertiesSerializationProcessor;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.codingmatters.value.objects.js.generator.NamingUtility.attributeName;
import static org.codingmatters.value.objects.js.generator.NamingUtility.propertyName;

public class JsClassGenerator extends JsFileWriter {

    public JsClassGenerator( String filePath ) throws IOException {
        super( filePath );
    }

    public void valueObjectClass( ParsedValueObject valueObject, String objectName, JsClassGenerator write ) throws IOException, ProcessingException {
        write.line( "class " + objectName + " {" );
        write.constructor( valueObject.properties() );
        write.getters( valueObject.properties() );
        write.toObjectMethod( valueObject.properties() );
        write.toJsonMethod();
        write.line( "}" );
        write.line( "export { " + objectName + "}" );
    }

    public void constructor( List<ValueObjectProperty> properties ) throws IOException {
        newLine();

        List<String> names = properties.stream().map( prop->propertyName( prop.name() ) ).collect( Collectors.toList() );
        line( "constructor ( " + String.join( ", ", names ) + " ){" );

        line( String.join( "\n        ", properties.stream().map( prop->
                "this." + attributeName( prop.name() ) + " = " + propertyName( prop.name() ) + ";"
        ).collect( Collectors.toList() ) ) );

        line( "deepFreezeSeal( this );" );
        line( "}" );
        flush();
    }

    public void getters( List<ValueObjectProperty> properties ) throws IOException {
        for( ValueObjectProperty property : properties ) {
            line( propertyName( property.name() ) + "() {" );
            line( "return this." + attributeName( property.name() ) + ";" );
            line( "}" );
        }
    }

    public void toObjectMethod( List<ValueObjectProperty> properties ) throws IOException, ProcessingException {
        line( "toObject() {" );
        line( "var jsonObject = {};" );
        PropertiesSerializationProcessor propertiesSerializationProcessor = new PropertiesSerializationProcessor( this );
        for( ValueObjectProperty property : properties ) {
            propertiesSerializationProcessor.process( property );
        }
        line( "return jsonObject;" );
        line( "}" );
    }

    public void toJsonMethod() throws IOException {
        line( "toJSON() {" );
        line( "return this.toObject();" );
        line( "}" );
    }

    public void builderClass( ParsedValueObject valueObject, String objectName, JsClassGenerator write ) throws IOException, ProcessingException {
        String builderName = NamingUtility.builderName( objectName );
        write.line( "class " + builderName + " {" );
        write.line( "constructor(){" );
        write.line( String.join( "\n        ", valueObject.properties().stream().map( prop->"this." + NamingUtility.attributeName( prop.name() ) + " = null;" ).collect( Collectors.toList() ) ) );
        write.line( "}" );
        write.setters( valueObject.properties() );
        write.buildMethod( objectName, valueObject.properties() );
        write.fromObjectMethod( builderName, valueObject.properties() );
        write.fromJsonMethod();
        write.line( "}" );
        write.line( "export { " + builderName + "}" );
    }


    public void setters( List<ValueObjectProperty> properties ) throws IOException {
        for( ValueObjectProperty property : properties ) {
            String propertyName = propertyName( property.name() );
            line( propertyName + "( " + propertyName + " ) {" );
            line( "this." + attributeName( property.name() ) + " = " + propertyName + ";" );
            line( "return this;" );
            line( "}" );
        }
    }

    public void buildMethod( String objectName, List<ValueObjectProperty> properties ) throws IOException {
        line( "build(){" );
        line( "return new " + objectName + "(" +
                String.join( ",", properties.stream().map( prop->"this." + attributeName( prop.name() ) ).collect( Collectors.toList() ) ) +
                ")" );
        line( "}" );
    }

    public void fromObjectMethod( String builderName, List<ValueObjectProperty> properties ) throws IOException, ProcessingException {
        line( "static fromObject( jsonObject ) {" );
        line( "var builder = new " + builderName + "()" );
        PropertiesDeserializationProcessor propertiesDeserializationProcessor = new PropertiesDeserializationProcessor( this );
        for( ValueObjectProperty property : properties ) {
            line( "if( jsonObject[\"" + property.name() + "\"] != undefined ){" );
            propertiesDeserializationProcessor.process( property );
            line( "}" );
        }
        line( "return builder.build();" );
        line( "}" );
    }

    public void fromJsonMethod() throws IOException {
        line( "static fromJson( json ){" );
        line( "var jsonObject = JSON.parse( json );" );
        line( "return this.fromObject( jsonObject );" );
        line( "}" );
    }
}
