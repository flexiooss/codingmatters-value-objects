package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;
import static org.codingmatters.value.objects.generation.PropertyHelper.propertyType;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueImplementation {

    private final String packageName;
    private final String interfaceName;
    private final MethodSpec constructor;
    private final List<FieldSpec> fields;
    private final List<MethodSpec> getters;

    public ValueImplementation(String packageName, String interfaceName, List<PropertySpec> propertySpecs) {
        this.packageName = packageName;
        this.interfaceName = interfaceName;
        this.constructor = this.createConstructor(propertySpecs);
        this.fields = this.createFields(propertySpecs);
        this.getters = this.createGetters(propertySpecs);
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(interfaceName + "Impl")
                .addSuperinterface(ClassName.get(this.packageName, interfaceName))
                .addModifiers(PUBLIC)
                .addMethod(this.constructor)
                .addMethods(this.getters)
                .addFields(this.fields)
                .build();
    }

    private MethodSpec createConstructor(List<PropertySpec> propertySpecs) {
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        for (PropertySpec propertySpec : propertySpecs) {
            constructorBuilder
                    .addParameter(propertyType(propertySpec), propertySpec.name())
                    .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
            ;
        }

        return constructorBuilder.build();
    }

    private List<FieldSpec> createFields(List<PropertySpec> propertySpecs) {
        List<FieldSpec> fields = new LinkedList<>();
        for (PropertySpec propertySpec : propertySpecs) {
            fields.add(
                    FieldSpec.builder(propertyType(propertySpec), propertySpec.name(), PRIVATE, FINAL).build()
            );
        }
        return fields;
    }

    private List<MethodSpec> createGetters(List<PropertySpec> propertySpecs) {
        List<MethodSpec> getters = new LinkedList<>();
        for (PropertySpec propertySpec : propertySpecs) {
            getters.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .returns(propertyType(propertySpec))
                            .addModifiers(PUBLIC)
                            .addStatement("return this.$N", propertySpec.name())
                            .build()
            );
        }
        return getters;
    }
}
