package org.codingmatters.value.objects.js.parser.model.types;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.parser.NamingUtils;
import org.codingmatters.value.objects.js.parser.processing.ParsedYamlProcessor;


public class ValueObjectTypeList implements ValueObjectType {

    private final String name;
    private final ValueObjectType type;
    private final String packageName;

    public ValueObjectTypeList( String name, ValueObjectType type, String packageName ) {
        this.type = type;
        this.name = name;
        this.packageName = NamingUtils.convertToNameSpace( packageName );
    }

    public ValueObjectType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String packageName() {
        return packageName;
    }

    @Override
    public void process( ParsedYamlProcessor processor ) throws ProcessingException {
        processor.process( this );
    }

}
