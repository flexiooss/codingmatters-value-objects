package org.codingmatters.value.objects.js.generator;

import java.util.Arrays;
import java.util.Locale;

public class Naming {

    public static String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

    public static String findPackage( String currentPackage, String typeRef ) {
        String[] currentPackageParts = currentPackage.split( "\\." );
        String[] typeRefParts = typeRef.split( "\\." );
        typeRefParts[ typeRefParts.length - 1 ] = className( typeRefParts[ typeRefParts.length - 1 ] );
        int index = 0;
        while( index < currentPackageParts.length && index < typeRefParts.length && currentPackageParts[ index ].equals( typeRefParts[ index ] ) ) {
            index++;
        }
        return rewind( currentPackageParts.length - index ) + String.join( "/", Arrays.copyOfRange( typeRefParts, index, typeRefParts.length ) );
    }

    private static String rewind( int length ) {
        if( length == 0 ) {
            return "./";
        }
        StringBuilder rewind = new StringBuilder();
        for( int i = 0; i < length; i++ ) {
            rewind.append( "../" );
        }
        return rewind.toString();
    }

    public static String className( String typeRef ) {
        String[] split = typeRef.split( "\\." );
        return split[ split.length - 1 ];
    }

    public static String builderName( String typeRef ) {
        String[] split = typeRef.split( "\\." );
        return split[ split.length - 1 ] + "Builder";
    }
}
