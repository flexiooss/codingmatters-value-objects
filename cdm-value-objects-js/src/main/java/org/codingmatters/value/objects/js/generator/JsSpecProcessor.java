package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ParsedYAMLSpec;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.io.File;
import java.io.IOException;


public class JsSpecProcessor implements ParsedYamlProcessor {

    private final File rootDirectory;
    private final String rootPackage;
    private final GenerationContext generationContext;

    public JsSpecProcessor( File rootDirectory, String rootPackage ) {
        this.rootDirectory = rootDirectory;
        this.rootPackage = rootPackage;
        this.generationContext = new GenerationContext( rootPackage );
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
            String objectName = Naming.firstLetterUpperCase( valueObject.name() );
            String fileName = objectName + ".js";

            File targetDirectory = new File( rootDirectory, generationContext.currentPackagePath() );
            String targetFile = String.join( "/", targetDirectory.getPath(), fileName );
            String builderName = objectName + "Builder";

            for( ValueObjectProperty property : valueObject.properties() ) {
                property.process( this );
            }

            JsClassWriter write = new JsClassWriter( targetFile );
            generationContext.writer( write );
            write.line( "class " + objectName + " {" );


        } catch( Exception e ) {
            throw new ProcessingException( "Error processing value object", e );
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
            generationContext.write().line( "import { " + Naming.className( inSpecValueObject.inSpecValueObjectName() ) + ", " + Naming.builderName( inSpecValueObject.inSpecValueObjectName() ) +
                    " } from '" + Naming.findPackage( generationContext.currentPackage(), rootPackage + "." + inSpecValueObject.inSpecValueObjectName() ) + "'" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec value object", e );
        }
    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {
        try {
            generationContext.write().line( "import { " + Naming.className( nestedValueObject.nestValueObject().name() ) + ", " + Naming.builderName( nestedValueObject.nestValueObject().name() ) +
                    " } from '" + Naming.findPackage( generationContext.currentPackage(), rootPackage + "." + nestedValueObject.namespace() ) + "'" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec value object", e );
        }
    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {
        try {
            generationContext.write().line( "import { " + Naming.className( list.name() ) + ", " + Naming.className( list.name() ) +
                    " } from '" + Naming.findPackage( generationContext.currentPackage(), rootPackage + "." + list.namespace() + "." + Naming.className( list.name() ) + "'" ) );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec value object", e );
        }
    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) {

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) {
//        generationContext.write().line( "import { " + externalEnum.enumReference() + " } " );
    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {
        try {
            generationContext.write().line( "import { " + Naming.className( inSpecEnum.name() ) + " } from '" +
                    Naming.findPackage( generationContext.currentPackage(), rootPackage + "." + inSpecEnum.namespace() + "." + Naming.className( inSpecEnum.name() ) ) + "'" );
        } catch( IOException e ) {
            throw new ProcessingException( "Error processing in spec enum", e );
        }
    }
}
