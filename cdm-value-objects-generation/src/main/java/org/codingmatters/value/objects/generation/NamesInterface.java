package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.util.LinkedList;
import java.util.List;

public class NamesInterface {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    public NamesInterface(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder(this.types.namesType())
                .addModifiers(Modifier.PUBLIC)
                .addMethods(this.propertyNameMethods())
                .addField(this.instanceField())
                .build();
    }

    private FieldSpec instanceField() {
        return FieldSpec.builder(this.types.namesType(), "INSTANCE", Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .initializer("new $T() {}", this.types.namesType())
                .build();
    }

    private List<MethodSpec> propertyNameMethods() {
        List<MethodSpec> results = new LinkedList<>();
        for (PropertySpec propertySpec : this.propertySpecs) {
            if(propertySpec.typeSpec().typeKind().isValueObject()) {
                ClassName valueObjectNamesType = this.types.valueObjectNamesType(propertySpec);
                results.add(MethodSpec.methodBuilder(propertySpec.name() + "Names")
                        .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                        .returns(valueObjectNamesType)
                        .addStatement("return $T.INSTANCE", valueObjectNamesType)
                        .build());

            }
            results.add(MethodSpec.methodBuilder(propertySpec.name())
                    .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                    .returns(String.class)
                    .addStatement("return $S", this.types.fieldName(propertySpec))
                    .build());
        }
        return results;
    }
}
