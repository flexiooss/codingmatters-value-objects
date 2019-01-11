package org.codingmatters.value.objects.js.generator;

public class JsParameter {
    private String name;
    private String type;

    public JsParameter( String name, String type ) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return this.name;
    }

    public String type() {
        return this.type;
    }
}
