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

public class JsObjectValueTypeReferenceProcessor implements ParsedYamlProcessor {
    private final JsClassGenerator write;

    public JsObjectValueTypeReferenceProcessor( JsClassGenerator jsClassGenerator ) {
        this.write = jsClassGenerator;
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {

    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        try {
            write.string( NamingUtility.className( externalValueObject.objectReference() ) );
        } catch( IOException e ) {
            throw new ProcessingException( "Error Processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            write.string( NamingUtility.className( inSpecValueObject.inSpecValueObjectName() ) );
        } catch( IOException e ) {
            throw new ProcessingException( "Error Processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            write.string( NamingUtility.className( nestedValueObject.nestValueObject().name() ) );
        } catch( IOException e ) {
            throw new ProcessingException( "Error Processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            new JsValueListTypeReferenceProcessor(write).process( list );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing list type", e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            switch( primitiveType.type() ) {
                case STRING:
                case BYTES:
                    write.string( "string" );
                    break;
                case FLOAT:
                case LONG:
                case INT:
                case DOUBLE:
                    write.string( "number" );
                    break;
                case BOOL:
                    write.string( "boolean" );
                    break;
                case TIME:
                    write.string( "FlexTime" );
                    break;
                case DATE:
                    write.string( "FlexDate" );
                    break;
                case DATE_TIME:
                    write.string( "FlexDateTime" );
                    break;
                case TZ_DATE_TIME:
                    write.string( "FlexZonedDateTime" );
                    break;
                case OBJECT:
                    write.string( "object" );
                    break;
                default:
                    throw new ProcessingException( "Type is not defined" );
            }
        } catch( IOException e ) {
            throw new ProcessingException( "Error Processing type", e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            write.string( NamingUtility.className( externalEnum.enumReference() ) );
        } catch( IOException e ) {
            throw new ProcessingException( "Error Processing type", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            write.string( NamingUtility.className( inSpecEnum.name() ) );
        } catch( IOException e ) {
            throw new ProcessingException( "Error Processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try {
            write.string( parsedEnum.name() );
        } catch( IOException e ){
            throw new ProcessingException( "Error Processing type", e );
        }
    }
}
