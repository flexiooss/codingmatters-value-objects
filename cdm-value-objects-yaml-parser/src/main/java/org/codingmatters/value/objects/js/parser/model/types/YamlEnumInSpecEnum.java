package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.NamingUtils;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class YamlEnumInSpecEnum implements YamlEnum {
    private final List<String> values;
    private final String namespace;
    private String name;

    public YamlEnumInSpecEnum( String name, String namespace, List<String> values ) {
        this.name = name;
        this.namespace = NamingUtils.convertToNameSpace( namespace );
        this.values = values;
    }

    public YamlEnumInSpecEnum( String name, String namespace, String... values ) {
        this( name, namespace, Arrays.stream( values ).collect( Collectors.toList() ) );
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
