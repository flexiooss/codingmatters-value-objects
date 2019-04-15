package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.util.List;

public class ParsedEnum extends ParsedType {

    private final List<String> enumValues;

    public ParsedEnum( String name, String packageName, List<String> enumValues ) {
        super( name, packageName );
        this.enumValues = enumValues;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

    public List<String> enumValues() {
        return this.enumValues;
    }
}
