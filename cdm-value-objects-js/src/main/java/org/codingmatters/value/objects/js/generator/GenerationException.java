package org.codingmatters.value.objects.js.generator;

public class GenerationException extends Exception {
    public GenerationException( String message ) {
        super( message );
    }

    public GenerationException( String message, Exception e ) {
        super( message, e );
    }
}
