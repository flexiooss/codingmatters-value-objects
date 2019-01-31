package org.codingmatters.value.objects.js.parser;

import java.util.Collection;
import java.util.Stack;
import java.util.stream.Collectors;

public class NamingUtils {

    public static String nestedTypeName( Collection<String> context ) {
        return String.join( "", context.stream().map( NamingUtils::camelCase ).collect( Collectors.toList() ) );
    }

    public static String camelCase( String str ) {
        return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
    }

    public static String namespace( Stack<String> context ) {
        return String.join( ".", context.subList( 0, context.size() - 1 ) );
    }

    public static String convertToNameSpace( String namespace ) {
        return namespace.toLowerCase();
    }
}
