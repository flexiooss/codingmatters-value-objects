package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.valueObject.GenerationContext;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class JsValueObjectGenerator implements ParsedYamlProcessor {

    private final File rootDirectory;
    private final String currentPackage;
    private final GenerationContext generationContext;
    private final PackageFilesBuilder packageBuilder;
    private Set<String> flexioJsHelpersImport;

    public JsValueObjectGenerator( File rootDirectory, String currentPackage, String typesPackage, PackageFilesBuilder packageBuilder ) {
        this.rootDirectory = rootDirectory;
        this.currentPackage = currentPackage;
        this.generationContext = new GenerationContext( currentPackage, typesPackage );
        this.flexioJsHelpersImport = new HashSet<>();
        this.flexioJsHelpersImport.add( "deepFreezeSeal" );
        this.flexioJsHelpersImport.add( "assert" );
        this.flexioJsHelpersImport.add( "isNull" );
        this.packageBuilder = packageBuilder;
    }

    public JsValueObjectGenerator( File rootDir, String typesPackage, PackageFilesBuilder packageBuilder ) {
        this( rootDir, typesPackage, typesPackage, packageBuilder );
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {
        rootDirectory.mkdirs();
        for( ParsedValueObject valueObject : spec.valueObjects() ){
            valueObject.process( this );
        }
    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {
        try {
            generateClass( valueObject );
        } catch( Exception e ){
            throw new ProcessingException( "Error processing value object", e );
        }
    }

    private void generateClass( ParsedValueObject valueObject ) throws Exception {
        String objectName = NamingUtility.className( valueObject.name() );
        String fileName = objectName + ".js";

        File targetDirectory = new File( rootDirectory, generationContext.currentPackagePath() );
        packageBuilder.addClass( generationContext.currentPackage(), objectName );
        String targetFile = String.join( "/", targetDirectory.getPath(), fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile, generationContext.typesPackage() ) ) {
            generationContext.writer( write );
            for( ValueObjectProperty property : valueObject.properties() ){
                property.process( this );
            }
            for( String inport : generationContext.imports() ){
                write.line( inport );
            }
            String line = "import { " + String.join( ", ", this.flexioJsHelpersImport ) + " } from 'flexio-jshelpers' ";
            write.line( line );
            write.newLine();
            write.valueObjectClass( valueObject, objectName, write );
            write.newLine();
            write.builderClass( valueObject, objectName, write );
            write.flush();
        }
    }

    private void generateList( ValueObjectTypeList list ) throws ProcessingException {
        try {
            String objectName = NamingUtility.className( list.name() );
            String fileName = objectName + ".js";
            String targetPackage = list.packageName();
            packageBuilder.addList( targetPackage, objectName );
            File targetDirectory = new File( rootDirectory, targetPackage.replace( ".", "/" ) );
            String targetFile = String.join( "/", targetDirectory.getPath(), fileName );

            try( JsClassGenerator write = new JsClassGenerator( targetFile, generationContext.typesPackage() ) ) {
                JsValueObjectGenerator processor = new JsValueObjectGenerator( this.rootDirectory, targetPackage, this.packageBuilder );
                processor.generationContext.writer( write );
//                list.type().process( processor );

                String className = NamingUtility.className( list.name() );
                write.line( "class " + className + " extends Array {" );
                write.line( "constructor( ...args ){" );
                write.line( "super( ...args );" );
                write.line( "}" );
                write.line( "}" );
                write.line( "export { " + className + " }" );
                write.flush();
            }
        } catch( Exception e ){
            throw new ProcessingException( "Error processing list", e );
        }
    }

    private void generateEnum( YamlEnumInSpecEnum inSpecEnum ) throws Exception {
        String objectName = NamingUtility.className( inSpecEnum.name() );
        String fileName = objectName + ".js";
        String targetPackage = generationContext.typesPackage() + "." + inSpecEnum.namespace();
        packageBuilder.addList( targetPackage, objectName );
        File targetDirectory = new File( rootDirectory, targetPackage.replace( ".", "/" ) );
        String targetFile = String.join( "/", targetDirectory.getPath(), fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile, generationContext.typesPackage() ) ) {
            write.line( "import { FlexEnum } from 'flexio-jshelpers'" );
            write.line( "class " + objectName + " extends FlexEnum {" );
            write.line( "}" );
            write.line( objectName + ".initEnum([ " +
                    String.join( ", ", inSpecEnum.values().stream().map( val->"'" + val + "'" ).collect( Collectors.toList() ) )
                    + " ]);" );
            write.line( "export { " + objectName + "}" );
        }
    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        property.type().process( this );
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        generationContext.addImport( "import {FLEXIO_IMPORT_OBJECT} from 'flexio-jshelpers'" );
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        generationContext.addImport( "import {FLEXIO_IMPORT_OBJECT} from 'flexio-jshelpers'" );
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        generationContext.addImport( "import {FLEXIO_IMPORT_OBJECT} from 'flexio-jshelpers'" );
        JsValueObjectGenerator processor = new JsValueObjectGenerator( this.rootDirectory, this.currentPackage + "." + nestedValueObject.namespace(), this.currentPackage, this.packageBuilder );
        processor.process( nestedValueObject.nestValueObject() );
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        generationContext.addImport( "import {FLEXIO_IMPORT_OBJECT} from 'flexio-jshelpers'" );
        list.type().process( this );
        generateList( list );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        switch( primitiveType.type() ){
            case DATE:
                this.flexioJsHelpersImport.add( "FlexDate" );
                break;
            case TIME:
                this.flexioJsHelpersImport.add( "FlexTime" );
                break;
            case DATE_TIME:
                this.flexioJsHelpersImport.add( "FlexDateTime" );
                break;
            case TZ_DATE_TIME:
                this.flexioJsHelpersImport.add( "FlexZonedDateTime" );
                break;
            case BOOL:
                this.flexioJsHelpersImport.add( "isBoolean" );
                break;
            case STRING:
                this.flexioJsHelpersImport.add( "isString" );
                break;
            case BYTES:
                this.flexioJsHelpersImport.add( "isString" );
                break;
            case OBJECT:
                this.flexioJsHelpersImport.add( "isObject" );
                break;
            case INT:
                this.flexioJsHelpersImport.add( "isNumber" );
                break;
            case DOUBLE:
                this.flexioJsHelpersImport.add( "isNumber" );
                break;
            case LONG:
                this.flexioJsHelpersImport.add( "isNumber" );
                break;
            case FLOAT:
                this.flexioJsHelpersImport.add( "isNumber" );
                break;
            default:
                System.out.println( "Enum constant import not handled: " + primitiveType.type().name() );
                break;
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) {
        generationContext.addImport( "import {FLEXIO_IMPORT_OBJECT} from 'flexio-jshelpers'" );
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        generationContext.addImport( "import {FLEXIO_IMPORT_OBJECT} from 'flexio-jshelpers'" );
        try {
            generateEnum( inSpecEnum );
        } catch( Exception e ){
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }

}
