package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.Spliterator;

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
                .addTypeVariable(TypeVariableName.get("E"))
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
                .addMethod(MethodSpec.methodBuilder("get")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(TypeName.INT, "index")
                        .returns(TypeVariableName.get("E"))
                        .build())
                .addMethod(MethodSpec.methodBuilder("size")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(TypeName.INT)
                        .build())
                .addMethod(MethodSpec.methodBuilder("indexOf")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(Object.class, "o")
                        .returns(TypeName.INT)
                        .build())
                .addMethod(MethodSpec.methodBuilder("isEmpty")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("toArray")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addTypeVariable(TypeVariableName.get("T"))
                        .addParameter(ArrayTypeName.of(TypeVariableName.get("T")), "a")
                        .returns(ArrayTypeName.of(TypeVariableName.get("T")))
                        .build())
                .addMethod(MethodSpec.methodBuilder("iterator")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ArrayTypeName.of(TypeVariableName.get("E")))
                        .build())
                //Spliterator<E> spliterator()
                .addMethod(MethodSpec.methodBuilder("spliterator")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Spliterator.class), TypeVariableName.get("E")))
                        .build())
                .build();

    }
}
