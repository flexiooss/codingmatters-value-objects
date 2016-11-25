package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by nelt on 11/19/16.
 */
public class ValueSetImplementation {

    private final String packageName;
    private final TypeSpec valueSetType;

    public ValueSetImplementation(String packageName, TypeSpec valueSetType) {
        this.packageName = packageName;
        this.valueSetType = valueSetType;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(ClassName.get(this.packageName, "ValueSetImpl"))
                .addModifiers()
                .addTypeVariable(TypeVariableName.get("E"))
                .superclass(ParameterizedTypeName.get(ClassName.get(HashSet.class), TypeVariableName.get("E")))
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(this.packageName, "ValueSet"), TypeVariableName.get("E")))
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                                .addMember("value", "$S", "unchecked")
                                .build())
                        .varargs().addParameter(ArrayTypeName.of(TypeVariableName.get("E")), "elements")
                        .addStatement("super($T.asList($N))", Arrays.class, "elements")
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class)
                                .addMember("value", "$S", "unchecked")
                                .build())
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), TypeVariableName.get("E")), "elements")
                        .addStatement("super($N)", "elements")
                        .build())
                .build();
    }
}
