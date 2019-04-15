package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class ValueObjectTypeExternalType implements ValueObjectType {

    private final String typeReference;

    public ValueObjectTypeExternalType( String typeReference ) {
        this.typeReference = typeReference;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

    public String typeReference() {
        return typeReference;
    }
}
