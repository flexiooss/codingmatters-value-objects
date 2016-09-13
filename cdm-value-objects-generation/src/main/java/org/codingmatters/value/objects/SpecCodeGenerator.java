package org.codingmatters.value.objects;

import com.squareup.javapoet.JavaFile;
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
            String interfaceName = this.capitalizedFirst(valueSpec.name());

            TypeSpec valueBuilder = TypeSpec.classBuilder("Builder")
                    .addModifiers(PUBLIC, STATIC)
                    .build();
            TypeSpec valueInterface = TypeSpec.interfaceBuilder(interfaceName)
                    .addModifiers(PUBLIC)
                    .addType(valueBuilder)
                    .build();
            TypeSpec valueImpl = TypeSpec.classBuilder(interfaceName + "Impl")
                    .build();


            this.writeJavaFile(packageDestination, valueInterface);
            this.writeJavaFile(packageDestination, valueImpl);


        }
    }

    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
    }

    private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
