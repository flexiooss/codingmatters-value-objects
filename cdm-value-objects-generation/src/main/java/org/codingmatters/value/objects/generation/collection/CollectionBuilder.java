package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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
                        ParameterizedTypeName.get(ClassName.get(LinkedList.class), TypeVariableName.get("E")),
                        "delegate", Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $T<>()", LinkedList.class)
                        .build())
                .addMethod(MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(valueCollectionInterface, TypeVariableName.get("E")))
                        .addStatement("return new $T<>(this.delegate)", valueCollectionImpl)
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
                        //.addStatement("return builder<E>().with(this.delegate.stream().filter(predicate).collect($T.toList()))", Collectors.class)
                        .addStatement("$T<E> filteredContent = new $T<>(this.delegate.stream().filter(predicate).collect($T.toList()))",
                                LinkedList.class, LinkedList.class, Collectors.class
                        )
                        .addStatement("this.delegate.clear()")
                        .addStatement("this.delegate.addAll(filteredContent)")
                        .addStatement("return this")
                        .build())
                .build();
    }
}
