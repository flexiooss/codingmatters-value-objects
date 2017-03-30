package org.codingmatters.value.objects.json;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.spec.PropertySpec;

import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * Created by nelt on 3/30/17.
 */
public class ValueWriter {
    private final ValueConfiguration types;
    private final List<PropertySpec> propertySpecs;

    public ValueWriter(ValueConfiguration types, List<PropertySpec> propertySpecs) {
        this.types = types;
        this.propertySpecs = propertySpecs;
    }

    public TypeSpec type() {
        return TypeSpec.classBuilder(this.types.valueType().simpleName() + "Writer")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(
                        MethodSpec.methodBuilder("write")
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(this.types.valueType(), "value")
                                .returns(String.class)
                                .addStatement("return null")
                                .build()
                )
                .build();
    }
}
