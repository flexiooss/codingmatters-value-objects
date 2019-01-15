package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.util.Objects;

public class YamlEnumExternalEnum implements YamlEnum {

    private final String enumReference;

    public YamlEnumExternalEnum( String enumValue ) {
        this.enumReference = enumValue;
    }

    public String enumReference() {
        return enumReference;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
