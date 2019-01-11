package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.parser.model.ParsedValueObject;

import java.util.ArrayList;
import java.util.List;

public class ParsedYAMLSpec {

    private final List<ParsedValueObject> valueObjects;

    public ParsedYAMLSpec() {
        this.valueObjects = new ArrayList<>();
    }

    public List<ParsedValueObject> valueObjects() {
        return this.valueObjects;
    }

}
