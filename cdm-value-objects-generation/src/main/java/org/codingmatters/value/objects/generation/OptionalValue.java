package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
                .addMethod(this.construcor())
                .addFields(this.fields())
                .addMethods(this.optionalGetters())
                .build();
    }

    private MethodSpec construcor() {
        MethodSpec.Builder result = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(this.types.valueType(), "value")
                .addStatement("this.optional = $T.ofNullable(value)", Optional.class);

        for (PropertySpec propertySpec : this.propertySpecs) {
            if (!propertySpec.typeSpec().typeKind().isValueObject()) {
                if (!propertySpec.typeSpec().cardinality().isCollection()) {
                    result.addStatement("this.$L = $T.ofNullable(value.$L())", propertySpec.name(), Optional.class, propertySpec.name());
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
            if(! propertySpec.typeSpec().typeKind().isValueObject()) {
                if(! propertySpec.typeSpec().cardinality().isCollection()) {
                    results.add(FieldSpec.builder(
                            ParameterizedTypeName.get(ClassName.get(Optional.class), this.types.propertyType(propertySpec)),
                            propertySpec.name())
                            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                            .build());
                }
            }
        }


        return results;
    }

    private Iterable<MethodSpec> optionalGetters() {
        List<MethodSpec> results = new LinkedList<>();

        for (PropertySpec propertySpec : this.propertySpecs) {
            if(! propertySpec.typeSpec().typeKind().isValueObject()) {
                if(! propertySpec.typeSpec().cardinality().isCollection()) {
                    results.add(MethodSpec.methodBuilder(propertySpec.name())
                            .addModifiers(Modifier.PUBLIC)
                            .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), this.types.propertyType(propertySpec)))
                            .addStatement("return this.$L", propertySpec.name())
                            .build());
                }
            }
        }

        return results;
    }
}
