package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by nelt on 10/6/16.
 */
public class ValueChanger {
    private final ValueConfiguration types;
    private final MethodSpec configure;

    public ValueChanger(ValueConfiguration types) {
        this.types = types;
        this.configure = MethodSpec.methodBuilder("configure")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(this.types.valueBuilderType(), "builder")
                .returns(this.types.valueBuilderType())
                .build();
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder("Changer")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethod(this.configure)
                .build();
    }
}
