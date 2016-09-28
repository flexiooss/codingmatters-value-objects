package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;

/**
 * Created by nelt on 9/28/16.
 */
public class ValueObjectConfiguration {

    private final ClassName valueType;
    private final ClassName valueImplType;
    private final ClassName builderType;

    public ValueObjectConfiguration(String packageName, String interfaceName) {
        this.valueType = ClassName.get(packageName, interfaceName);
        this.valueImplType = ClassName.get(packageName, interfaceName + "Impl");
        this.builderType = ClassName.get(packageName, interfaceName + ".Builder");
    }

    public ClassName valueType() {
        return valueType;
    }

    public ClassName valueImplType() {
        return valueImplType;
    }

    public ClassName builderType() {
        return builderType;
    }
}
