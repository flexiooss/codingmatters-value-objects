package org.codingmatters.value.objects.js.generator;

import java.util.Locale;

public class Naming {

    public static String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }
}
