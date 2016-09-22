package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

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
        String interfaceName = capitalizedFirst(valueSpec.name());

        TypeSpec valueBuilder = this.createValueBuilder(interfaceName, valueSpec.propertySpecs());
        TypeSpec valueInterface = this.createValueInterface(interfaceName, valueBuilder, valueSpec.propertySpecs());
        TypeSpec valueImpl = this.createValueImplementation(interfaceName, valueSpec.propertySpecs());

        this.writeJavaFile(packageDestination, valueInterface);
        this.writeJavaFile(packageDestination, valueImpl);
    }


    private TypeSpec createValueInterface(String interfaceName, TypeSpec valueBuilder, List<PropertySpec> propertySpecs) {
        ValueInterface valueInterface = new ValueInterface(propertySpecs);

        return TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(PUBLIC)
                    .addMethods(valueInterface.getters())
                    .addType(valueBuilder)
                    .build();
    }

    private TypeSpec createValueBuilder(String interfaceName, List<PropertySpec> propertySpecs) {
        ValueBuilder valueBuilderGenerator = new ValueBuilder(interfaceName, propertySpecs);

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
        ValueImplementation valueImplementation = new ValueImplementation(interfaceName, propertySpecs);

        return TypeSpec.classBuilder(interfaceName + "Impl")
                .addSuperinterface(ClassName.get(this.packageName, interfaceName))
                .addModifiers(PUBLIC)
                .addMethod(valueImplementation.constructor())
                .addMethods(valueImplementation.getters())
                .addFields(valueImplementation.fields())
                .build();
    }



    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
//        file.writeTo(System.out);
    }

    static public String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
