package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class ObjectTypeExternalValue implements ObjectType {

    private final String objectReference;

    public ObjectTypeExternalValue( String objectReference ) {
        this.objectReference = objectReference;
    }

    public String objectReference() {
        return objectReference;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
