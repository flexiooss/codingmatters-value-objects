package org.codingmatters.value.objects.java;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.model.ValueObjectProperty;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;

import java.util.ArrayList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;

public class JavaObjectBuilderHelper {

    private final TypeResolver typeResolver;

    public JavaObjectBuilderHelper() {
        this.typeResolver = new TypeResolver();
    }

    public TypeSpec builderMethod( ParsedValueObject valueObject ) {
        return TypeSpec.classBuilder( "Builder" )
                .addModifiers( PUBLIC, STATIC )
                .addMethod( buildMethod( valueObject ) )
                .addFields( fields(valueObject) )
                .addMethods( setters(valueObject) )
                .build();
    }

    private List<FieldSpec> fields( ParsedValueObject valueObject ) {
        List<FieldSpec> fields = new ArrayList<>();
        for( ValueObjectProperty property : valueObject.properties() ){
            fields.add( FieldSpec.builder( typeName( property.type() ), property.name(), PRIVATE ).build() );
        }
        return fields;
    }

    private List<MethodSpec> setters( ParsedValueObject valueObject ) {
        List<MethodSpec> setters = new ArrayList<>();
        for( ValueObjectProperty property : valueObject.properties() ){

        }
        return setters;
    }

    private MethodSpec buildMethod( ParsedValueObject valueObject ) {
        List<String> constructorParameters = new ArrayList<>();
        List<String> generationArgs = new ArrayList<>();
        generationArgs.add( "type" );
        valueObject.properties().forEach( field -> {
            constructorParameters.add( "this.$N" );
            generationArgs.add( field.name() );
        } );
        String constructorParametersFormat = String.join( ", ", constructorParameters );
        typeResolver.process( valueObject );
        return MethodSpec.constructorBuilder()
                .addModifiers( PUBLIC )
                .returns( typeResolver.type() )
                .addStatement(
                        "return new $T(" + constructorParametersFormat + ")",
                        generationArgs.toArray()
                ).build();
    }

}
