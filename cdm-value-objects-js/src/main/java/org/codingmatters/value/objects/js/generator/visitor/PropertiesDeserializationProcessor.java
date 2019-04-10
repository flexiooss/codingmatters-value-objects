package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.parser.model.ParsedEnum;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public class PropertiesDeserializationProcessor implements ParsedYamlProcessor {

    private final JsFileWriter write;
    private String propertyName;
    private String currentVariable;
    private char currentIndex = 'a';
    private final String typesPackage;

    public PropertiesDeserializationProcessor( JsFileWriter jsClassGenerator, String typesPackage ) {
        this.write = jsClassGenerator;
        this.typesPackage = typesPackage;
    }

    public void currentVariable( String currentVariable ) {
        this.currentVariable = currentVariable;
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
            String builderName = NamingUtility.builderFullName( reference );
            write.string( builderName + ".fromObject( " + currentVariable + " ).build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderFullName( typesPackage + "." + inSpecValueObject.inSpecValueObjectName() );
            write.string( builderName + ".fromObject( " + currentVariable + " ).build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            String builderName = NamingUtility.builderFullName( typesPackage + "." + nestedValueObject.namespace() + "." + nestedValueObject.nestValueObject().name() );
            write.string( builderName + ".fromObject( " + currentVariable + " ).build()" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            String var = generateVarName();
            String listClassName = NamingUtility.classFullName( list.packageName() + "." + list.name() );
            write.string( "new " + listClassName + "( ..." + currentVariable + ".map( " + var + "=>" );
            currentVariable = var;
            list.type().process( this );
            write.string( " ))" );
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
            String className = NamingUtility.classFullName( externalEnum.enumReference() );
            write.string( className + ".enumValueOf( " + currentVariable + " )" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.classFullName( typesPackage + "." + inSpecEnum.namespace() + "." + inSpecEnum.name() );
            write.string( className + ".enumValueOf( " + currentVariable + " )" );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.classFullName( parsedEnum.packageName() + "." + parsedEnum.name() );
            write.string( className + ".enumValueOf( " + currentVariable + " )" );
            throw new ProcessingException( "HELLO"  );
        } catch( IOException e ){
            throw new ProcessingException( "Error processing type", e );
        }
    }

}
