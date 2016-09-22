package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.*;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static javax.lang.model.element.Modifier.*;

/**
 * Created by nelt on 9/13/16.
 */
public class SpecCodeGenerator {

    private final Spec spec;
    private final String packageName;

    public SpecCodeGenerator(Spec spec, String packageName) {
        this.spec = spec;
        this.packageName = packageName;
    }

    public void generateTo(File dir) throws IOException {
        File packageDestination = new File(dir, packageName.replaceAll(".", "/"));

        for (ValueSpec valueSpec : this.spec.valueSpecs()) {
            this.generateValueTypesTo(valueSpec, packageDestination);
        }
    }

    private void generateValueTypesTo(ValueSpec valueSpec, File packageDestination) throws IOException {
        String interfaceName = this.capitalizedFirst(valueSpec.name());

        TypeSpec valueBuilder = this.createValueBuilder(interfaceName, valueSpec.propertySpecs());
        TypeSpec valueInterface = this.createValueInterface(interfaceName, valueBuilder, valueSpec.propertySpecs());
        TypeSpec valueImpl = this.createValueImplementation(interfaceName, valueSpec.propertySpecs());

        this.writeJavaFile(packageDestination, valueInterface);
        this.writeJavaFile(packageDestination, valueImpl);
    }


    private TypeSpec createValueInterface(String interfaceName, TypeSpec valueBuilder, List<PropertySpec> propertySpecs) {
        List<MethodSpec> getters = new LinkedList<>();

        for (PropertySpec propertySpec : propertySpecs) {
            getters.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .returns(ClassName.bestGuess(propertySpec.type()))
                            .addModifiers(PUBLIC, ABSTRACT)
                            .build()
            );
        }

        return TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(PUBLIC)
                    .addMethods(getters)
                    .addType(valueBuilder)
                    .build();
    }

    private TypeSpec createValueBuilder(String interfaceName, List<PropertySpec> propertySpecs) {
        ValueBuilderGenerator valueBuilderGenerator = new ValueBuilderGenerator(interfaceName, propertySpecs);

        return TypeSpec.classBuilder("Builder")
                .addModifiers(PUBLIC, STATIC)
                .addMethod(valueBuilderGenerator.builderMethod())
                .addMethod(valueBuilderGenerator.buildMethod())
                .addFields(valueBuilderGenerator.fields())
                .addMethods(valueBuilderGenerator.setters())
                .build();
    }

    static public Object [] concat(Object first, Object ... others) {
        int size = 1;
        if(others != null) {
            size += others.length;
        }
        Object[] result = new Object[size];
        result[0] = first;
        for(int i = 1 ; i < result.length ; i++) {
            result[i] = others[i-1];
        }
        return result;
    }

    private TypeSpec createValueImplementation(String interfaceName, List<PropertySpec> propertySpecs) {
        List<FieldSpec> fields = new LinkedList<>();
        List<MethodSpec> getters = new LinkedList<>();

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder();

        for (PropertySpec propertySpec : propertySpecs) {
            constructorBuilder
                    .addParameter(ClassName.bestGuess(propertySpec.type()), propertySpec.name())
                    .addStatement("this.$N = $N", propertySpec.name(), propertySpec.name())
            ;


            fields.add(
                    FieldSpec.builder(ClassName.bestGuess(propertySpec.type()), propertySpec.name(), PRIVATE, FINAL).build()
            );
            getters.add(
                    MethodSpec.methodBuilder(propertySpec.name())
                            .returns(ClassName.bestGuess(propertySpec.type()))
                            .addModifiers(PUBLIC)
                            .addStatement("return this.$N", propertySpec.name())
                            .build()
            );
        }

        return TypeSpec.classBuilder(interfaceName + "Impl")
                .addSuperinterface(ClassName.get(this.packageName, interfaceName))
                .addModifiers(PUBLIC)
                .addMethod(constructorBuilder.build())
                .addMethods(getters)
                .addFields(fields)
                .build();
    }

    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
//        file.writeTo(System.out);
    }

    private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
