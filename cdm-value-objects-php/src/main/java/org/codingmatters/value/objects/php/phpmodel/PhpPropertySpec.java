package org.codingmatters.value.objects.php.phpmodel;

import org.codingmatters.value.objects.spec.PropertyTypeSpec;


public class PhpPropertySpec {

    private final PropertyTypeSpec propertyTypeSpec;
    private String name;

    public PhpPropertySpec( PropertyTypeSpec propertyTypeSpec, String name ) {
        this.name = name;
        this.propertyTypeSpec = propertyTypeSpec;
    }

    public String name() {
        return this.name;
    }

    public PropertyTypeSpec typeSpec() {
        return propertyTypeSpec;
    }
}
