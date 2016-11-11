package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.util.ArrayList;

/**
 * Created by nelt on 11/11/16.
 */
public class ValueListImplementation {

    private final String packageName;
    private final TypeSpec valueListType;

    public ValueListImplementation(String packageName, TypeSpec valueListType) {
        this.packageName = packageName;
        this.valueListType = valueListType;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(ClassName.get(this.packageName, "ValueListImpl"))
                .addModifiers()
                .addTypeVariable(TypeVariableName.get("E"))
                .superclass(ParameterizedTypeName.get(ClassName.get(ArrayList.class), TypeVariableName.get("E")))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(this.packageName, "ValueList"), TypeVariableName.get("E")))
                .build();
    }
}
