package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.generation.collection.ValueList;
import org.codingmatters.value.objects.generation.collection.ValueListImplementation;
import org.codingmatters.value.objects.generation.collection.ValueSet;
import org.codingmatters.value.objects.generation.collection.ValueSetImplementation;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.io.File;
import java.io.IOException;

import static org.codingmatters.value.objects.spec.PropertyCardinality.LIST;
import static org.codingmatters.value.objects.spec.PropertyCardinality.SET;

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

        if(this.hasPropertyWithCardinality(this.spec, LIST)) {
            TypeSpec valueListInterface = new ValueList(this.packageName).type();
            this.writeJavaFile(packageDestination, valueListInterface);
            this.writeJavaFile(packageDestination, new ValueListImplementation(this.packageName, valueListInterface).type());
        }
        if(this.hasPropertyWithCardinality(this.spec, SET)) {
            TypeSpec valueSetInterface = new ValueSet(this.packageName).type();
            this.writeJavaFile(packageDestination, valueSetInterface);
            this.writeJavaFile(packageDestination, new ValueSetImplementation(this.packageName, valueSetInterface).type());
        }

        for (ValueSpec valueSpec : this.spec.valueSpecs()) {
            this.generateValueTypesTo(valueSpec, packageDestination);
        }
    }

    private boolean hasPropertyWithCardinality(Spec spec, PropertyCardinality cardinality) {
        for (ValueSpec valueSpec : spec.valueSpecs()) {
            for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
                if(cardinality.equals(propertySpec.typeSpec().cardinality())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void generateValueTypesTo(ValueSpec valueSpec, File packageDestination) throws IOException {
        ValueConfiguration types = new ValueConfiguration(this.packageName, valueSpec);

        TypeSpec valueInterface = new ValueInterface(types, valueSpec.propertySpecs()).type();
        this.writeJavaFile(packageDestination, valueInterface);

        TypeSpec valueImpl = new ValueImplementation(types, valueSpec.propertySpecs()).type();
        this.writeJavaFile(packageDestination, valueImpl);
    }


    private void writeJavaFile(File packageDestination, TypeSpec valueInterface) throws IOException {
        JavaFile file = JavaFile.builder(this.packageName, valueInterface).build();
        file.writeTo(packageDestination);
        file.writeTo(System.out);
    }

}
