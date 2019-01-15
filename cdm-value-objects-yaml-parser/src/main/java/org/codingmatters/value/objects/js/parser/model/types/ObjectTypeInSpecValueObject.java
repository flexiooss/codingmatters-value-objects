package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.util.Objects;

public class ObjectTypeInSpecValueObject implements ObjectType {

    private final String inSpecValueObjectName;

    public ObjectTypeInSpecValueObject( String inSpecValueObjectName ) {
        this.inSpecValueObjectName = inSpecValueObjectName;
    }

    public String inSpecValueObjectName() {
        return inSpecValueObjectName;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
