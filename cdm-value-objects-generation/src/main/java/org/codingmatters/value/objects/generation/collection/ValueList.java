package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Collection;

/**
 * Created by nelt on 10/11/16.
 */
public class ValueList {

    private final String packageName;

    public ValueList(String packageName) {
        this.packageName = packageName;
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder("ValueList")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(TypeName.OBJECT, "o")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("containsAll")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(ClassName.get(Collection.class), "c")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .build();
    }
}
