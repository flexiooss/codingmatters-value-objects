package org.codingmatters.value.objects.js.parser;

import java.util.Stack;
import java.util.stream.Collectors;

public class NamingUtils {

    public static String nestedTypeName( Stack<String> context ) {
        return String.join( "", context.stream().map( NamingUtils::camelCase ).collect( Collectors.toList() ) );
    }

    private static String camelCase( String str ) {
        return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
    }
}
