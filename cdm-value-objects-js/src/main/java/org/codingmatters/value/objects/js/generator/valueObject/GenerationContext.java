package org.codingmatters.value.objects.js.generator.valueObject;

public class GenerationContext {

    private final String currentPackage;
    private JsClassGenerator writer;

    public GenerationContext( String rootPackage ) {
        this.currentPackage = rootPackage;
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
}
