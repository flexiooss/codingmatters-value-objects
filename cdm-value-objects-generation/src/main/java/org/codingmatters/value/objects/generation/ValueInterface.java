package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;

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
    private final List<TypeSpec> enums;

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

        this.enums = this.createEnums();
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder(this.types.valueType())
                .addModifiers(PUBLIC)
                .addTypes(this.enums)
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
        return MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
                .returns(this.types.valueType())
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(this.types.propertyType(propertySpec), "value")
                .build();
    }

    private MethodSpec createChangedMethod() {
        return MethodSpec.methodBuilder("changed")
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(this.types.valueChangerType(), "changer")
                .returns(this.types.valueType())
                .build();
    }

    private List<TypeSpec> createEnums() {
        List<TypeSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            if(TypeKind.ENUM.equals(propertySpec.typeSpec().typeKind()) && propertySpec.typeSpec().isInSpecEnum()) {
                result.add(this.createEnum(propertySpec));
            }
        }
        return result;
    }

    private TypeSpec createEnum(PropertySpec propertySpec) {
        TypeSpec.Builder result = TypeSpec.enumBuilder(this.types.enumTypeName(propertySpec.name()))
                .addModifiers(STATIC, PUBLIC)
                ;
        for (String value : propertySpec.typeSpec().enumValues()) {
            result.addEnumConstant(value);
        }

        return result.build();
    }
}
