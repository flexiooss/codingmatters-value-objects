package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class ValueObjectTypePrimitiveType implements ValueObjectType {

    private final YAML_PRIMITIVE_TYPES type;

    public ValueObjectTypePrimitiveType( String type ) {
        this.type = YAML_PRIMITIVE_TYPES.from( type );
    }

    public YAML_PRIMITIVE_TYPES type() {
        return type;
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
        TZ_DATE_TIME,
        OBJECT;

        public static YAML_PRIMITIVE_TYPES from( String type ) {
            try {
                return YAML_PRIMITIVE_TYPES.valueOf( type.toUpperCase().replace( "-", "_" ) );
            } catch( IllegalArgumentException e ) {
                return null;
            }
        }
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
