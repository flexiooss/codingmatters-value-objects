package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.model.types.ValueObjectType;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import org.codingmatters.value.objects.js.parser.processing.ProcessableYaml;

import java.util.Objects;

public class ValueObjectProperty implements ProcessableYaml {

    private final String name;
    private final ValueObjectType type;

    public ValueObjectProperty( String name, ValueObjectType type ) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public ValueObjectType type() {
        return type;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
