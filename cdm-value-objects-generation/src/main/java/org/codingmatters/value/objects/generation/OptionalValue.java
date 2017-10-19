package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class OptionalValue {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;
    private final OptionalHelper optionalHelper;

    public OptionalValue(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
        this.optionalHelper = new OptionalHelper();
    }

    public List<TypeSpec> types() {
        List<TypeSpec> result = new LinkedList<>();

        result.add(TypeSpec.classBuilder(this.types.optionalValueType())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(this.staticConstrucor())
                .addMethod(this.construcor())
                .addFields(this.fields())
                .addMethods(this.optionalGetters())
                .addMethods(this.optionalMethods())
                .build()
        );

        return result;
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
                    result.addStatement("this.$L = $T.ofNullable(value != null ? value.$L() : null)",
                            propertySpec.name(),
                            Optional.class,
                            propertySpec.name());
                }
            } else {
                if(propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST) {
                    if (propertySpec.typeSpec().typeKind().isValueObject()) {
                        result.addStatement("this.$L = new $T<>(value != null ? value.$L() : null, e -> $T.of(e))",
                                propertySpec.name(),
                                ClassName.get(this.types.rootPackage() + ".optional", "OptionalValueList"),
                                propertySpec.name(),
                                this.types.propertySingleOptionalType(propertySpec));
                    } else {
                        result.addStatement("this.$L = new $T<>(value != null ? value.$L() : null, e -> $T.ofNullable(e))",
                                propertySpec.name(),
                                ClassName.get(this.types.rootPackage() + ".optional", "OptionalValueList"),
                                propertySpec.name(),
                                Optional.class);
                    }
                } else {
                    result.addStatement("this.$L = new $T<>(value != null ? value.$L() : null)",
                            propertySpec.name(),
                            ClassName.get(this.types.rootPackage() + ".optional", "OptionalValueSet"),
                            propertySpec.name());
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
                            .initializer("this.$L", propertySpec.name())
                            .build());
                }
            } else {
                results.add(FieldSpec.builder(
                        this.types.propertyOptionalType(propertySpec),
                        propertySpec.name())
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build());
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
            } else {
                results.add(MethodSpec.methodBuilder(propertySpec.name())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(this.types.propertyOptionalType(propertySpec))
                        .addStatement("return this.$L", propertySpec.name())
                        .build());
            }
        }

        return results;
    }

    private Iterable<MethodSpec> optionalMethods() {
        ClassName valueType = this.types.valueType();
        List<MethodSpec> results = this.optionalHelper.optionalMethods(valueType);
        return results;
    }

}
