package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class ObjectTypeInSpecValueObject implements ObjectType {

    private final String inSpecValueObjectName;
    private final String packageName;

    public ObjectTypeInSpecValueObject( String inSpecValueObjectName, String packageName ) {
        this.inSpecValueObjectName = inSpecValueObjectName;
        this.packageName = packageName;
    }

    public String inSpecValueObjectName() {
        return inSpecValueObjectName;
    }

    public String packageName() {
        return packageName;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
