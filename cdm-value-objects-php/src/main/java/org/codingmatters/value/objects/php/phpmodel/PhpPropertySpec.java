package org.codingmatters.value.objects.php.phpmodel;

import org.codingmatters.value.objects.spec.PropertyTypeSpec;


public class PhpPropertySpec {

    private final PropertyTypeSpec propertyTypeSpec;
    private final String name;
    private final String realName;

    public PhpPropertySpec( PropertyTypeSpec propertyTypeSpec, String name, String realName ) {
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
