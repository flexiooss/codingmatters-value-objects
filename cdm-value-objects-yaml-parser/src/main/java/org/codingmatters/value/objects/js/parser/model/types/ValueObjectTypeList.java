package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;


public class ValueObjectTypeList implements ValueObjectType {

    private final String name;
    private final ValueObjectType type;
    private final String namespace;

    public ValueObjectTypeList( String name, ValueObjectType type, String namespace ) {
        this.type = type;
        this.name = name;
        this.namespace = namespace;
    }

    public ValueObjectType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String namespace() {
        return namespace;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

}
