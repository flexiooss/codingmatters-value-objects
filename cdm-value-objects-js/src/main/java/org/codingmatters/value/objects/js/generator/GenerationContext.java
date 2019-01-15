package org.codingmatters.value.objects.js.generator;

import java.io.IOException;

public class GenerationContext {

    private final String currentPackage;
    private JsClassWriter writer;

    public GenerationContext( String rootPackage ) {
        this.currentPackage = rootPackage;
    }

    public String currentPackage( ) {
        return currentPackage;
    }

    public String currentPackagePath( ) {
        return this.currentPackage.replace( ".", "/" );
    }

    public void writer( JsClassWriter writer ) {
        this.writer = writer;
    }

    public JsClassWriter write( ) {
        return writer;
    }
}
