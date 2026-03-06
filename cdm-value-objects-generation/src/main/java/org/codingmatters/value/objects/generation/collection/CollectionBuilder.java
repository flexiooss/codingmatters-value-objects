package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;

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
                        .addStatement("return new $T<>($T.unmodifiableList(this.delegate))", valueCollectionImpl, Collections.class)
                        .build())

                .addMethod(MethodSpec.methodBuilder("with")
                        .addModifiers(Modifier.PUBLIC)
                        .varargs().addParameter(ArrayTypeName.of(TypeVariableName.get("E")), "elements")
                        .returns(ParameterizedTypeName.get(this.valueCollectionInterface.nestedClass("Builder"), TypeVariableName.get("E")))
                        .beginControlFlow("if (elements != null)")
                        .addStatement("$T.addAll(this.delegate, elements)", Collections.class)
                        .endControlFlow()
                        .addStatement("return this")
                        .build())
                .addMethod(MethodSpec.methodBuilder("with")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), TypeVariableName.get("E")), "elements")
                        .returns(ParameterizedTypeName.get(this.valueCollectionInterface.nestedClass("Builder"), TypeVariableName.get("E")))
                        .beginControlFlow("if (elements != null)")
                        .addStatement("elements.forEach(e -> this.delegate.add(e))")
                        .endControlFlow()
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
