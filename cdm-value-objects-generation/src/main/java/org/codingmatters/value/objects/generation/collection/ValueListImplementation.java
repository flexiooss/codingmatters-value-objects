package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

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
                .addMethod(MethodSpec.methodBuilder("stream")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), TypeVariableName.get("E")))
                        .addStatement("return super.stream()")
                        .build())
                .build();
    }
}
