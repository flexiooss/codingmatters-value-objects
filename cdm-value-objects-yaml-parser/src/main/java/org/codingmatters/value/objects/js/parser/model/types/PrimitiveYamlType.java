package org.codingmatters.value.objects.js.parser.model.types;

import java.util.Objects;

public class PrimitiveYamlType implements ValueObjectType {

    private final YAML_PRIMITIVE_TYPES type;

    public PrimitiveYamlType( String type ) {
        this.type = YAML_PRIMITIVE_TYPES.from( type );
    }

    public enum YAML_PRIMITIVE_TYPES {
        STRING,
        BYTES,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        BOOL,
        DATE,
        TIME,
        DATE_TIME,
        TZ_DATE_TIME;

        public static YAML_PRIMITIVE_TYPES from( String type ) {
            try {
                return YAML_PRIMITIVE_TYPES.valueOf( type.toUpperCase().replace( "-", "_" ) );
            } catch( IllegalArgumentException e ) {
                return null;
            }
        }
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) { return true; }
        if( o == null || getClass() != o.getClass() ) { return false; }
        PrimitiveYamlType that = (PrimitiveYamlType) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash( type );
    }
}
