package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;
import static org.codingmatters.value.objects.generation.PropertyHelper.builderPropertyType;
import static org.codingmatters.value.objects.generation.SpecCodeGenerator.concat;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueBuilder {
    private final ClassName builderType;
    private final List<PropertySpec> propertySpecs;
    private ClassName valueType;
    private ClassName valueImplType;

    private final List<FieldSpec> fields;
    private final List<MethodSpec> setters;
    private final MethodSpec builderMethod;
    private final MethodSpec builderFromValueMethod;
    private final MethodSpec buildMethod;

    public ValueBuilder(String packageName, String interfaceName, List<PropertySpec> propertySpecs) {
        this.valueType = ClassName.get(packageName, interfaceName);
        this.valueImplType = ClassName.get(packageName, interfaceName + "Impl");
        this.builderType = ClassName.get(packageName, interfaceName + ".Builder");
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
            fields.add(FieldSpec.builder(builderPropertyType(propertySpec), propertySpec.name(), PRIVATE).build());
        }
        return fields;
    }

    private List<MethodSpec> createSetters() {
        List<MethodSpec> setters = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            setters.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .addParameter(builderPropertyType(propertySpec), propertySpec.name())
                            .returns(this.builderType)
                            .addModifiers(PUBLIC)
                            .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
                            .addStatement("return this")
                            .build()
            );
        }
        return setters;
    }

    private MethodSpec createBuilderMethod() {
        return MethodSpec.methodBuilder("builder")
                .addModifiers(STATIC, PUBLIC)
                .returns(ClassName.bestGuess("Builder"))
                .addStatement("return new $T()", this.builderType)
                .build();
    }

    private MethodSpec createBuilderFromValueMethod() {
        List<Object> bindings = new LinkedList<>();

        String statement = "return new $T()\n";
        bindings.add(this.builderType);

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(propertySpec.typeKind().isValueObject()) {
                statement += "." + propertySpec.name() + "($T.from(value." + propertySpec.name() + "()))\n";
                bindings.add(builderPropertyType(propertySpec));
            } else {
                statement += "." + propertySpec.name() + "(value." + propertySpec.name() + "())\n";
            }
        }

        return MethodSpec.methodBuilder("from")
                .addModifiers(STATIC, PUBLIC)
                .addParameter(this.valueType, "value")
                .returns(this.builderType)
                .addStatement(statement, bindings.toArray())
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


            if(propertySpec.typeKind().isValueObject()) {
                constructorParametersFormat += "this.$N != null ? this.$N.build() : null";
                constructorParametersNames.add(propertySpec.name());
                constructorParametersNames.add(propertySpec.name());
            } else {
                constructorParametersFormat += "this.$N";
                constructorParametersNames.add(propertySpec.name());
            }
        }
        if(constructorParametersFormat == null) {
            constructorParametersFormat = "";
        }
        return MethodSpec.methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(this.valueType)
                .addStatement(
                        "return new $T(" + constructorParametersFormat + ")",
                        concat(this.valueImplType, constructorParametersNames.toArray()))
                .build();
    }
}
