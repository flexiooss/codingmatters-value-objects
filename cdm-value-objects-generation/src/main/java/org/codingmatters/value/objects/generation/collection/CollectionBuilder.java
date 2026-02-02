package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by nelt on 4/3/17.
 */
public class CollectionBuilder {
    private final ClassName valueCollectionInterface;
    private final ClassName valueCollectionImpl;

    public CollectionBuilder(ClassName valueCollectionInterface, ClassName valueCollectionImpl) {
        this.valueCollectionInterface = valueCollectionInterface;
        this.valueCollectionImpl = valueCollectionImpl;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder("Builder")
                .addTypeVariable(TypeVariableName.get("E"))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addField(FieldSpec.builder(
                        ParameterizedTypeName.get(ClassName.get(ArrayList.class), TypeVariableName.get("E")),
                        "delegate", Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $T<>()", ArrayList.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(valueCollectionInterface, TypeVariableName.get("E")))
                        .addStatement("")
                        .addStatement("return new $T<>($T.unmodifiableList(new $T<>(this.delegate)))", valueCollectionImpl, Collections.class, ArrayList.class)
                        .build())

                .addMethod(MethodSpec.methodBuilder("with")
                        .addModifiers(Modifier.PUBLIC)
                        .varargs().addParameter(ArrayTypeName.of(TypeVariableName.get("E")), "elements")
                        .returns(ParameterizedTypeName.get(this.valueCollectionInterface.nestedClass("Builder"), TypeVariableName.get("E")))
                        .addStatement("if(elements != null) {this.delegate.addAll($T.asList(elements));}", Arrays.class)
                        .addStatement("return this")
                        .build())
                .addMethod(MethodSpec.methodBuilder("with")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), TypeVariableName.get("E")), "elements")
                        .returns(ParameterizedTypeName.get(this.valueCollectionInterface.nestedClass("Builder"), TypeVariableName.get("E")))
                        .addStatement("if(elements != null) {elements.forEach(e -> this.delegate.add(e));}")
                        .addStatement("return this")
                        .build())
                .addMethod(MethodSpec.methodBuilder("filtered")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Predicate.class), TypeVariableName.get("E")), "predicate")
                        .returns(ParameterizedTypeName.get(this.valueCollectionInterface.nestedClass("Builder"), TypeVariableName.get("E")))
                        .addStatement("this.delegate.removeIf(predicate.negate())")
                        .addStatement("return this")
                        .build())
                .build();
    }
}
