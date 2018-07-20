package org.codingmatters.value.objects.php.phpmodel;

import org.codingmatters.value.objects.spec.PropertyTypeSpec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhpPackagedValueSpec {

    private String name;
    private final String packageName;
    private List<PhpPropertySpec> properties;
    private List<PhpMethod> methods;
    private PropertyTypeSpec extender;
    private Set<String> imports;

    public PhpPackagedValueSpec( String packageName, String name ) {
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

    public void addProperty( PhpPropertySpec property ) {
        this.properties.add( property );
    }

    public List<PhpPropertySpec> propertySpecs() {
        return this.properties;
    }

    public List<PhpMethod> methods() {
        return this.methods;
    }

    public void addMethod( PhpMethod method ) {
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
