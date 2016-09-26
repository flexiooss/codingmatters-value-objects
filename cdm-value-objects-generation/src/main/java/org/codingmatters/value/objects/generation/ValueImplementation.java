package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
    private MethodSpec equalsMethod;

    public ValueImplementation(String packageName, String interfaceName, List<PropertySpec> propertySpecs) {
        this.packageName = packageName;
        this.interfaceName = interfaceName;
        this.constructor = this.createConstructor(propertySpecs);
        this.fields = this.createFields(propertySpecs);
        this.getters = this.createGetters(propertySpecs);
        this.equalsMethod = this.createEquals(propertySpecs);
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(interfaceName + "Impl")
                .addSuperinterface(ClassName.get(this.packageName, interfaceName))
                .addModifiers(PUBLIC)
                .addMethod(this.constructor)
                .addMethods(this.getters)
                .addMethod(this.equalsMethod)
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

    private MethodSpec createEquals(List<PropertySpec> propertySpecs) {
        /*
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySpec that = (PropertySpec) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
         */

        String equalsStatement;
        List<Object> equalsStatementParameters= new LinkedList<>();
        if(propertySpecs.size() > 0) {
            equalsStatement = "return ";
            boolean started = false;
            for (PropertySpec propertySpec : propertySpecs) {
                if(started) {
                    equalsStatement += " && \n";
                }
                started = true;

                equalsStatement += "$T.equals(" + propertySpec.name() + ", that." + propertySpec.name() + ")";
                equalsStatementParameters.add(ClassName.get(Objects.class));
            }

        } else {
            equalsStatement = "return true";
        }

        ClassName className = ClassName.get(this.packageName, this.interfaceName + "Impl");
        return MethodSpec.methodBuilder("equals")
                .addModifiers(PUBLIC)
                .addParameter(ClassName.bestGuess(Object.class.getName()), "o")
                .returns(boolean.class)
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addStatement("$T that = ($T) o", className, className)
                .addStatement(equalsStatement, equalsStatementParameters.toArray())
                .build();
    }
}
