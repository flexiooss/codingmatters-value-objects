package org.codingmatters.value.objects.java;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.generation.GenerationUtils;
import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.*;
import org.codingmatters.value.objects.js.parser.model.types.*;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import org.codingmatters.value.objects.spec.Spec;

import java.io.File;
import java.security.KeyRep;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class JavaValueObjectGenerator implements ParsedYamlProcessor {

    private final Spec spec;
    private final String rootPackage;
    private final File rootDirectory;
    private final JavaObjectBuilderHelper builderHelper;
    private File packageDestination;
    private TypeSpec.Builder interfaceSpec;
    private TypeSpec.Builder implementationSpec;
    private ParsedType valueObject;

    public JavaValueObjectGenerator( Spec spec, String rootPackage, File toDirectory ) {
        this.spec = spec;
        this.rootPackage = rootPackage;
        this.rootDirectory = toDirectory;
        this.builderHelper = new JavaObjectBuilderHelper();
    }


    @Override
    public void process( ParsedYAMLSpec spec ) throws ProcessingException {
        for( ParsedType valueObject : spec.valueObjects() ){
            this.valueObject = valueObject;
            valueObject.process( this );
        }
    }

    @Override
    public void process( ParsedValueObject valueObject ) throws ProcessingException {
        this.packageDestination = GenerationUtils.packageDir( this.rootDirectory, valueObject.packageName() );
        this.interfaceSpec = TypeSpec.interfaceBuilder( "" );
        interfaceSpec.addType( builderHelper.builderMethod( valueObject ) );
        this.implementationSpec = TypeSpec.classBuilder( "" );
        for( ValueObjectProperty property : valueObject.properties() ){
            property.process( this );
        }
    }

    @Override
    public void process( ValueObjectProperty property ) throws ProcessingException {
        // add getters
        this.interfaceSpec.addMethod( MethodSpec.methodBuilder( property.name() )
//                .returns(  )

                .build()
        );
    }

    @Override
    public void process( ObjectTypeExternalValue externalValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeInSpecValueObject inSpecValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ObjectTypeNested nestedValueObject ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeList list ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypePrimitiveType primitiveType ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumExternalEnum externalEnum ) throws ProcessingException {

    }

    @Override
    public void process( YamlEnumInSpecEnum inSpecEnum ) throws ProcessingException {

    }

    @Override
    public void process( ValueObjectTypeExternalType externalType ) throws ProcessingException {

    }

    @Override
    public void process( ParsedEnum parsedEnum ) throws ProcessingException {

    }
}
