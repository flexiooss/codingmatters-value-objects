package org.codingmatters.value.objects.spec;

import java.util.List;

/**
 * Created by nelt on 4/1/17.
 */
public interface PropertyHolderSpec {
    List<PropertySpec> propertySpecs();
    PropertySpec propertySpec(String name);
}
