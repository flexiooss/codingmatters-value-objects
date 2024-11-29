package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.GenerationException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.valueObject.GenerationContext;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.*;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsValueObjectGenerator implements ParsedYamlProcessor {

    public static final String FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE = "import { globalFlexioImport } from '@flexio-oss/js-commons-bundle/global-import-registry'";
    private final File rootDirectory;
    private final String currentPackage;
    private final boolean generateList;
    private final GenerationContext generationContext;
    private final PackageFilesBuilder packageBuilder;
    private Set<String> flexioTypesImport;
    private Set<String> flexioGeneratorHelperImport;
    private Set<String> flexioAssertImport;

    public JsValueObjectGenerator( File rootDirectory, String currentPackage, String typesPackage, PackageFilesBuilder packageBuilder ) {
        this( rootDirectory, currentPackage, typesPackage, packageBuilder, true );
    }

    public JsValueObjectGenerator( File rootDirectory, String currentPackage, String typesPackage, PackageFilesBuilder packageBuilder, boolean generateList ) {
        this.rootDirectory = rootDirectory;
        this.currentPackage = currentPackage;
        this.generationContext = new GenerationContext( currentPackage, typesPackage );
        this.flexioTypesImport = new HashSet<>();
        this.flexioGeneratorHelperImport = new HashSet<>();
        this.flexioAssertImport = new HashSet<>();
        this.flexioAssertImport.add( "assert" );
        this.flexioAssertImport.add( "assertType" );
        this.flexioAssertImport.add( "isNull" );
        this.flexioAssertImport.add( "isObject" );
        this.flexioAssertImport.add( "isString" );
        this.flexioAssertImport.add( "isFunction" );
        this.flexioAssertImport.add( "TypeCheck" );
        this.flexioAssertImport.add( "assertInstanceOf" );
        this.flexioGeneratorHelperImport.add( "deepFreezeSeal" );
        this.flexioGeneratorHelperImport.add( "valueObjectInterface" );
        this.flexioGeneratorHelperImport.add( "valueObjectBuilderInterface" );
        this.packageBuilder = packageBuilder;
        this.generateList = generateList;
    }

    public JsValueObjectGenerator( File rootDir, String typesPackage, PackageFilesBuilder packageBuilder ) {
        this( rootDir, typesPackage, typesPackage, packageBuilder );
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {
        rootDirectory.mkdirs();
        for( ParsedType valueObject : spec.valueObjects() ){
            valueObject.process( this );
        }
    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {
        try{
            generateClass( valueObject );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing value object", e );
        }
    }

    private void generateClass( ParsedValueObject valueObject ) throws Exception {
        File targetDirectory = new File( rootDirectory, generationContext.currentPackagePath() );
        String objectName = NamingUtility.className( valueObject.name() );
        String targetFile = String.join( "/", targetDirectory.getPath(), objectName + "List.js" );

        if( generateList ){
            new JsValueListGenerator( generationContext, targetFile ).process(
                    new ValueObjectTypeList(
                            valueObject.name() + "List",
                            new ObjectTypeExternalValue( generationContext.currentPackage() + "." + valueObject.name() ),
                            "null"
                    )
            );
        }

        String fileName = objectName + ".js";
        packageBuilder.addClass( generationContext.currentPackage(), objectName, generateList );

        targetFile = String.join( "/", targetDirectory.getPath(), fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile, generationContext ) ){
            generationContext.writer( write );
            for( ValueObjectProperty property : valueObject.properties() ){
                property.process( this );
            }
            this.writeImports( write );

            write.newLine();
            write.valueObjectClass( valueObject, objectName, write );
            write.newLine();
            write.builderClass( valueObject, objectName, write );
            write.flush();
        }
    }

    private void generateList( ValueObjectTypeList list ) throws ProcessingException  {
        if( list.type() instanceof ValueObjectTypeList ){
            File targetDirectory = new File( rootDirectory, list.packageName().replace( '.', '/' ) );
            String objectName = list.name() + "List";
            String targetFile = String.join( "/", targetDirectory.getPath(), objectName + ".js" );
            new JsValueListGenerator( generationContext, targetFile ).process( list );
            try{
                packageBuilder.addEnum( list.packageName(), objectName, false );
            } catch( GenerationException e ) {
                throw new ProcessingException( "Error adding list to package builder", e );
            }
            list.type().process( this );
        }
    }

    private void generateInSpecEnum( YamlEnumInSpecEnum inSpecEnum ) throws Exception {
        String objectName = NamingUtility.className( inSpecEnum.name() );
        String targetPackage = generationContext.typesPackage() + "." + inSpecEnum.namespace();
        generateEnum( objectName, targetPackage, inSpecEnum.values() );
    }

    private void generateTypeEnum( ParsedEnum parsedEnum ) throws Exception {
        String objectName = NamingUtility.className( parsedEnum.name() );
        String targetPackage = generationContext.typesPackage();
        generateEnum( objectName, targetPackage, parsedEnum.enumValues() );
    }

    private void generateEnum( String objectName, String targetPackage, List<String> values ) throws Exception {
        File targetDirectory = new File( rootDirectory, targetPackage.replace( ".", "/" ) );
        File targetFile = new File( targetDirectory, objectName + "List.js" );
        String enumReference = targetPackage + "." + objectName;
        System.out.println( "GENERATE ENUM " + enumReference + " IN " + targetFile.getPath().replace( "/home/nico/workspaces/codingmatters/codingmatters-value-objects/cdm-value-objects-js/target/js-test", "OK" ) );
        if( generateList ){
            String namespace;
            if( targetPackage.equals( generationContext.typesPackage() ) ){
                namespace = null;
            } else {
                namespace = targetPackage.replace( generationContext.typesPackage() + ".", "" );
            }
            new JsValueListGenerator( generationContext, targetFile.getPath() ).process(
                    new ValueObjectTypeList(
                            null,
                            new YamlEnumInSpecEnum( objectName, namespace ),
                            targetPackage
                    )
            );
        }
        String fileName = objectName + ".js";
        packageBuilder.addEnum( targetPackage, objectName, generateList );
        targetFile = new File( targetDirectory, fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile.getPath(), generationContext ) ){
            write.line( "import { FlexEnum } from '@flexio-oss/js-commons-bundle/flex-types'" );
            write.line( "/**" );
            write.line( "* @readonly" );
            write.line( "* @enum {" + objectName + "}" );
            write.line( "*/" );
            write.line( "class " + objectName + " extends FlexEnum {" );
            for( String value : values ){
                write.line( "/**" );
                write.line( "* @static" );
                write.line( "* @property {" + objectName + "} " + value );
                write.line( "*/" );
            }
            write.line( "}" );
            write.line( objectName + ".initEnum([" +
                    String.join( ", ", values.stream().map( val -> "'" + val + "'" ).collect( Collectors.toList() ) )
                    + "])" );
            write.line( "export { " + objectName + " }" );
        }
    }

    private void writeImports( JsClassGenerator write ) throws Exception {
        for( String inport : generationContext.imports() ){
            write.line( inport );
        }
        if( !this.flexioAssertImport.isEmpty() ){
            String line = "import { " + String.join( ", ", this.flexioAssertImport ) + " } from '@flexio-oss/js-commons-bundle/assert'";
            write.line( line );
        }
        if( !this.flexioGeneratorHelperImport.isEmpty() ){
            String line = "import { " + String.join( ", ", this.flexioGeneratorHelperImport ) + " } from '@flexio-oss/js-commons-bundle/js-generator-helpers'";
            write.line( line );
        }
        if( !this.flexioTypesImport.isEmpty() ){
            String line = "import { " + String.join( ", ", this.flexioTypesImport ) + " } from '@flexio-oss/js-commons-bundle/flex-types'";
            write.line( line );
        }
    }


    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
        property.type().process( this );
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
        JsValueObjectGenerator processor = new JsValueObjectGenerator(
                this.rootDirectory,
                this.generationContext.typesPackage() + "." + nestedValueObject.namespace(),
                this.currentPackage,
                this.packageBuilder
        );
        processor.process( nestedValueObject.nestValueObject() );
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        list.type().process( this );
        generateList( list );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        switch( primitiveType.type() ) {
            case DATE:
                this.flexioTypesImport.add( "FlexDate" );
                break;
            case TIME:
                this.flexioTypesImport.add( "FlexTime" );
                break;
            case DATE_TIME:
                this.flexioTypesImport.add( "FlexDateTime" );
                break;
            case TZ_DATE_TIME:
                this.flexioTypesImport.add( "FlexZonedDateTime" );
                break;
            case BOOL:
                this.flexioAssertImport.add( "isBoolean" );
                break;
            case STRING:
                this.flexioAssertImport.add( "isString" );
                break;
            case OBJECT:
                this.flexioAssertImport.add( "isObject" );
                break;
            case INT:
            case LONG:
                this.flexioAssertImport.add( "isInteger" );
                break;
            case DOUBLE:
            case FLOAT:
                this.flexioAssertImport.add( "isNumber" );
                break;
            case BYTES:
                this.flexioAssertImport.add( "isBinary" );
                break;
            default:
                System.out.println( "Enum constant import not handled: " + primitiveType.type().name() );
                break;
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
        try{
            generateInSpecEnum( inSpecEnum );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        generationContext.addImport( FLEXIO_GLOBAL_IMPORT_REGISTRY_LINE );
        try{
            generateTypeEnum( parsedEnum );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }
}
