package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import org.codingmatters.value.objects.js.parser.processing.ProcessableYaml;

import java.util.ArrayList;
import java.util.List;

public class ParsedYAMLSpec implements ProcessableYaml {

    private final List<ParsedType> valueObjects;

    public ParsedYAMLSpec() {
        this.valueObjects = new ArrayList<>();
    }

    public ParsedYAMLSpec( List<ParsedType> valueObjects ) {
        this.valueObjects = valueObjects;
    }

    public List<ParsedType> valueObjects() {
        return this.valueObjects;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
