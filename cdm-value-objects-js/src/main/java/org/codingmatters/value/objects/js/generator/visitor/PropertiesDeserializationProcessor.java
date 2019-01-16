package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

public class PropertiesDeserializationProcessor implements ParsedYamlProcessor {

    private final JsClassWriter write;
    private String propertyName;

    public PropertiesDeserializationProcessor( JsClassWriter jsClassWriter ) {
        this.write = jsClassWriter;
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {

    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        try {
            this.propertyName = property.name();
            write.indent();
            write.string( "builder." + property.name() + "( " );
            property.type().process( this );
            write.string( ");" );
            write.newLine();
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing property " + property.name(), e );
        }
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderName( externalValueObject.objectReference() );
            write.string( builderName + ".fromObject( jsonObject['" + propertyName + "'] )" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderName( inSpecValueObject.inSpecValueObjectName() );
            write.string( builderName + ".fromObject( jsonObject['" + propertyName + "'] )" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderName( nestedValueObject.nestValueObject().name() );
            write.string( builderName + ".fromObject( jsonObject['" + propertyName + "'] )" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            write.string( "jsonObject['" + propertyName + "'].map( x=>x" );
            list.type().process( this );
            write.string( " )" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.className( externalEnum.enumReference() );
            write.string( className + ".enumValueOf( jsonObject['" + propertyName + "'] )" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.className( inSpecEnum.name() );
            write.string( className + ".enumValueOf( jsonObject['" + propertyName + "'] )" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing type", e );
        }
    }

}
