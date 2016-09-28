package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.File;
import java.io.IOException;

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
        ValueObjectConfiguration types = new ValueObjectConfiguration(this.packageName, valueSpec);

        TypeSpec valueInterface = new ValueInterface(types, valueSpec.propertySpecs()).type();
        this.writeJavaFile(packageDestination, valueInterface);

        TypeSpec valueImpl = new ValueImplementation(types, valueSpec.propertySpecs()).type();
        this.writeJavaFile(packageDestination, valueImpl);
    }


    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
    }

}
