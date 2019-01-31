package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.NamingUtils;
import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

public class ObjectTypeNested implements ObjectType {

    private final ParsedValueObject nestValueObject;
    private final String namespace;

    public ObjectTypeNested( ParsedValueObject nestValueObject, String namespace ) {
        this.nestValueObject = nestValueObject;
        if( namespace != null ){
            this.namespace = NamingUtils.convertToNameSpace( namespace );
        } else {
            this.namespace = null;
        }
    }

    public ParsedValueObject nestValueObject() {
        return nestValueObject;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

    public String namespace() {
        return namespace;
    }
}
