package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.*;
import java.util.function.Consumer;

import static javax.lang.model.element.Modifier.*;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueBuilder {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    private final List<FieldSpec> fields;
    private final List<MethodSpec> setters;
    private final MethodSpec buildMethod;
    private final List<TypeName> protocols;

    public ValueBuilder(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
        this.protocols = this.createProtocols();

        this.fields = this.createFields();
        this.setters = this.createSetters();
        this.buildMethod = this.createBuildMethod();
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder("Builder")
                .addModifiers(PUBLIC, STATIC)
                .addMethod(this.buildMethod)
                .addFields(this.fields)
                .addMethods(this.setters)
                .addSuperinterfaces(this.protocols)
                .build();
    }

    private List<FieldSpec> createFields() {
        List<FieldSpec> fields = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            fields.add(FieldSpec.builder(this.types.propertyType(propertySpec), propertySpec.name(), PRIVATE).build());
        }
        return fields;
    }

    private List<MethodSpec> createSetters() {
        List<MethodSpec> setters = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SINGLE)) {
                setters.addAll(this.createSingleSetter(propertySpec));
            } else {
                setters.addAll(this.createMultipleSetters(propertySpec));
                setters.addAll(this.createMultipleAdders(propertySpec));
            }
        }
        return setters;
    }

    private List<MethodSpec> createSingleSetter(PropertySpec propertySpec) {
        LinkedList<MethodSpec> result = new LinkedList<>();
        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                    .addParameter(this.types.propertyType(propertySpec), propertySpec.name())
                    .returns(this.types.valueBuilderType())
                    .addModifiers(PUBLIC)
                    .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
                    .addStatement("return this")
                    .build()
        );
        if(propertySpec.typeSpec().typeKind().isValueObject()) {
            ClassName propertyType = this.types.valueObjectSingleType(propertySpec);
            result.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .addParameter(
                                    ParameterizedTypeName.get(ClassName.get(Consumer.class), propertyType.nestedClass("Builder")),
                                    propertySpec.name()
                            )
                            .returns(this.types.valueBuilderType())
                            .addModifiers(PUBLIC)
                            .addStatement("$T.Builder builder = $T.builder()", propertyType, propertyType)
                            .addStatement("$N.accept(builder)", propertySpec.name())
                            .addStatement("return this.$N(builder.build())", propertySpec.name())
                            .build()
            );
        }
        if(propertySpec.typeSpec().typeKind().equals(TypeKind.JAVA_TYPE) && propertySpec.typeSpec().typeRef().equals(String.class.getName())) {
            this.addFormattedStringSetters(propertySpec, result);
        }
        return result;
    }

    private void addFormattedStringSetters(PropertySpec propertySpec, LinkedList<MethodSpec> result) {
        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                        .addParameter(this.types.propertyType(propertySpec), "format")
                        .varargs().addParameter(Object[].class, "args")
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $T.format(format, args)", propertySpec.name(), String.class)
                        .addStatement("return this")
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                        .addParameter(Locale.class, "locale")
                        .addParameter(this.types.propertyType(propertySpec), "format")
                        .varargs().addParameter(Object[].class, "args")
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $T.format(locale, format, args)", propertySpec.name(), String.class)
                        .addStatement("return this")
                        .build()
        );
    }

    private List<MethodSpec> createMultipleSetters(PropertySpec propertySpec) {
        List<MethodSpec> result = new LinkedList<>();

        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = null", propertySpec.name())
                        .addStatement("return this")
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                        .varargs().addParameter(ArrayTypeName.of(this.types.propertySingleType(propertySpec)), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $N != null ? new $T.Builder<$T>().with($N).build() : null",
                                propertySpec.name(),
                                propertySpec.name(),
                                propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ?
                                        this.types.collectionConfiguration().valueListType() :
                                        this.types.collectionConfiguration().valueSetType()
                                ,
                                this.types.propertySingleType(propertySpec),
                                propertySpec.name()
                        )
                        .addStatement("return this")
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                        .addParameter(this.types.propertyType(propertySpec), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name())
                        .addParameter(ParameterizedTypeName.get(
                                ClassName.get(Collection.class),
                                this.types.propertySingleType(propertySpec)), propertySpec.name()
                        )
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $N != null ? new $T.Builder<$T>().with($N).build() : null",
                                propertySpec.name(),
                                propertySpec.name(),
                                propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ?
                                        this.types.collectionConfiguration().valueListType() :
                                        this.types.collectionConfiguration().valueSetType(),
                                this.types.propertySingleType(propertySpec),
                                propertySpec.name()
                        )
                        .addStatement("return this")
                        .build()
        );

        if(propertySpec.typeSpec().typeKind().isValueObject()) {
            ClassName propertyType = this.types.valueObjectSingleType(propertySpec);
            String varargParameterName = propertySpec.name() + "Elements";
            result.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .varargs().addParameter(
                                    ArrayTypeName.of(ParameterizedTypeName.get(ClassName.get(Consumer.class), propertyType.nestedClass("Builder"))),
                            varargParameterName
                            )
                            .returns(this.types.valueBuilderType())
                            .addModifiers(PUBLIC)
                            .beginControlFlow("if($N != null)", varargParameterName)
                                .addStatement("$T elements = new $T()",
                                        ParameterizedTypeName.get(ClassName.get(LinkedList.class), propertyType),
                                        ParameterizedTypeName.get(ClassName.get(LinkedList.class), propertyType))
                                .beginControlFlow("for($T $N : $N)",
                                        ParameterizedTypeName.get(ClassName.get(Consumer.class), propertyType.nestedClass("Builder")),
                                        propertySpec.name(),
                                        varargParameterName)
                                    .addStatement("$T.Builder builder = $T.builder()", propertyType, propertyType)
                                    .addStatement("$N.accept(builder)", propertySpec.name())
                                    .addStatement("elements.add(builder.build())", propertySpec.name())
                                .endControlFlow()
                            .addStatement("this.$N(elements.toArray(new $T[elements.size()]))", propertySpec.name(), propertyType)
                            .endControlFlow()
                            .addStatement("return this")
                            .build()
            );
        }

        return result;
    }



    private List<MethodSpec> createMultipleAdders(PropertySpec propertySpec) {
        List<MethodSpec> result = new LinkedList<>();

        result.add(
                MethodSpec.methodBuilder(propertySpec.name() + "Add")
                        .addParameter(this.types.propertySingleType(propertySpec), "last")
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .beginControlFlow("if(this.$N == null)", propertySpec.name())
                        .addStatement("return this.$N(last)", propertySpec.name())
                        .nextControlFlow("else")
                        .addStatement("this.$N = this.$N.toBuilder().with(last).build()", propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .endControlFlow()
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name() + "AddIf")
                        .addParameter(Boolean.class, "condition")
                        .addParameter(this.types.propertySingleType(propertySpec), "last")
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .beginControlFlow("if(! condition)")
                        .addStatement("return this")
                        .endControlFlow()
                        .beginControlFlow("if(this.$N == null)", propertySpec.name())
                        .addStatement("return this.$N(last)", propertySpec.name())
                        .nextControlFlow("else")
                        .addStatement("this.$N = this.$N.toBuilder().with(last).build()", propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .endControlFlow()
                        .build()
        );
        if(! propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SET)) {
            result.add(
                    MethodSpec.methodBuilder(propertySpec.name() + "AddFirst")
                            .addParameter(this.types.propertySingleType(propertySpec), "first")
                            .returns(this.types.valueBuilderType())
                            .addModifiers(PUBLIC)
                            .beginControlFlow("if(this.$N == null)", propertySpec.name())
                            .addStatement("return this.$N(first)", propertySpec.name())
                            .nextControlFlow("else")
                            .addStatement("this.$N = new $T.Builder<$T>().with(first).with(this.$N).build()",
                                    propertySpec.name(),
                                    propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ?
                                            this.types.collectionConfiguration().valueListType() :
                                            this.types.collectionConfiguration().valueSetType(),
                                    this.types.propertySingleType(propertySpec),
                                    propertySpec.name()
                            )
                            .addStatement("return this")
                            .endControlFlow()
                            .build()
            );
            result.add(
                    MethodSpec.methodBuilder(propertySpec.name() + "AddFirstIf")
                            .addParameter(Boolean.class, "condition")
                            .addParameter(this.types.propertySingleType(propertySpec), "first")
                            .returns(this.types.valueBuilderType())
                            .addModifiers(PUBLIC)
                            .beginControlFlow("if(! condition)")
                            .addStatement("return this")
                            .endControlFlow()
                            .beginControlFlow("if(this.$N == null)", propertySpec.name())
                            .addStatement("return this.$N(first)", propertySpec.name())
                            .nextControlFlow("else")
                            .addStatement("this.$N = new $T.Builder<$T>().with(first).with(this.$N).build()",
                                    propertySpec.name(),
                                    propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ?
                                            this.types.collectionConfiguration().valueListType() :
                                            this.types.collectionConfiguration().valueSetType(),
                                    this.types.propertySingleType(propertySpec),
                                    propertySpec.name()
                            )
                            .addStatement("return this")
                            .endControlFlow()
                            .build()
            );
        }

        result.add(
                MethodSpec.methodBuilder(propertySpec.name() + "AddAll")
                        .addParameter(this.types.propertyType(propertySpec), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .beginControlFlow("if(this.$N == null)", propertySpec.name())
                        .addStatement("return this.$N($N)", propertySpec.name(), propertySpec.name())
                        .nextControlFlow("else")
                        .addStatement("this.$N = this.$N.toBuilder().with($N).build()", propertySpec.name(), propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .endControlFlow()
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name() + "AddAll")
                        .varargs().addParameter(ArrayTypeName.of(this.types.propertySingleType(propertySpec)), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .beginControlFlow("if(this.$N == null)", propertySpec.name())
                        .addStatement("return this.$N($N)", propertySpec.name(), propertySpec.name())
                        .nextControlFlow("else")
                        .addStatement("this.$N = this.$N.toBuilder().with($N).build()", propertySpec.name(), propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .endControlFlow()
                        .build()
        );
        result.add(
                MethodSpec.methodBuilder(propertySpec.name() + "AddAll")
                        .addParameter(ParameterizedTypeName.get(
                                ClassName.get(Collection.class),
                                this.types.propertySingleType(propertySpec)), propertySpec.name()
                        )
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .beginControlFlow("if(this.$N == null)", propertySpec.name())
                        .addStatement("return this.$N($N)", propertySpec.name(), propertySpec.name())
                        .nextControlFlow("else")
                        .addStatement("this.$N = this.$N.toBuilder().with($N).build()", propertySpec.name(), propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .endControlFlow()
                        .build()
        );

        if(propertySpec.typeSpec().typeKind().isValueObject()) {
            ClassName propertyType = this.types.valueObjectSingleType(propertySpec);
            String varargParameterName = propertySpec.name() + "Elements";
            result.add(
                    MethodSpec.methodBuilder(propertySpec.name() + "AddAll")
                            .varargs().addParameter(
                                    ArrayTypeName.of(ParameterizedTypeName.get(ClassName.get(Consumer.class), propertyType.nestedClass("Builder"))),
                                    varargParameterName
                            )
                            .returns(this.types.valueBuilderType())
                            .addModifiers(PUBLIC)
                            .beginControlFlow("if(this.$N == null)", propertySpec.name())
                            .addStatement("return this.$N($N)", propertySpec.name(), propertySpec.name())
                            .nextControlFlow("else if($L != null)", varargParameterName)
                            .addStatement("this.$N = this.$N.toBuilder().with(($T<$T>) $T.stream($L).map(" +
                                            "consumer -> {" +
                                            "$T.Builder builder = $T.builder();" +
                                            "consumer.accept(builder);" +
                                            "return builder.build();" +
                                            "}).toList()).build()",
                                    propertySpec.name(), propertySpec.name(),
                                    List.class, propertyType,
                                    Arrays.class, varargParameterName,
                                    propertyType, propertyType
                            )
                            .addStatement("return this")
                            .nextControlFlow("else")
                            .addStatement("return this")
                            .endControlFlow()
                            .build()
            );
        }

        return result;
    }


    private MethodSpec createBuildMethod() {
        String constructorParametersFormat = null;
        List<String> constructorParametersNames = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(constructorParametersFormat == null) {
                constructorParametersFormat = "";
            } else {
                constructorParametersFormat += ", ";
            }
            constructorParametersFormat += "this.$N";
            constructorParametersNames.add(propertySpec.name());
        }
        if(constructorParametersFormat == null) {
            constructorParametersFormat = "";
        }
        return MethodSpec.methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(this.types.valueType())
                .addStatement(
                        "return new $T(" + constructorParametersFormat + ")",
                        concat(this.types.valueImplType(), constructorParametersNames.toArray()))
                .build();
    }

    private List<TypeName> createProtocols() {
        List<TypeName> result = new LinkedList<>();

        for (String protocol : this.types.valueSpec().builderProtocols()) {
            result.add(ClassName.bestGuess(protocol));
        }
        for (String protocol : this.types.valueSpec().parametrizedBuilderProtocols()) {
            result.add(ParameterizedTypeName.get(ClassName.bestGuess(protocol), ClassName.bestGuess("Builder")));
//            result.add(ClassName.bestGuess(protocol));
        }

        return result;
    }

    static private Object [] concat(Object first, Object ... others) {
        int size = 1;
        if(others != null) {
            size += others.length;
        }
        Object[] result = new Object[size];
        result[0] = first;
        System.arraycopy(others, 0, result, 1, result.length - 1);
        return result;
    }
}
