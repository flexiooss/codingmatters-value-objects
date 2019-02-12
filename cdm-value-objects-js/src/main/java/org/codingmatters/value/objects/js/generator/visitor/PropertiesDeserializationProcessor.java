package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.IOException;

public class PropertiesDeserializationProcessor implements ParsedYamlProcessor {

    private final JsClassGenerator write;
    private String propertyName;
    private String currentVariable;
    private char currentIndex = 'a';

    public PropertiesDeserializationProcessor( JsClassGenerator jsClassGenerator ) {
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
        try {
            this.propertyName = property.name();
            write.indent();
            write.string( "builder." + NamingUtility.propertyName( property.name() ) + "( " );
            currentVariable = "jsonObject['" + propertyName + "']";
            property.type().process( this );
            write.string( ");" );
            write.newLine();
        } catch( IOException e ){
            throw new ProcessingException( "Error processing property " + property.name(), e );
        }
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        try {
            String reference = externalValueObject.objectReference();
            String builderName = "window.FLEXIO_IMPORT_OBJECT." + reference.substring( 0, reference.lastIndexOf( "." )+1 ) + NamingUtility.builderName( reference );
            write.string( builderName + ".fromObject( " + currentVariable + " ).build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderName( inSpecValueObject.inSpecValueObjectName() );
            write.string( builderName + ".fromObject( " + currentVariable + " ).build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderName( nestedValueObject.nestValueObject().name() );
            write.string( builderName + ".fromObject( " + currentVariable + " ).build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            String var = generateVarName();
            write.string( currentVariable + ".map( " + var + "=>" );
            currentVariable = var;
            list.type().process( this );
            write.string( " )" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    private String generateVarName() {
        return String.valueOf( currentIndex++ );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try {
            switch( primitiveType.type() ){
                case INT:
                case LONG:
                    write.string( "parseInt(" + currentVariable + ")" );
                    break;
                case FLOAT:
                case DOUBLE:
                    write.string( "parseFloat(" + currentVariable + ")" );
                    break;
                case DATE:
                    write.string( "new FlexDate( " + currentVariable + " )" );
                    break;
                case TIME:
                    write.string( "new FlexTime( " + currentVariable + " )" );
                    break;
                case DATE_TIME:
                    write.string( "new FlexDateTime( " + currentVariable + " )" );
                    break;
                case TZ_DATE_TIME:
                    write.string( "new FlexZonedDateTime( " + currentVariable + " )" );
                    break;
                default:
                    write.string( currentVariable );
                    break;
            }
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.className( externalEnum.enumReference() );
            write.string( className + ".enumValueOf( " + className + " )" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.className( inSpecEnum.name() );
            write.string( className + ".enumValueOf( " + currentVariable + " )" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

}
