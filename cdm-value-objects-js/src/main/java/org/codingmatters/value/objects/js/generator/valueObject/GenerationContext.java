package org.codingmatters.value.objects.js.generator.valueObject;

import java.util.HashSet;
import java.util.Set;

public class GenerationContext {

    private final String currentPackage;
    private JsClassGenerator writer;
    private final String typesPackage;
    private Set<String> imports;

    public GenerationContext( String rootPackage, String typesPackage ) {
        this.currentPackage = rootPackage;
        this.typesPackage = typesPackage;
        this.imports = new HashSet<>();
    }

    public String currentPackage() {
        return currentPackage;
    }

    public String currentPackagePath() {
        return this.currentPackage.replace( ".", "/" );
    }

    public void writer( JsClassGenerator writer ) {
        this.writer = writer;
    }

    public JsClassGenerator write() {
        return writer;
    }

    public String typesPackage() {
        return this.typesPackage;
    }

    public Set<String> imports() {
        return imports;
    }

    public void addImport( String inport ) {
        imports.add( inport );
    }
}
