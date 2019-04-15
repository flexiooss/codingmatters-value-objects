package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import org.codingmatters.value.objects.js.parser.processing.ProcessableYaml;

import java.util.ArrayList;
import java.util.List;

public class ParsedValueObject extends ParsedType {

    private final List<ValueObjectProperty> properties;

    public ParsedValueObject( String name, String packageName ) {
        super( name, packageName );
        this.properties = new ArrayList<>();
    }

    public List<ValueObjectProperty> properties() {
        return this.properties;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

}
