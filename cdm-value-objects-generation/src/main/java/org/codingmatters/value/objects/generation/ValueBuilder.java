package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;
import static org.codingmatters.value.objects.generation.PropertyHelper.propertyType;
import static org.codingmatters.value.objects.generation.SpecCodeGenerator.concat;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueBuilder {

    private final List<FieldSpec> fields;
    private final List<MethodSpec> setters;
    private final MethodSpec builderMethod;
    private final MethodSpec buildMethod;

    public ValueBuilder(String interfaceName, List<PropertySpec> propertySpecs) {
        this.fields = this.createFields(propertySpecs);
        this.setters = this.createSetters(propertySpecs);
        this.builderMethod = this.createBuilderMethod();
        this.buildMethod = this.createBuildMethod(interfaceName, propertySpecs);
    }

    public List<FieldSpec> fields() {
        return fields;
    }

    public List<MethodSpec> setters() {
        return setters;
    }

    public MethodSpec builderMethod() {
        return builderMethod;
    }

    public MethodSpec buildMethod() {
        return buildMethod;
    }

    private List<FieldSpec> createFields(List<PropertySpec> propertySpecs) {
        List<FieldSpec> fields = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            fields.add(
                    FieldSpec.builder(propertyType(propertySpec), propertySpec.name(), PRIVATE).build()
            );
        }
        return fields;
    }


    private List<MethodSpec> createSetters(List<PropertySpec> propertySpecs) {
        List<MethodSpec> setters = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            setters.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .addParameter(propertyType(propertySpec), propertySpec.name())
                            .returns(ClassName.bestGuess("Builder"))
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
                .addStatement("return new $T()", ClassName.bestGuess("Builder"))
                .build();
    }

    private MethodSpec createBuildMethod(String interfaceName, List<PropertySpec> propertySpecs) {
        String constructorParametersFormat = null;
        List<String> constructorParametersNames = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
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
                .returns(ClassName.bestGuess(interfaceName))
                .addStatement(
                        "return new $T(" + constructorParametersFormat + ")",
                        concat(ClassName.bestGuess(interfaceName + "Impl"), constructorParametersNames.toArray()))
                .build();
    }
}
