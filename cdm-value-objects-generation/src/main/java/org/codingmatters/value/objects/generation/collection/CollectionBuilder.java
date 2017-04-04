package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

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
                        .returns(this.valueCollectionInterface.nestedClass("Builder"))
                        .addStatement("if(elements != null) {this.delegate.addAll($T.asList(elements));}", Arrays.class)
                        .addStatement("return this")
                        .build())
                .addMethod(MethodSpec.methodBuilder("with")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), TypeVariableName.get("E")), "elements")
                        .returns(this.valueCollectionInterface.nestedClass("Builder"))
                        .addStatement("if(elements != null) {this.delegate.addAll(elements);}")
                        .addStatement("return this")
                        .build())
                .build();
    }
}
