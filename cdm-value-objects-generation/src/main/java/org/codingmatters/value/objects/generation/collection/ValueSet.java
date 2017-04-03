package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Collection;

/**
 * Created by nelt on 11/19/16.
 */
public class ValueSet {

    private final String packageName;

    public ValueSet(String packageName) {
        this.packageName = packageName;
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder("ValueSet")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Iterable.class), TypeVariableName.get("E")))
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("E"))
                .addMethod(MethodSpec.methodBuilder("contains")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(TypeName.OBJECT, "o")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("containsAll")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), TypeVariableName.get("?")), "c")
                        .returns(TypeName.BOOLEAN)
                        .build())
                .addMethod(MethodSpec.methodBuilder("size")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
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
                //Object[] toArray()
                .addMethod(MethodSpec.methodBuilder("toArray")
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .returns(Object[].class)
                        .build())
                .addType(new CollectionBuilder(
                                ClassName.get(this.packageName, "ValueSet"),
                                ClassName.get(this.packageName, "ValueSetImpl")
                        ).type()
                )
                .build();
    }
}
