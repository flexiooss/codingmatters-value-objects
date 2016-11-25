package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Created by nelt on 9/22/16.
 */
public class ValueInterface {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    private final List<MethodSpec> getters;
    private final List<MethodSpec> withers;
    private final ValueBuilder valueBuilder;
    private final ValueChanger valueChanger;
    private final MethodSpec changedMethod;
    private final MethodSpec hashCode;

    public ValueInterface(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;

        this.getters = this.createGetters();
        this.withers = this.createWithers();
        this.changedMethod = this.createChangedMethod();
        this.valueBuilder = new ValueBuilder(types, propertySpecs);
        this.valueChanger = new ValueChanger(types);
        this.hashCode = MethodSpec.methodBuilder("hashCode")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(int.class)
                .build();
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder(this.types.valueType())
                .addModifiers(PUBLIC)
                .addMethods(this.getters)
                .addMethods(this.withers)
                .addMethod(this.hashCode)
                .addMethod(this.changedMethod)
                .addType(this.valueBuilder.type())
                .addType(this.valueChanger.type())
                .build();
    }

    private List<MethodSpec> createGetters() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            result.add(this.createGetter(propertySpec));
        }
        return result;
    }

    private List<MethodSpec> createWithers() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            result.add(this.createWhither(propertySpec));
        }
        return result;
    }

    private MethodSpec createGetter(PropertySpec propertySpec) {
        return MethodSpec.methodBuilder(propertySpec.name())
                .returns(this.types.propertyType(propertySpec))
                .addModifiers(PUBLIC, ABSTRACT)
                .build();
    }

    private MethodSpec createWhither(PropertySpec propertySpec) {
        MethodSpec wither;
//        if(propertySpec.typeSpec().typeKind().isValueObject()) {
//            wither = MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
//                    .returns(this.types.valueType())
//                    .addModifiers(PUBLIC, ABSTRACT)
//                    .addParameter(this.types.builderPropertyType(propertySpec), "value")
//                    .build();
//        } else {
//            wither = MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
//                    .returns(this.types.valueType())
//                    .addModifiers(PUBLIC, ABSTRACT)
//                    .addParameter(this.types.propertyType(propertySpec), "value")
//                    .build();
//        }
        wither = MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
                .returns(this.types.valueType())
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(this.types.propertyType(propertySpec), "value")
                .build();
        return wither;
    }

    private MethodSpec createChangedMethod() {
        return MethodSpec.methodBuilder("changed")
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(this.types.valueChangerType(), "changer")
                .returns(this.types.valueType())
                .build();
    }
}
