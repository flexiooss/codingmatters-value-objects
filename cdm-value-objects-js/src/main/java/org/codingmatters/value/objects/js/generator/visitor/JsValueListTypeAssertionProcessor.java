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

import java.io.IOException;

public class JsValueListTypeAssertionProcessor implements ParsedYamlProcessor {
    private final String typesPackage;
    private final JsClassGenerator write;

    public JsValueListTypeAssertionProcessor( String rootPackage, JsClassGenerator write ) {
        typesPackage = rootPackage;
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
            write.string( NamingUtility.classFullName( externalValueObject.objectReference() + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try{
            write.string( NamingUtility.classFullName( inSpecValueObject.packageName() + "." + NamingUtility.className(inSpecValueObject.inSpecValueObjectName()) + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try{
            write.string( NamingUtility.classFullName( nestedValueObject.nestValueObject().packageName() + "." + nestedValueObject.nestValueObject().name() + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        if( list.type() instanceof ValueObjectTypeList ){
            try{
                write.string( NamingUtility.classFullName( list.packageName() + "." + list.name() + "List" ) );
            } catch( IOException e ) {
                throw new ProcessingException( e );
            }
        } else {
            list.type().process( this );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        String arrayName;
        try{
            switch( primitiveType.type() ) {
                case BOOL:
                    arrayName = "BooleanArray";
                    break;
                case DATE:
                    arrayName = "DateArray";
                    break;
                case DATE_TIME:
                    arrayName = "DateTimeArray";
                    break;
                case DOUBLE:
                    arrayName = "DoubleArray";
                    break;
                case FLOAT:
                    arrayName = "FloatArray";
                    break;
                case INT:
                    arrayName = "IntegerArray";
                    break;
                case LONG:
                    arrayName = "LongArray";
                    break;
                case OBJECT:
                    arrayName = "ObjectArray";
                    break;
                case BYTES:
                    arrayName = "BlobArray";
                    break;
                case STRING:
                    arrayName = "StringArray";
                    break;
                case TIME:
                    arrayName = "TimeArray";
                    break;
                case TZ_DATE_TIME:
                    arrayName = "TzDateTimeArray";
                    break;
                default:
                    throw new ProcessingException( "Cannot handle this type: " + primitiveType.type() );
            }
            write.string( NamingUtility.classFullName( "io.flexio.flex_types.arrays." + arrayName ) );
        } catch( Exception e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try{
            write.string( NamingUtility.classFullName( NamingUtility.externalEnumRef(externalEnum.enumReference()) + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try{
            write.string( NamingUtility.classFullName( typesPackage + "." + inSpecEnum.namespace() + "." + inSpecEnum.name() + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        try{
            write.string( NamingUtility.classFullName( externalType.typeReference() + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try{
            write.string( NamingUtility.classFullName( parsedEnum.packageName() + parsedEnum.name() + "List" ) );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }
}
