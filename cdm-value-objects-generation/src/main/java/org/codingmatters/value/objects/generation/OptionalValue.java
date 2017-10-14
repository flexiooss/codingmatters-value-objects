package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalValue {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    public OptionalValue(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(this.types.optionalValueType())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(this.staticConstrucor())
                .addMethod(this.construcor())
                .addFields(this.fields())
                .addMethods(this.optionalGetters())
                .addMethods(this.optionalMethods())
                .build();
    }

    private MethodSpec staticConstrucor() {
        return MethodSpec.methodBuilder("of")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addParameter(this.types.valueType(), "value")
                .returns(this.types.optionalValueType())
                .addStatement("return new $T(value)", this.types.optionalValueType())
                .build();
    }

    private MethodSpec construcor() {
        MethodSpec.Builder result = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(this.types.valueType(), "value")
                .addStatement("this.optional = $T.ofNullable(value)", Optional.class);

        for (PropertySpec propertySpec : this.propertySpecs) {
            if (!propertySpec.typeSpec().cardinality().isCollection()) {
                if (!propertySpec.typeSpec().typeKind().isValueObject()) {
                    result.addStatement("this.$L = $T.ofNullable(value != null ? value.$L() : null)", propertySpec.name(), Optional.class, propertySpec.name());
//                } else {
//                    result.addStatement("this.$L = value != null ? $T.of(value.$L()) : null", propertySpec.name(), this.types.propertyOptionalType(propertySpec), propertySpec.name());
                }
            }
        }

        return result
                .build();
    }

    private Iterable<FieldSpec> fields() {
        List<FieldSpec> results = new LinkedList<>();

        results.add(FieldSpec.builder(
                ParameterizedTypeName.get(ClassName.get(Optional.class), this.types.valueType()),
                "optional")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build());

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(! propertySpec.typeSpec().cardinality().isCollection()) {
                if (!propertySpec.typeSpec().typeKind().isValueObject()) {
                    results.add(FieldSpec.builder(
                            ParameterizedTypeName.get(ClassName.get(Optional.class), this.types.propertyType(propertySpec)),
                            propertySpec.name())
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build());
                } else {
                    results.add(FieldSpec.builder(
                            this.types.propertyOptionalType(propertySpec),
                            propertySpec.name())
                            .addModifiers(Modifier.PRIVATE)
                            .initializer("null")
                            .build());
                }
            }
        }


        return results;
    }

    private Iterable<MethodSpec> optionalGetters() {
        List<MethodSpec> results = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(! propertySpec.typeSpec().cardinality().isCollection()) {
                if (!propertySpec.typeSpec().typeKind().isValueObject()) {
                    results.add(MethodSpec.methodBuilder(propertySpec.name())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), this.types.propertyType(propertySpec)))
                            .addStatement("return this.$L", propertySpec.name())
                            .build());
                } else {
                    results.add(MethodSpec.methodBuilder(propertySpec.name())
                            .addModifiers(Modifier.PUBLIC, Modifier.SYNCHRONIZED)
                            .returns(this.types.propertyOptionalType(propertySpec))
                            .beginControlFlow("if(this.$L == null)", propertySpec.name())
                            .addStatement("this.$L = $T.of(this.optional.isPresent() ? this.optional.get().$L() : null)",
                                    propertySpec.name(),
                                    this.types.propertyOptionalType(propertySpec),
                                    propertySpec.name()
                            )
                            .endControlFlow()
                            .addStatement("return this.$L", propertySpec.name())
                            .build());
                }
            }
        }

        return results;
    }

    private Iterable<MethodSpec> optionalMethods() {
        List<MethodSpec> results = new LinkedList<>();

        results.add(MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC)
                .returns(this.types.valueType())
                .addStatement("return this.optional.get()")
                .build());
        results.add(MethodSpec.methodBuilder("isPresent")
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addStatement("return this.optional.isPresent()")
                .build());
        results.add(MethodSpec.methodBuilder("ifPresent")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), this.types.valueType()), "consumer")
                .addStatement("this.optional.ifPresent(consumer)")
                .build());
        results.add(MethodSpec.methodBuilder("filter")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Predicate.class), this.types.valueType()), "predicate")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), this.types.valueType()))
                .addStatement("return this.optional.filter(predicate)")
                .build());
        results.add(MethodSpec.methodBuilder("map")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("U"))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Function.class), this.types.valueType(), TypeVariableName.get("? extends U")), "function")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), TypeVariableName.get("U")))
                .addStatement("return this.optional.map(function)")
                .build());
        results.add(MethodSpec.methodBuilder("flatMap")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("U"))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Function.class), this.types.valueType(), ParameterizedTypeName.get(ClassName.get(Optional.class), TypeVariableName.get("U"))), "function")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), TypeVariableName.get("U")))
                .addStatement("return this.optional.flatMap(function)")
                .build());
        results.add(MethodSpec.methodBuilder("orElse")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(this.types.valueType(), "value")
                .returns(this.types.valueType())
                .addStatement("return this.optional.orElse(value)")
                .build());
        results.add(MethodSpec.methodBuilder("orElseGet")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Supplier.class), this.types.valueType()), "supplier")
                .returns(this.types.valueType())
                .addStatement("return this.optional.orElseGet(supplier)")
                .build());
        results.add(MethodSpec.methodBuilder("orElseThrow")
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("X", ClassName.get(Throwable.class)))
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Supplier.class), WildcardTypeName.subtypeOf(TypeVariableName.get("X"))),
                        "supplier")
                .returns(this.types.valueType())
                .addException(TypeVariableName.get("X"))
                .addStatement("return this.optional.orElseThrow(supplier)")
                .build());
        return results;
    }
}
