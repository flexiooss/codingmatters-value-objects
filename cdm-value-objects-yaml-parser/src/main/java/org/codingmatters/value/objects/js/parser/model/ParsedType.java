package org.codingmatters.value.objects.js.parser.model;

import org.codingmatters.value.objects.js.parser.processing.ProcessableYaml;

import java.util.List;

public abstract class ParsedType implements ProcessableYaml {

    protected final String packageName;
    protected final String name;

    public ParsedType( String name, String packageName ) {
        this.name = name;
        this.packageName = packageName;
    }

    public String name() {
        return this.name;
    }

    public String packageName() {
        return packageName;
    }
}
