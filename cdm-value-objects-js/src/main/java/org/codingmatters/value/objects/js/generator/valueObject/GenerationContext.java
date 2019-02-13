package org.codingmatters.value.objects.js.generator.valueObject;

public class GenerationContext {

    private final String currentPackage;
    private JsClassGenerator writer;
    private final String typesPackage;

    public GenerationContext( String rootPackage, String typesPackage ) {
        this.currentPackage = rootPackage;
        this.typesPackage = typesPackage;
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
}
