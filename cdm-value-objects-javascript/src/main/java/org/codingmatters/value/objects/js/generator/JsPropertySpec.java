package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.spec.PropertyTypeSpec;

public class JsPropertySpec {
    private final PropertyTypeSpec propertyTypeSpec;
    private final String name;
    private final String realName;

    public JsPropertySpec( PropertyTypeSpec propertyTypeSpec, String name, String realName ) {
        this.name = name;
        this.realName = realName;
        this.propertyTypeSpec = propertyTypeSpec;
    }

    public String name() {
        return this.name;
    }

    public String realName() {
        return realName;
    }

    public PropertyTypeSpec typeSpec() {
        return propertyTypeSpec;
    }
}
