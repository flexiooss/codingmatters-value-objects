package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Created by nelt on 4/1/17.
 */
public class ValueCollectionConfiguration {
    private final ClassName valueListType;
    private final ClassName valueListImplementationType;
    private final ClassName valueSetType;
    private final ClassName valueSetImplementationType;

    public ValueCollectionConfiguration(String packageName) {
        this.valueListType = ClassName.get(packageName, "ValueList");
        this.valueListImplementationType = ClassName.get(packageName, "ValueListImpl");
        this.valueSetType = ClassName.get(packageName, "ValueSet");
        this.valueSetImplementationType = ClassName.get(packageName, "ValueSetImpl");
    }

    public ClassName valueListType() {
        return valueListType;
    }

    public ParameterizedTypeName valueListOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueListType, type);
    }

    public ParameterizedTypeName valueListImplOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueListImplementationType, type);
    }

    public ClassName valueSetType() {
        return valueSetType;
    }

    public ParameterizedTypeName valueSetOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueSetType, type);
    }

    public ParameterizedTypeName valueSetImplOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueSetImplementationType, type);
    }
}
