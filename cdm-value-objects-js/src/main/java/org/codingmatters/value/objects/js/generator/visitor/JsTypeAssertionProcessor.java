package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

import static org.codingmatters.value.objects.js.generator.NamingUtility.propertyName;

public class JsTypeAssertionProcessor implements ParsedYamlProcessor {

    private final JsClassGenerator write;
    private final JsTypeReferenceProcessor jsTypeReferenceProcessor;
    private String currentVariable;


    public JsTypeAssertionProcessor( JsClassGenerator jsClassGenerator ) {
        this.write = jsClassGenerator;
        this.jsTypeReferenceProcessor = new JsTypeReferenceProcessor( jsClassGenerator );
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
        try {
            write.line( "if( !isNull( " + currentVariable + " )){" );
            write.indent();
            write.string( "assert( " + currentVariable + " instanceof " );
            jsTypeReferenceProcessor.process( externalValueObject );
            write.string( ", '" + currentVariable + " should be a " );
            jsTypeReferenceProcessor.process( externalValueObject );
            write.string( "' );" );
            write.newLine();
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing assertion", e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            write.line( "if( !isNull( " + currentVariable + " )){" );
            write.indent();
            write.string( "assert( " + currentVariable + " instanceof " );
            jsTypeReferenceProcessor.process( inSpecValueObject );
            write.string( ", '" + currentVariable + " should be a " );
            jsTypeReferenceProcessor.process( inSpecValueObject );
            write.string( "' );" );
            write.newLine();
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing assertion", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            write.line( "if( !isNull( " + currentVariable + " )){" );
            write.indent();
            write.string( "assert( " + currentVariable + " instanceof " );
            jsTypeReferenceProcessor.process( nestedValueObject );
            write.string( ", '" + currentVariable + " should be a " );
            jsTypeReferenceProcessor.process( nestedValueObject );
            write.string( "' );" );
            write.newLine();
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing assertion", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            write.line( "if( !isNull( " + currentVariable + " )){" );
            write.indent();
            write.string( "assert( " + currentVariable + " instanceof " );
            jsTypeReferenceProcessor.process( list );
            write.string( ", '" + currentVariable + " should be a " );
            jsTypeReferenceProcessor.process( list );
            write.string( "' );" );
            write.newLine();
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing assertion", e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            switch( primitiveType.type() ){
                case INT:
                case FLOAT:
                case LONG:
                case DOUBLE:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( isNumber( " + currentVariable + " ), '" + currentVariable + " should be a number' );" );
                    write.line( "}" );
                    break;
                case OBJECT:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( isObject( " + currentVariable + " ), '" + currentVariable + " should be an object' );" );
                    write.line( "}" );
                    break;
                case BYTES:
                case STRING:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( isString( " + currentVariable + " ), '" + currentVariable + " should be a string' );" );
                    write.line( "}" );
                    break;
                case BOOL:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( isBoolean( " + currentVariable + " ), '" + currentVariable + " should be a bool' );" );
                    write.line( "}" );
                    break;
                case DATE:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( " + currentVariable + " instanceof FlexDate, '" + currentVariable + " should be a FlexDate' );" );
                    write.line( "}" );
                    break;
                case TIME:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( " + currentVariable + " instanceof FlexTime, '" + currentVariable + " should be a FlexTime' );" );
                    write.line( "}" );
                    break;
                case DATE_TIME:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( " + currentVariable + " instanceof FlexDateTime, '" + currentVariable + " should be a FlexDateTime' );" );
                    write.line( "}" );
                    break;
                case TZ_DATE_TIME:
                    write.line( "if( !isNull( " + currentVariable + " )){" );
                    write.line( "assert( " + currentVariable + " instanceof FlexZonedDateTime, '" + currentVariable + " should be a FlexZonedDateTime' );" );
                    write.line( "}" );
                    break;
            }
        } catch( IOException e ){
            throw new ProcessingException( "Error generating assertion", e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            write.line( "if( !isNull( " + currentVariable + " )){" );
            write.indent();
            write.string( "assert( " + currentVariable + " instanceof " );
            jsTypeReferenceProcessor.process( externalEnum );
            write.string( ", '" + currentVariable + " should be a " );
            jsTypeReferenceProcessor.process( externalEnum );
            write.string( "' );" );
            write.newLine();
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing assertion", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            write.line( "if( !isNull( " + currentVariable + " )){" );
            write.indent();
            write.string( "assert( " + currentVariable + " instanceof " );
            jsTypeReferenceProcessor.process( inSpecEnum );
            write.string( ", '" + currentVariable + " should be a " );
            jsTypeReferenceProcessor.process( inSpecEnum );
            write.string( "' );" );
            write.newLine();
            write.line( "}" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing assertion", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }
}
