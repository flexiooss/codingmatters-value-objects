package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private final MethodSpec toMapMethod;
    private final MethodSpec hashCode;
    private final List<TypeSpec> enums;
    private List<ClassName> protocols;

    public ValueInterface(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;

        this.getters = this.createGetters();
        this.withers = this.createWithers();
        this.changedMethod = this.createChangedMethod();
        this.toMapMethod = this.createToMapMethod();
        this.valueBuilder = new ValueBuilder(types, propertySpecs);
        this.valueChanger = new ValueChanger(types);
        this.hashCode = MethodSpec.methodBuilder("hashCode")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(int.class)
                .build();

        this.enums = this.createEnums();
        this.protocols = this.createProtocols();
    }

    public TypeSpec type() {
        return TypeSpec.interfaceBuilder(this.types.valueType())
                .addMethod(this.createBuilderMethod())
                .addMethod(this.createBuilderFromValueMethod())
                .addMethod(this.createBuilderFromMapMethod())
                .addModifiers(PUBLIC)
                .addTypes(this.enums)
                .addMethods(this.getters)
                .addMethods(this.withers)
                .addMethod(this.hashCode)
                .addMethod(this.changedMethod)
                .addMethod(this.toMapMethod)
                .addMethod(this.optMethod())
                .addType(this.valueBuilder.type())
                .addType(this.valueChanger.type())
                .addSuperinterfaces(this.protocols)
                .build();
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
            statement += "." + propertySpec.name() + "(value." + propertySpec.name() + "())\n";
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


    private MethodSpec createBuilderFromMapMethod() {
        return MethodSpec.methodBuilder("fromMap")
                .addModifiers(STATIC, PUBLIC)
                .addParameter(Map.class, "value")
                .returns(this.types.valueBuilderType())
                .addCode(new FromMapBuilderMethod(this.types).block())
                .build();
    }

    private List<MethodSpec> createGetters() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            result.add(this.createGetter(propertySpec));
        }
        return result;
    }

    private MethodSpec createGetter(PropertySpec propertySpec) {
        return MethodSpec.methodBuilder(propertySpec.name())
                .returns(this.types.propertyType(propertySpec))
                .addModifiers(PUBLIC, ABSTRACT)
                .build();
    }

    private List<MethodSpec> createWithers() {
        List<MethodSpec> result = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            result.addAll(this.createDefaultWhithers(propertySpec));
            if(propertySpec.typeSpec().typeKind().isValueObject()) {
                result.addAll(this.createChangedWithers(propertySpec));
            }
            if(propertySpec.typeSpec().cardinality().isCollection()) {
                result.addAll(this.createIterableWithers(propertySpec));
            }
        }
        return result;
    }

    private List<MethodSpec> createDefaultWhithers(PropertySpec propertySpec) {
        List<MethodSpec> results = new LinkedList<>();
        results.add(MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
                .returns(this.types.valueType())
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(this.types.propertyType(propertySpec), "value")
                .build()
        );
        return results;
    }

    private List<MethodSpec> createIterableWithers(PropertySpec propertySpec) {
        List<MethodSpec> results = new LinkedList<>();
        results.add(MethodSpec.methodBuilder(this.types.witherMethodName(propertySpec))
                .returns(this.types.valueType())
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), this.types.propertySingleType(propertySpec)), "values")
                .build()
        );
        return results;
    }

    private List<MethodSpec> createChangedWithers(PropertySpec propertySpec) {
        List<MethodSpec> results = new LinkedList<>();
        if (!propertySpec.typeSpec().cardinality().isCollection()) {
            results.add(MethodSpec.methodBuilder(this.types.changedWitherMethodName(propertySpec))
                    .returns(this.types.valueType())
                    .addModifiers(PUBLIC, ABSTRACT)
                    .addParameter(this.types.valueObjectSingleType(propertySpec).nestedClass("Changer"), "changer")
                    .build()
            );
        } else {
            TypeName type = this.types.propertySingleType(propertySpec);
            ParameterizedTypeName changerType =
                    propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST) ?
                        this.types.collectionConfiguration().valueListOfTypeChanger(type) :
                            this.types.collectionConfiguration().valueSetOfTypeChanger(type);

            results.add(MethodSpec.methodBuilder(this.types.changedWitherMethodName(propertySpec))
                    .returns(this.types.valueType())
                    .addModifiers(PUBLIC, ABSTRACT)
                    .addParameter(changerType, "changer")
                    .build()
            );
        }
        return results;
    }

    private MethodSpec createChangedMethod() {
        return MethodSpec.methodBuilder("changed")
                .addModifiers(PUBLIC, ABSTRACT)
                .addParameter(this.types.valueChangerType(), "changer")
                .returns(this.types.valueType())
                .build();
    }

    private MethodSpec createToMapMethod() {
        return MethodSpec.methodBuilder("toMap")
                .addModifiers(PUBLIC, ABSTRACT)
                .returns(Map.class)
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

    private MethodSpec optMethod() {
        return MethodSpec.methodBuilder("opt")
                .addModifiers(ABSTRACT, PUBLIC)
                .returns(this.types.optionalValueType())
                .build();
    }



    private List<ClassName> createProtocols() {
        List<ClassName> result = new LinkedList<>();

        for (String protocol : this.types.valueSpec().protocols()) {
            result.add(ClassName.bestGuess(protocol));
        }

        return result;
    }

}
