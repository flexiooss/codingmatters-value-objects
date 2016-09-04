package org.codingmatters.value.objects.spec;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by nelt on 9/3/16.
 */
public enum PropertyType {
    STRING(String.class.getName()),
    INT(Integer.class.getName()), LONG(Long.class.getName()),
    FLOAT(Float.class.getName()), DOUBLE(Double.class.getName()),
    BOOL(Boolean.class.getName()),
    OBJECT("reference");

    private final String referencedType;

    PropertyType(String referencedType) {
        this.referencedType = referencedType;
    }

    public String getReferencedType() {
        return referencedType;
    }

    static public String validTypesSpec() {
        return Arrays.stream(values())
                .map(propertyType -> propertyType.toString().toLowerCase())
                .collect(Collectors.joining(", "));
    }
}
