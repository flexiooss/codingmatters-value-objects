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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class JsClassGeneratorSpecProcessor implements ParsedYamlProcessor {

    private final File rootDirectory;
    private final String rootPackage;
    private final GenerationContext generationContext;
    private final PackageFilesBuilder packageBuilder;
    private Set<String> flexioJsHelpersImport;

    public JsClassGeneratorSpecProcessor( File rootDirectory, String rootPackage ) {
        this.rootDirectory = rootDirectory;
        this.rootPackage = rootPackage;
        this.generationContext = new GenerationContext( rootPackage );
        this.flexioJsHelpersImport = new HashSet<>();
        this.flexioJsHelpersImport.add( "deepFreezeSeal" );
        this.packageBuilder = new PackageFilesBuilder();
    }

    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {
        rootDirectory.mkdirs();
        for( ParsedValueObject valueObject : spec.valueObjects() ) {
            valueObject.process( this );
        }
    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {
        try {
            generateClass( valueObject );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing value object", e );
        }
    }

    private void generateClass( ParsedValueObject valueObject ) throws Exception {
        String objectName = NamingUtility.className( valueObject.name() );
        String fileName = objectName + ".js";

        File targetDirectory = new File( rootDirectory, generationContext.currentPackagePath() );
        packageBuilder.addClass( generationContext.currentPackage(), objectName );
        String targetFile = String.join( "/", targetDirectory.getPath(), fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile ) ) {
            generationContext.writer( write );
            for( ValueObjectProperty property : valueObject.properties() ) {
                property.process( this );
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
            String targetPackage = this.rootPackage + "." + list.namespace();
            packageBuilder.addList( targetPackage, objectName );
            File targetDirectory = new File( rootDirectory, targetPackage.replace( ".", "/" ) );
            String targetFile = String.join( "/", targetDirectory.getPath(), fileName );

            try( JsClassGenerator write = new JsClassGenerator( targetFile ) ) {
                JsClassGeneratorSpecProcessor processor = new JsClassGeneratorSpecProcessor( this.rootDirectory, targetPackage );
                processor.generationContext.writer( write );
                list.type().process( processor );

                String className = NamingUtility.className( list.name() );
                write.line( "class " + className + " extends Array {" );
                write.line( "constructor( ...args ){" );
                write.line( "super( ...args );" );
                write.line( "}" );
                write.line( "}" );
                write.line( "export { " + className + " }" );
                write.flush();
            }
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing list", e );
        }
    }

    private void generateEnum( YamlEnumInSpecEnum inSpecEnum ) throws Exception {
        String objectName = NamingUtility.className( inSpecEnum.name() );
        String fileName = objectName + ".js";
        String targetPackage = this.rootPackage + "." + inSpecEnum.namespace();
        File targetDirectory = new File( rootDirectory, targetPackage.replace( ".", "/" ) );
        String targetFile = String.join( "/", targetDirectory.getPath(), fileName );
        try( JsClassGenerator write = new JsClassGenerator( targetFile ) ) {
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
    public void process( ObjectTypeExternalValue externalValueObject ) {
        // TODO import
    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {
        try {
            String className = NamingUtility.className( inSpecValueObject.inSpecValueObjectName() );
            String builderName = NamingUtility.builderName( inSpecValueObject.inSpecValueObjectName() );
            String packageName = NamingUtility.findPackage( generationContext.currentPackage(), rootPackage + "." + inSpecValueObject.inSpecValueObjectName() );
            generationContext.write().line( "import { " + className + ", " + builderName + " } from '" + packageName + "'" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec value object", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            String className = NamingUtility.className( nestedValueObject.nestValueObject().name() );
            String builderName = NamingUtility.builderName( nestedValueObject.nestValueObject().name() );
            String packageName = NamingUtility.findPackage( generationContext.currentPackage(), rootPackage + "." + nestedValueObject.namespace() );
            generationContext.write().line( "import { " + className + ", " + builderName + " } from '" + packageName + "/" + className + "'" );

            JsClassGeneratorSpecProcessor processor = new JsClassGeneratorSpecProcessor( this.rootDirectory, this.rootPackage + "." + nestedValueObject.namespace() );
            processor.process( nestedValueObject.nestValueObject() );

        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec value object", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            String className = NamingUtility.className( list.name() );
            String packageName = NamingUtility.findPackage( generationContext.currentPackage(), rootPackage + "." + list.namespace() + "." + className );
            generationContext.write().line( "import { " + className + " } from '" + packageName + "'" );
            list.type().process( this );

            generateList( list );

        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec value object list: " + list.name(), e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {
        switch( primitiveType.type() ) {
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
            default:
                // nothing to do
                break;
        }
    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) {
        // TODO import
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            String className = NamingUtility.className( inSpecEnum.name() );
            String packageName = NamingUtility.findPackage( generationContext.currentPackage(), rootPackage + "." + inSpecEnum.namespace() + "." + className );
            System.out.println( className + " / " + packageName + " / " + generationContext + " / " + generationContext.write() );
            generationContext.write().line( "import { " + className + " } from '" + packageName + "'" ); // TODO ?

            generateEnum( inSpecEnum );
        } catch( Exception e ) {
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }

}
