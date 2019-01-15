package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;
import org.codingmatters.value.objects.js.parser.processing.ProcessableYaml;

import java.util.ArrayList;
import java.util.List;

public class ParsedYAMLSpec implements ProcessableYaml {

    private final List<ParsedValueObject> valueObjects;

    public ParsedYAMLSpec() {
        this.valueObjects = new ArrayList<>();
    }

    public List<ParsedValueObject> valueObjects() {
        return this.valueObjects;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }
}
