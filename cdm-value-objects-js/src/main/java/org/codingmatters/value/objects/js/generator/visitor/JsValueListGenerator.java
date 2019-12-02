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

public class JsValueListGenerator implements ParsedYamlProcessor {

    private final String typesPackage;
    private final String filePath;

    public JsValueListGenerator( String typesPackage, String filePath ) {
        this.typesPackage = typesPackage;
        this.filePath = filePath;
    }

    private void writeList( JsClassGenerator write, ValueObjectType inSpecValueObject, String name ) throws IOException, ProcessingException {
        write.line( JsValueObjectGenerator.FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
        write.line( "import { assertType, isBoolean, isObject, assert, isNumber, isInteger, isNull, isString } from '@flexio-oss/assert'" );
        write.line( "import { deepFreezeSeal } from '@flexio-oss/js-generator-helpers'" );
        write.line( "import { FlexArray } from '@flexio-oss/flex-types'" );
        write.extendGenericTypeJsDoc( inSpecValueObject );
        String className = NamingUtility.className( name + "List" );
        write.line( "class " + className + " extends FlexArray {" );
        write.listConstructor();
        write.elementAccessor( inSpecValueObject );
        write.builderValidateElement( inSpecValueObject );
        write.line( "}" );
        write.line( "export { " + className + " }" );
        write.flush();
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
        try( JsClassGenerator write = new JsClassGenerator( filePath, typesPackage ) ){
            writeList( write, externalValueObject, externalValueObject.objectReference() );
        } catch( Exception e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {

//        try( JsClassGenerator write = new JsClassGenerator( filePath, typesPackage ) ){
//            writeList( write, inSpecValueObject, inSpecValueObject.inSpecValueObjectName() );
//        } catch( Exception e ) {
//            throw new ProcessingException( e );
//        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {

//        try( JsClassGenerator write = new JsClassGenerator( filePath, typesPackage ) ){
//            writeList( write, nestedValueObject, nestedValueObject.nestValueObject().name() );
//        } catch( Exception e ) {
//            throw new ProcessingException( e );
//        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        if( list.type() instanceof ValueObjectTypeList ){
            try( JsClassGenerator write = new JsClassGenerator( filePath, typesPackage ) ){
                writeList( write, list.type(), list.name() );
            } catch( Exception e ) {
                throw new ProcessingException( "error generating list", e );
            }
        }
        list.type().process( this );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try( JsClassGenerator write = new JsClassGenerator( filePath, typesPackage ) ){
            writeList( write, inSpecEnum, inSpecEnum.name() );
        } catch( Exception e ) {
            throw new ProcessingException( e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {

    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
//        try( JsClassGenerator write = new JsClassGenerator( filePath, typesPackage ) ){
//            writeList( write, parsedEnum, parsedEnum.name() );
//        } catch( Exception e ) {
//            throw new ProcessingException( e );
//        }
    }
}
