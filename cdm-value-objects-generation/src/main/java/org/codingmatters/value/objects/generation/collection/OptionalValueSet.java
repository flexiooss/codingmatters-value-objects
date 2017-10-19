package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Optional;

public class OptionalValueSet {
    private final String packageName;
    private final TypeSpec valueSetInterface;
    private final OptionalCollectionHelper optionalCollectionHelper;

    public OptionalValueSet(String packageName, TypeSpec valueSetInterface) {
        this.packageName = packageName;
        this.valueSetInterface = valueSetInterface;
        this.optionalCollectionHelper = new OptionalCollectionHelper(ClassName.get(this.packageName, valueSetInterface.name));
    }

    public TypeSpec type() {
        return this.optionalCollectionHelper.baseOptionalCass()
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(this.optionalCollectionHelper.valueCollection(), "elements")
                        .addStatement("this.optional = $T.ofNullable(elements)", Optional.class)
                        .build())
                .build();
    }
}
