package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.util.List;
import java.util.Objects;


public class ValueObjectTypeList implements ValueObjectType {

    private final String name;
    private final ValueObjectType type;
    private final String namespace;

    public ValueObjectTypeList( String name, ValueObjectType type, List<String> context ) {
        this.type = type;
        this.name = name;
        this.namespace = String.join( ".", context );
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
