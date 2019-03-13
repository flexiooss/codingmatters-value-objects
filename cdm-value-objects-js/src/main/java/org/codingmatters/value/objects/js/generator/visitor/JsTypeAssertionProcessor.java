package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

import static org.codingmatters.value.objects.js.generator.NamingUtility.propertyName;

public class JsTypeAssertionProcessor implements ParsedYamlProcessor {

    private final JsClassGenerator jsClassGenerator;
    private String currentVariable;


    public JsTypeAssertionProcessor( JsClassGenerator jsClassGenerator ) {
        this.jsClassGenerator = jsClassGenerator;
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {

    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        this.currentVariable = propertyName( property.name() );
        property.type().process( this );
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            switch( primitiveType.type() ){
                case INT:
                case FLOAT:
                case LONG:
                case DOUBLE:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && isNumber( " + currentVariable + " ), '" + currentVariable + " should be a number' );" );
                    break;
                case OBJECT:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && isObject( " + currentVariable + " ), '" + currentVariable + " should be an object' );" );
                    break;
                case BYTES:
                case STRING:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && isString( " + currentVariable + " ), '" + currentVariable + " should be a string' );" );
                    break;
                case BOOL:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && isBoolean( " + currentVariable + " ), '" + currentVariable + " should be a bool' );" );
                    break;
                case DATE:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && " + currentVariable + " instanceof FlexDate, '" + currentVariable + " should be a FlexDate' );" );
                    break;
                case TIME:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && " + currentVariable + " instanceof FlexTime, '" + currentVariable + " should be a FlexTime' );" );
                    break;
                case DATE_TIME:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && " + currentVariable + " instanceof FlexDateTime, '" + currentVariable + " should be a FlexDateTime' );" );
                    break;
                case TZ_DATE_TIME:
                    jsClassGenerator.line( "assert( !isNull(" + currentVariable + " ) && " + currentVariable + " instanceof FlexZonedDateTime, '" + currentVariable + " should be a FlexZonedDateTime' );" );
                    break;
            }
        } catch( IOException e ){
            throw new ProcessingException( "Error generating assertion", e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {

    }
}
