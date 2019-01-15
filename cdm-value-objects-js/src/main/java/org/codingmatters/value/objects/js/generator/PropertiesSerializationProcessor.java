package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeExternalValue;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeInSpecValueObject;
import org.codingmatters.value.objects.js.parser.model.types.ObjectTypeNested;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypeList;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectTypePrimitiveType;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumExternalEnum;
import org.codingmatters.value.objects.js.parser.model.types.YamlEnumInSpecEnum;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

/**
 * Created by nico on 15/01/19.
 */
public class PropertiesSerializationProcessor implements ParsedYamlProcessor {
    private final JsClassWriter writer;
    private String currentProperty;

    public PropertiesSerializationProcessor( JsClassWriter jsClassWriter ) {
        this.writer = jsClassWriter;
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
            writer.indent();
            writer.string( "jsonObject[\"" + currentProperty + "\"] = this." + currentProperty + "()" );
            property.type().process( this );
            writer.string( ";" );
            writer.newLine();
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing object value", e );
        }
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        try {
            writer.string( ".toObject()" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            writer.string( ".toObject()" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            writer.string( ".toObject()" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            writer.string( ".map( x=>x" );
            list.type().process( this );
            writer.string( ")" );
        } catch( IOException e ) {
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
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            writer.string( ".name" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing primitive type: " + currentProperty, e );
        }
    }
}
