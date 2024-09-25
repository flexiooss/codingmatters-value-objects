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

import java.io.IOException;

public class JsValueListDeserializationProcessor implements ParsedYamlProcessor {

    private final JsFileWriter write;
    private String currentVariable;
    private String varMap;
    private final String typesPackage;
    private int opened;

    public JsValueListDeserializationProcessor( JsFileWriter write, String currentVariable, String varMap, String typesPackage ) {
        this.write = write;
        this.currentVariable = currentVariable;
        this.varMap = varMap;
        this.typesPackage = typesPackage;
        this.opened = 0;
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
            write.string( "new " + NamingUtility.classFullName( externalValueObject.objectReference() ) + "List( ..." + currentVariable + ".map( " + varMap + " => isNull(" + varMap + ") ? null: " );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( inSpecValueObject.packageName() + "." + NamingUtility.className( inSpecValueObject.inSpecValueObjectName() ) ) + "List( ..." + currentVariable + ".map( " + varMap + " => isNull(" + varMap + ") ? null:" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( nestedValueObject.namespace() + "." + nestedValueObject.nestValueObject().name() ) + "List( ..." + currentVariable + ".map( " + varMap + " => isNull(" + varMap + ") ? null:" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        if( list.type() instanceof ValueObjectTypeList ){
            try{
                write.string("new " + NamingUtility.classFullName(list.packageName() + "." + list.name() + "List(..." + currentVariable + ".map(" + varMap + " =>  isNull(" + varMap + ") ? null: "));
                currentVariable = varMap;
                varMap = varMap + "bis";
                opened += 2;
                list.type().process( this );
            } catch( IOException e ) {
                throw new ProcessingException( e );
            }
        } else {
            list.type().process( this );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( "io.flexio.flex_types.arrays." ) );
            new JsValueListTypeReferenceProcessor( write ).process( primitiveType );
            write.string( "( ..." + currentVariable + ".map( " + varMap + " =>  isNull(" + varMap + ") ? null:" );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( NamingUtility.externalEnumRef(externalEnum.enumReference())  ) + "List( ..." + currentVariable + ".map( " + varMap + " => " );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( typesPackage + "." + inSpecEnum.namespace() + "." + inSpecEnum.name() ) + "List( ..." + currentVariable + ".map( " + varMap + " => " );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( externalType.typeReference() ) + "List( ..." + currentVariable + ".map( " + varMap + " => isNull(" + varMap + ") ? null: " );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        try{
            write.string( "new " + NamingUtility.classFullName( parsedEnum.packageName() + "." + parsedEnum.name() ) + "List( ..." + currentVariable + ".map( " + varMap + " => isNull(" + varMap + ") ? null: " );
        } catch( IOException e ) {
            throw new ProcessingException( e );
        }
    }

    public String currentVar() {
        return varMap;
    }

    public int opened() {
        return opened;
    }
}
