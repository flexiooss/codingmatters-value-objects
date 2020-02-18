package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

public class JsValueListTypeReferenceProcessor implements ParsedYamlProcessor {
    private final JsFileWriter write;

    public JsValueListTypeReferenceProcessor( JsFileWriter write ) {
        this.write = write;
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
        try{
            write.string( externalValueObject.objectReference().substring( externalValueObject.objectReference().lastIndexOf( "." ) + 1 ) + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try{
            write.string( inSpecValueObject.inSpecValueObjectName() + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try{
            write.string( nestedValueObject.nestValueObject().name() + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        list.type().process( this );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try{
            switch( primitiveType.type() ) {
                case BOOL:
                    write.string( "BooleanArray" );
                    break;
                case DATE:
                    write.string( "DateArray" );
                    break;
                case DATE_TIME:
                    write.string( "DateTimeArray" );
                    break;
                case DOUBLE:
                    write.string( "DoubleArray" );
                    break;
                case FLOAT:
                    write.string( "FloatArray" );
                    break;
                case INT:
                    write.string( "IntegerArray" );
                    break;
                case LONG:
                    write.string( "LongArray" );
                    break;
                case OBJECT:
                    write.string( "ObjectArray" );
                    break;
                case STRING:
                    write.string( "StringArray" );
                    break;
                case BYTES:
                    write.string( "Blob" );
                    break;
                case TIME:
                    write.string( "TimeArray" );
                    break;
                case TZ_DATE_TIME:
                    write.string( "TzDateTimeArray" );
                    break;
                default:
                    throw new ProcessingException( "Cannot handle this type: " + primitiveType.type() );
            }
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try{
            write.string( externalEnum.enumReference().substring( externalEnum.enumReference().lastIndexOf( "." ) + 1 ) + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try{
            write.string( inSpecEnum.name() + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        try{
            write.string( externalType.typeReference().substring( externalType.typeReference().indexOf( "." ) + 1 ) + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try{
            write.string( parsedEnum.name() + "List" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }
}
