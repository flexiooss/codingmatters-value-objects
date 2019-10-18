package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

/**
 * Created by nico on 15/01/19.
 */
public class PropertiesSerializationProcessor implements ParsedYamlProcessor {
    private final JsClassGenerator writer;
    private String currentProperty;

    public PropertiesSerializationProcessor( JsClassGenerator jsClassGenerator ) {
        this.writer = jsClassGenerator;
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {

    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        this.currentProperty = property.name();
        try {
            writer.line( "if (!isNull(this." + NamingUtility.attributeName( currentProperty ) + ")) {" );
            writer.indent();
            writer.string( "jsonObject['" + currentProperty + "'] = ");
            if (property.type().equals(ValueObjectTypePrimitiveType.YAML_PRIMITIVE_TYPES.OBJECT)) {
                writer.string("JSON.parse(JSON.stringify(this." + NamingUtility.attributeName(currentProperty) + "))");
            } else {
                writer.string("this." +NamingUtility.attributeName(currentProperty));
                property.type().process( this );
            }
            writer.newLine();
            writer.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing object value", e );
        }
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        try {
            writer.string( ".toObject()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            writer.string( ".toObject()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            writer.string( ".toObject()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {

            writer.string( ".mapToArray(x => x" );
            list.type().process( this );
            writer.string( ")" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            writer.string( ".name" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            writer.string( ".name" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try {
            writer.string( ".name" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }
}
