package org.codingmatters.value.objects.js.generator.visitor;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.NamingUtility;
import org.codingmatters.value.objects.js.generator.packages.PackageFilesBuilder;
import org.codingmatters.value.objects.js.generator.valueObject.GenerationContext;
import org.codingmatters.value.objects.js.generator.valueObject.JsClassGenerator;
import org.codingmatters.value.objects.js.parser.model.*;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class JsValueObjectGenerator implements ParsedYamlProcessor {

    private final File rootDirectory;
    private final String currentPackage;
    private final GenerationContext generationContext;
    private final PackageFilesBuilder packageBuilder;
    private Set<String> flexioTypesImport;
    private Set<String> flexioGeneratorHelperImport;
    private Set<String> flexioAssertImport;

    public JsValueObjectGenerator( File rootDirectory, String currentPackage, String typesPackage, PackageFilesBuilder packageBuilder ) {
        this.rootDirectory = rootDirectory;
        this.currentPackage = currentPackage;
        this.generationContext = new GenerationContext( currentPackage, typesPackage );
        generationContext.addImport( "import { deepFreezeSeal } from '@flexio-oss/js-generator-helpers'" );
        this.flexioTypesImport = new HashSet<>();
        this.flexioGeneratorHelperImport = new HashSet<>();
        this.flexioAssertImport = new HashSet<>();
        this.flexioGeneratorHelperImport.add( "deepFreezeSeal" );
        this.flexioAssertImport.add( "assert" );
        this.flexioAssertImport.add( "isNull" );
        this.packageBuilder = packageBuilder;
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
            String line = "import { " + String.join( ", ", this.flexioAssertImport ) + " } from '@flexio-oss/assert' ";
            write.line( line );
            line = "import { " + String.join( ", ", this.flexioGeneratorHelperImport ) + " } from '@flexio-oss/global-import-registry' ";
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
                list.type().process( this );
                for( String inport : generationContext.imports() ){
                    write.line( inport );
                }
                flexioTypesImport.add( "FlexArray" );
                String line = "import { " + String.join( ", ", this.flexioTypesImport ) + " } from '@flexio-oss/flex-types' ";
                write.line( line );
                String className = NamingUtility.className( list.name() );
                write.extendGenericTypeJsDoc( list.type() );
                write.line( "class " + className + " extends FlexArray {" );
                write.listConstructor();
                write.elementAccessor( list.type() );
                write.validateElement( list.type() );
                write.line( "}" );
                write.line( "export { " + className + " }" );
                write.flush();
            }
        } catch( Exception e ){
            throw new ProcessingException( "Error processing list", e );
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
        packageBuilder.addList( targetPackage, objectName );
        String fileName = objectName + ".js";
        File targetDirectory = new File( rootDirectory, targetPackage.replace( ".", "/" ) );
        File targetFile = new File( targetDirectory, fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile.getPath(), generationContext.typesPackage() ) ) {
            write.line( "import { FlexEnum } from '@flexio-oss/flex-types'" );
            write.line( "class " + objectName + " extends FlexEnum {" );
            write.line( "}" );
            write.line( objectName + ".initEnum([" +
                    String.join( ", ", values.stream().map( val->"'" + val + "'" ).collect( Collectors.toList() ) )
                    + "])" );
            write.line( "export { " + objectName + " }" );
        }
    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        property.type().process( this );
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry'" );
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry'" );
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry" );
        JsValueObjectGenerator processor = new JsValueObjectGenerator( this.rootDirectory, this.currentPackage + "." + nestedValueObject.namespace(), this.currentPackage, this.packageBuilder );
        processor.process( nestedValueObject.nestValueObject() );
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry'" );

        list.type().process( this );
        generateList( list );
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        switch( primitiveType.type() ){
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
            case BYTES:
                this.flexioAssertImport.add( "isString" );
                break;
            case OBJECT:
                this.flexioAssertImport.add( "isObject" );
                break;
            case INT:
                this.flexioAssertImport.add( "isNumber" );
                break;
            case DOUBLE:
                this.flexioAssertImport.add( "isNumber" );
                break;
            case LONG:
                this.flexioAssertImport.add( "isNumber" );
                break;
            case FLOAT:
                this.flexioAssertImport.add( "isNumber" );
                break;
            default:
                System.out.println( "Enum constant import not handled: " + primitiveType.type().name() );
                break;
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry'" );
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry'" );
        try {
            generateInSpecEnum( inSpecEnum );
        } catch( Exception e ){
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {
        throw new NotImplementedException();
    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {
        generationContext.addImport( "import { globalFlexioImport } from '@flexio-oss/global-import-registry'" );
        try {
            generateTypeEnum( parsedEnum );
        } catch( Exception e ){
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }


}
