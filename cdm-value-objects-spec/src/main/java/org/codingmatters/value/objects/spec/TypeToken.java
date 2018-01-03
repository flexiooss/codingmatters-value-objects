package org.codingmatters.value.objects.spec;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by nelt on 9/3/16.
 */
public enum TypeToken {
    STRING(String.class.getName()),
    INT(Integer.class.getName()), LONG(Long.class.getName()),
    FLOAT(Float.class.getName()), DOUBLE(Double.class.getName()),
    BOOL(Boolean.class.getName()),
    BYTES(byte[].class.getName()),
    DATE(LocalDate.class.getName()), TIME(LocalTime.class.getName()),
    DATE_TIME(LocalDateTime.class.getName()), TZ_DATE_TIME(ZonedDateTime.class.getName())
    ;

    private final String implementationType;

    TypeToken(String implementationType) {
        this.implementationType = implementationType;
    }

    public String getImplementationType() {
        return implementationType;
    }

    static public String validTypesSpec() {
        return Arrays.stream(values())
                .map(propertyType -> propertyType.toString().toLowerCase().replaceAll("_", "-"))
                .collect(Collectors.joining(", "));
    }

    static public TypeToken parse(String str) {
        return TypeToken.valueOf(str.replaceAll("-", "_").toUpperCase());
    }
}
