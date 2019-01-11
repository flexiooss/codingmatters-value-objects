package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.spec.PropertyTypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JsPackagedValueSpec {

    private String name;
    private final String packageName;
    private List<JsPropertySpec> properties;
    private List<JsMethod> methods;
    private PropertyTypeSpec extender;
    private Set<String> imports;

    public JsPackagedValueSpec( String packageName, String name ) {
        this.name = name;
        this.packageName = packageName;
        this.properties = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.imports = new HashSet<>();
        this.extender = null;
    }

    public String name() {
        return this.name;
    }

    public void addProperty( JsPropertySpec property ) {
        this.properties.add( property );
    }

    public List<JsPropertySpec> propertySpecs() {
        return this.properties;
    }

    public List<JsMethod> methods() {
        return this.methods;
    }

    public void addMethod( JsMethod method ) {
        this.methods.add( method );
    }

    public void extend( PropertyTypeSpec typeSpec ) {
        extender = typeSpec;
    }

    public PropertyTypeSpec extender() {
        return extender;
    }

    public void addImport( String importation ) {
        imports.add( importation );
    }

    public Set<String> imports() {
        return imports;
    }

    public String packageName() {
        return packageName;
    }
}
