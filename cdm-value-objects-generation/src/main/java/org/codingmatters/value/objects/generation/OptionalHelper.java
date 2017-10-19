package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalHelper {

    public List<MethodSpec> optionalMethods(TypeName valueType) {
        List<MethodSpec> results = new LinkedList<>();

        results.add(MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(valueType)
                .addStatement("return this.optional.get()")
                .build());
        results.add(MethodSpec.methodBuilder("isPresent")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return this.optional.isPresent()")
                .build());
        results.add(MethodSpec.methodBuilder("ifPresent")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), valueType), "consumer")
                .addStatement("this.optional.ifPresent(consumer)")
                .build());
        results.add(MethodSpec.methodBuilder("filter")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Predicate.class), valueType), "predicate")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), valueType))
                .addStatement("return this.optional.filter(predicate)")
                .build());
        results.add(MethodSpec.methodBuilder("map")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("U"))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Function.class), valueType, TypeVariableName.get("? extends U")), "function")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), TypeVariableName.get("U")))
                .addStatement("return this.optional.map(function)")
                .build());
        results.add(MethodSpec.methodBuilder("flatMap")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("U"))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Function.class), valueType, ParameterizedTypeName.get(ClassName.get(Optional.class), TypeVariableName.get("U"))), "function")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), TypeVariableName.get("U")))
                .addStatement("return this.optional.flatMap(function)")
                .build());
        results.add(MethodSpec.methodBuilder("orElse")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(valueType, "value")
                .returns(valueType)
                .addStatement("return this.optional.orElse(value)")
                .build());
        results.add(MethodSpec.methodBuilder("orElseGet")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Supplier.class), valueType), "supplier")
                .returns(valueType)
                .addStatement("return this.optional.orElseGet(supplier)")
                .build());
        results.add(MethodSpec.methodBuilder("orElseThrow")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("X", ClassName.get(Throwable.class)))
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Supplier.class), WildcardTypeName.subtypeOf(TypeVariableName.get("X"))),
                        "supplier")
                .returns(valueType)
                .addException(TypeVariableName.get("X"))
                .addStatement("return this.optional.orElseThrow(supplier)")
                .build());
        return results;
    }
}
