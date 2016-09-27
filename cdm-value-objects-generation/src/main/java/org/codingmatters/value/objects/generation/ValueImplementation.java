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
    private final MethodSpec hashCodeMethod;

    public ValueImplementation(String packageName, String interfaceName, List<PropertySpec> propertySpecs) {
        this.packageName = packageName;
        this.interfaceName = interfaceName;
        this.constructor = this.createConstructor(propertySpecs);
        this.fields = this.createFields(propertySpecs);
        this.getters = this.createGetters(propertySpecs);
        this.equalsMethod = this.createEquals(propertySpecs);
        this.hashCodeMethod = this.createHashCode(propertySpecs);
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(interfaceName + "Impl")
                .addSuperinterface(ClassName.get(this.packageName, interfaceName))
                .addModifiers(PUBLIC)
                .addMethod(this.constructor)
                .addFields(this.fields)
                .addMethods(this.getters)
                .addMethod(this.equalsMethod)
                .addMethod(this.hashCodeMethod)
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
        ClassName className = ClassName.get(this.packageName, this.interfaceName + "Impl");
        /*
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySpec that = (PropertySpec) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
         */

        String statement;
        List<Object> bindings= new LinkedList<>();
        if(propertySpecs.size() > 0) {
            statement = "$T that = ($T) o;\n";
            bindings.add(className);
            bindings.add(className);

            statement += "return ";
            boolean started = false;
            for (PropertySpec propertySpec : propertySpecs) {
                if(started) {
                    statement += " && \n";
                }
                started = true;

                statement += "$T.equals(this." + propertySpec.name() + ", that." + propertySpec.name() + ")";
                bindings.add(ClassName.get(Objects.class));
            }

        } else {
            statement = "return true";
        }

        return MethodSpec.methodBuilder("equals")
                .addModifiers(PUBLIC)
                .addParameter(ClassName.bestGuess(Object.class.getName()), "o")
                .returns(boolean.class)
                .addAnnotation(ClassName.get(Override.class))
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addStatement(statement, bindings.toArray())
                .build();
    }

    private MethodSpec createHashCode(List<PropertySpec> propertySpecs) {
        String statement = "return $T.hash(";

        //return Objects.hash(valueSpecs);

        boolean started = false;
        for (PropertySpec propertySpec : propertySpecs) {
            if(started) {
                statement += ", ";
            }
            started = true;
            statement += "this." + propertySpec.name();
        }
        statement += ")";

        return MethodSpec.methodBuilder("hashCode")
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addAnnotation(ClassName.get(Override.class))
                .addStatement(statement, ClassName.get(Objects.class))
                .build();
    }
}
