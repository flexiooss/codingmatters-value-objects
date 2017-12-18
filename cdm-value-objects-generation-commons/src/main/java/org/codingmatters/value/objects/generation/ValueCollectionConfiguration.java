package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Created by nelt on 4/1/17.
 */
public class ValueCollectionConfiguration {
    private final ClassName valueListType;
    private final ClassName optionalValueListType;
    private final ClassName valueListImplementationType;
    private final ClassName valueListBuilderType;
    private final ClassName valueSetType;
    private final ClassName optionalValueSetType;
    private final ClassName valueSetImplementationType;
    private final ClassName valueSetBuilderType;

    public ValueCollectionConfiguration(String packageName) {
        this.valueListType = ClassName.get(packageName, "ValueList");
        this.optionalValueListType = ClassName.get(packageName + ".optional", "OptionalValueList");
        this.valueListImplementationType = ClassName.get(packageName, "ValueListImpl");
        this.valueListBuilderType = this.valueListType.nestedClass("Builder");

        this.valueSetType = ClassName.get(packageName, "ValueSet");
        this.optionalValueSetType = ClassName.get(packageName + ".optional", "OptionalValueSet");
        this.valueSetImplementationType = ClassName.get(packageName, "ValueSetImpl");
        this.valueSetBuilderType = this.valueSetType.nestedClass("Builder");
    }

    public ClassName valueListType() {
        return valueListType;
    }

    public ParameterizedTypeName valueListOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueListType, type);
    }

    public ParameterizedTypeName optionalValueListOfType(TypeName type, TypeName optionalType) {
        return ParameterizedTypeName.get(this.optionalValueListType, type, optionalType);
    }

    public ParameterizedTypeName valueListImplOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueListImplementationType, type);
    }

    public ClassName valueListBuilderType() {
        return valueListBuilderType;
    }

    public ClassName valueSetType() {
        return valueSetType;
    }

    public ParameterizedTypeName valueSetOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueSetType, type);
    }

    public ParameterizedTypeName optionalValueSetOfType(TypeName type) {
        return ParameterizedTypeName.get(this.optionalValueSetType, type);
    }

    public ParameterizedTypeName valueSetImplOfType(TypeName type) {
        return ParameterizedTypeName.get(this.valueSetImplementationType, type);
    }

    public ClassName valueSetBuilderType() {
        return valueSetBuilderType;
    }

    public TypeName rawValueList() {
        return this.valueListType;
    }

    public TypeName rawValueSet() {
        return this.valueSetType;
    }
}
