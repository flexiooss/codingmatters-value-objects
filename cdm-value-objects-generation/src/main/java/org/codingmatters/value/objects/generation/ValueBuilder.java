package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

    public ValueBuilder(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;

        this.fields = this.createFields();
        this.setters = this.createSetters();
        this.buildMethod = this.createBuildMethod();
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder("Builder")
                .addModifiers(PUBLIC, STATIC)
//                .addMethod(this.builderMethod)
//                .addMethod(this.builderFromValueMethod)
                .addMethod(this.buildMethod)
                .addFields(this.fields)
                .addMethods(this.setters)
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
        if(propertySpec.typeSpec().typeKind().equals(TypeKind.IN_SPEC_VALUE_OBJECT)) {
            ClassName propertyType = this.types.propertySingleType(propertySpec);
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
        return result;
    }

    private List<MethodSpec> createMultipleSetters(PropertySpec propertySpec) {
        return Arrays.asList(
                MethodSpec.methodBuilder(propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = null", propertySpec.name())
                        .addStatement("return this")
                        .build(),
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
                        .build(),
                MethodSpec.methodBuilder(propertySpec.name())
                        .addParameter(this.types.propertyType(propertySpec), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .build(),
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
