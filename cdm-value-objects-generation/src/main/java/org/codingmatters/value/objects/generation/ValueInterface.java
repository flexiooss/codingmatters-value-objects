package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.codingmatters.value.objects.generation.PropertyHelper.builderPropertyType;
import static org.codingmatters.value.objects.generation.PropertyHelper.propertyType;
import static org.codingmatters.value.objects.generation.SpecCodeGenerator.capitalizedFirst;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueInterface {
    private final ValueObjectConfiguration types;
    private final List<PropertySpec> propertySpecs;

    private final List<MethodSpec> getters;
    private final List<MethodSpec> withers;
    private final ValueBuilder valueBuilder;

    public ValueInterface(ValueObjectConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;

        this.getters = this.createGetters();
        this.withers = this.createWithers();
        this.valueBuilder = new ValueBuilder(types, propertySpecs);
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder(this.types.valueType())
                .addModifiers(PUBLIC)
                .addMethods(this.getters)
                .addMethods(this.withers)
                .addType(this.valueBuilder.type())
                .build();
    }

    private List<MethodSpec> createGetters() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            result.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .returns(propertyType(propertySpec))
                            .addModifiers(PUBLIC, ABSTRACT)
                            .build()
            );
        }
        return result;
    }

    private List<MethodSpec> createWithers() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            if(propertySpec.typeKind().isValueObject()) {
                result.add(
                        MethodSpec.methodBuilder("with" + capitalizedFirst(propertySpec.name()))
                                .returns(this.types.valueType())
                                .addModifiers(PUBLIC, ABSTRACT)
                                .addParameter(builderPropertyType(propertySpec), "value")
                                .build()
                );
            } else {
                result.add(
                        MethodSpec.methodBuilder("with" + capitalizedFirst(propertySpec.name()))
                                .returns(this.types.valueType())
                                .addModifiers(PUBLIC, ABSTRACT)
                                .addParameter(propertyType(propertySpec), "value")
                                .build()
                );
            }
        }
        return result;
    }
}
