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
        String interfaceName = capitalizedFirst(valueSpec.name());

        TypeSpec valueInterface = new ValueInterface(interfaceName, valueSpec.propertySpecs()).type();
        this.writeJavaFile(packageDestination, valueInterface);

        TypeSpec valueImpl = new ValueImplementation(this.packageName, interfaceName, valueSpec.propertySpecs()).type();
        this.writeJavaFile(packageDestination, valueImpl);
    }


    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
//        file.writeTo(System.out);
    }

    static public String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
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
}
