package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

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
        TypeSpec.Builder result = this.optionalCollectionHelper.baseOptionalCass();

        return result.build();
    }
}
