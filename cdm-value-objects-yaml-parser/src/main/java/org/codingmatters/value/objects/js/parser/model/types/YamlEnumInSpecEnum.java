package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class YamlEnumInSpecEnum implements YamlEnum {
    private final List<String> values;
    private final String namespace;
    private String name;

    public YamlEnumInSpecEnum( String name, Stack<String> context, List<String> values ) {
        this.name = name;
        this.namespace = String.join( ".", context );
        this.values = values;
    }

    public YamlEnumInSpecEnum( String name, Stack<String> context, String... values ) {
        this.name = name;
        this.namespace = String.join( ".", context );
        this.values = Arrays.stream( values ).collect( Collectors.toList() );
    }

    public List<String> values() {
        return values;
    }

    public String namespace() {
        return namespace;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

    public String name() {
        return name;
    }
}
