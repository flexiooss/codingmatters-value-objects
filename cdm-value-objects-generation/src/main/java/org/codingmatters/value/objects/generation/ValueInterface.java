package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static org.codingmatters.value.objects.generation.PropertyHelper.propertyType;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueInterface {

    private final String interfaceName;
    private final List<MethodSpec> getters;
    private final ValueBuilder valueBuilder;

    public ValueInterface(String interfaceName, List<PropertySpec> propertySpecs) {
        this.interfaceName = interfaceName;
        this.getters = this.createGetters(propertySpecs);
        this.valueBuilder = new ValueBuilder(interfaceName, propertySpecs);
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder(this.interfaceName)
                .addModifiers(PUBLIC)
                .addMethods(this.getters)
                .addType(this.valueBuilder.type())
                .build();
    }

    private List<MethodSpec> createGetters(List<PropertySpec> propertySpecs) {
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
}
