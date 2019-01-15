package org.codingmatters.value.objects.js.parser.processing;


import org.codingmatters.value.objects.js.error.ProcessingException;

public interface ProcessableYaml {
    public void process( ParsedYamlProcessor processor ) throws ProcessingException;

}
