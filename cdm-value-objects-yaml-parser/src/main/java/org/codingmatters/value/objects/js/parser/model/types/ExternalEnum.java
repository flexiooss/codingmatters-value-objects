package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Objects;

public class ExternalEnum implements YamlEnum {

    private final String enumReference;

    public ExternalEnum( String enumValue ) {
        this.enumReference = enumValue;
    }

    public String enumReference() {
        return enumReference;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        ExternalEnum that = (ExternalEnum) o;
        return this.enumReference.equals( that.enumReference );
    }

    @Override
    public int hashCode() {

        return Objects.hash( enumReference );
    }
}
