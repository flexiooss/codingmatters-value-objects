package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueBuilder {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    private final List<FieldSpec> fields;
    private final List<MethodSpec> setters;
    private final MethodSpec builderMethod;
    private final MethodSpec builderFromValueMethod;
    private final MethodSpec buildMethod;

    public ValueBuilder(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;

        this.fields = this.createFields();
        this.setters = this.createSetters();
        this.builderMethod = this.createBuilderMethod();
        this.builderFromValueMethod = this.createBuilderFromValueMethod();
        this.buildMethod = this.createBuildMethod();
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder("Builder")
                .addModifiers(PUBLIC, STATIC)
                .addMethod(this.builderMethod)
                .addMethod(this.builderFromValueMethod)
                .addMethod(this.buildMethod)
                .addFields(this.fields)
                .addMethods(this.setters)
                .build();
    }

    private List<FieldSpec> createFields() {
        List<FieldSpec> fields = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
//            fields.add(FieldSpec.builder(this.types.builderPropertyType(propertySpec), propertySpec.name(), PRIVATE).build());
            fields.add(FieldSpec.builder(this.types.propertyType(propertySpec), propertySpec.name(), PRIVATE).build());
        }
        return fields;
    }

    private List<MethodSpec> createSetters() {
        List<MethodSpec> setters = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SINGLE)) {
                setters.add(this.createSingleSetter(propertySpec));
            } else {
                setters.addAll(this.createMultipleSetter(propertySpec));
            }
        }
        return setters;
    }

    private MethodSpec createSingleSetter(PropertySpec propertySpec) {
        return MethodSpec.methodBuilder(propertySpec.name())
                .addParameter(this.types.propertyType(propertySpec), propertySpec.name())
                .returns(this.types.valueBuilderType())
                .addModifiers(PUBLIC)
                .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
                .addStatement("return this")
                .build();
    }

    private List<MethodSpec> createMultipleSetter(PropertySpec propertySpec) {
        return Arrays.asList(
                MethodSpec.methodBuilder(propertySpec.name())
                        .varargs().addParameter(ArrayTypeName.of(this.types.propertySingleType(propertySpec)), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = new $T($N)",
                                propertySpec.name(),
                                this.types.propertyImplType(propertySpec),
                                propertySpec.name()
                        )
                        .addStatement("return this")
                        .build(),
                MethodSpec.methodBuilder(propertySpec.name())
                        .addParameter(this.types.builderPropertyType(propertySpec), propertySpec.name())
                        .returns(this.types.valueBuilderType())
                        .addModifiers(PUBLIC)
                        .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
                        .addStatement("return this")
                        .build()
        );
    }

    private MethodSpec createBuilderMethod() {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(STATIC, PUBLIC)
                .returns(ClassName.bestGuess("Builder"))
                .addStatement("return new $T()", this.types.valueBuilderType())
                .build();
    }

    private MethodSpec createBuilderFromValueMethod() {
        List<Object> bindings = new LinkedList<>();

        String statement = "return new $T()\n";
        bindings.add(this.types.valueBuilderType());

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SINGLE)) {
//                if (propertySpec.typeSpec().typeKind().isValueObject()) {
//                    statement += "." + propertySpec.name() + "($T.from(value." + propertySpec.name() + "()))\n";
//                    bindings.add(this.types.builderPropertyType(propertySpec));
//                } else {
//                    statement += "." + propertySpec.name() + "(value." + propertySpec.name() + "())\n";
//                }
                statement += "." + propertySpec.name() + "(value." + propertySpec.name() + "())\n";
            } else {

            }
        }

        return MethodSpec.methodBuilder("from")
                .addModifiers(STATIC, PUBLIC)
                .addParameter(this.types.valueType(), "value")
                .returns(this.types.valueBuilderType())
                .beginControlFlow("if(value != null)")
                .addStatement(statement, bindings.toArray())
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("return null")
                .endControlFlow()
                .build();
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


//            if(propertySpec.typeSpec().typeKind().isValueObject()) {
//                constructorParametersFormat += "this.$N != null ? this.$N.build() : null";
//                constructorParametersNames.add(propertySpec.name());
//                constructorParametersNames.add(propertySpec.name());
//            } else {
//                constructorParametersFormat += "this.$N";
//                constructorParametersNames.add(propertySpec.name());
//            }
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
        for(int i = 1 ; i < result.length ; i++) {
            result[i] = others[i-1];
        }
        return result;
    }
}
