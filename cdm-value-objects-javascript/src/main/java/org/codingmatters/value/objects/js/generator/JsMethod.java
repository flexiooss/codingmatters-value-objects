package org.codingmatters.value.objects.js.generator;

import java.util.ArrayList;
import java.util.List;

public class JsMethod {
    private String name;
    private List<JsParameter> parameters;
    private List<String> instructions;
    private String retrunType;

    public JsMethod( String name ) {
        this.name = name;
        this.parameters = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    public void addParameters( JsParameter parameter ) {
        this.parameters.add( parameter );
    }

    public void addInstruction( String instruction ) {
        this.instructions.add( instruction );
    }

    public String name() {
        return this.name;
    }

    public void returnType( String returnType ) {
        this.retrunType = returnType;
    }

    public List<JsParameter> parameters() {
        return this.parameters;
    }

    public List<String> instructions() {
        return instructions;
    }

    public String type() {
        return this.retrunType;
    }
}
