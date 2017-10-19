package org.codingmatters.value.objects.generation.collection;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;

public class OptionalValueList {
    private final String packageName;
    private final TypeSpec valueListInterface;
    private final OptionalCollectionHelper optionalCollectionHelper;

    public OptionalValueList(String packageName, TypeSpec valueListInterface) {
        this.packageName = packageName;
        this.valueListInterface = valueListInterface;
        this.optionalCollectionHelper = new OptionalCollectionHelper(ClassName.get(this.packageName, valueListInterface.name));
    }

    public TypeSpec type() {
        TypeSpec.Builder result = this.optionalCollectionHelper.baseOptionalCass();
        return result.build();
    }
}
