package org.codingmatters.value.objects.spec;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by nelt on 9/3/16.
 */
public enum PropertyType {
    STRING,
    INT, LONG,
    FLOAT, DOUBLE,
    BOOL
    ;

    static public String validTypesSpec() {
        return Arrays.stream(values())
                .map(propertyType -> propertyType.toString().toLowerCase())
                .collect(Collectors.joining(", "));
    }
}
