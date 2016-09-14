package org.codingmatters.value.objects;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.File;
import java.io.IOException;

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
        String interfaceName = this.capitalizedFirst(valueSpec.name());

        TypeSpec valueBuilder = this.createValueBuilder(interfaceName);
        TypeSpec valueInterface = this.createValueInterface(interfaceName, valueBuilder);
        TypeSpec valueImpl = this.createValueImplementation(interfaceName);

        this.writeJavaFile(packageDestination, valueInterface);
        this.writeJavaFile(packageDestination, valueImpl);
    }

    private TypeSpec createValueImplementation(String interfaceName) {
        return TypeSpec.classBuilder(interfaceName + "Impl")
                    .addSuperinterface(ClassName.get(this.packageName, interfaceName))
                    .build();
    }

    private TypeSpec createValueInterface(String interfaceName, TypeSpec valueBuilder) {
        return TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(PUBLIC)
                    .addType(valueBuilder)
                    .build();
    }

    private TypeSpec createValueBuilder(String interfaceName) {
        MethodSpec builderMethod = MethodSpec.methodBuilder("builder")
                .addModifiers(STATIC, PUBLIC)
                .returns(ClassName.bestGuess("Builder"))
                .addStatement("return new $T()", ClassName.bestGuess("Builder"))
                .build();
        MethodSpec buildMethod = MethodSpec.methodBuilder("build")
                .addModifiers(PUBLIC)
                .returns(ClassName.bestGuess(interfaceName))
                .addStatement("return new $T()", ClassName.bestGuess(interfaceName + "Impl"))
                .build();

        return TypeSpec.classBuilder("Builder")
                .addModifiers(PUBLIC, STATIC)
                .addMethod(builderMethod)
                .addMethod(buildMethod)
                .build();
    }

    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
    }

    private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
