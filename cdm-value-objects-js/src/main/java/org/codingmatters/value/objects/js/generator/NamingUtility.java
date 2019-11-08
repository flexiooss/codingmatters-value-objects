package org.codingmatters.value.objects.js.generator;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class NamingUtility {

    public static String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

    public static String firstLetterLowerCase( String name ) {
        return name.substring( 0, 1 ).toLowerCase( Locale.ENGLISH ) + name.substring( 1 );
    }

    public static String findPackage( String currentPackage, String typeRef ) {
        String[] currentPackageParts = currentPackage.split( "\\." );
        String[] typeRefParts = typeRef.split( "\\." );
//        typeRefParts[typeRefParts.length - 1] = className( typeRefParts[typeRefParts.length - 1] );
        int index = 0;
        while( index < currentPackageParts.length && index < typeRefParts.length && currentPackageParts[index].equals( typeRefParts[index] ) ){
            index++;
        }
        return rewind( currentPackageParts.length - index ) + String.join( "/", Arrays.copyOfRange( typeRefParts, index, typeRefParts.length ) );
    }

    private static String rewind( int length ) {
        if( length == 0 ){
            return "./";
        }
        StringBuilder rewind = new StringBuilder();
        for( int i = 0; i < length; i++ ){
            rewind.append( "../" );
        }
        return rewind.toString();
    }

    public static String className( String... valueObjectName ) {
        return getJoinedName( String.join( " ", valueObjectName ) );
    }

    public static String className( String valueObjectName ) {
        String[] split = valueObjectName.split( "\\." );
        return getJoinedName( split[split.length - 1] );
    }

    public static String builderName( String valueObjectName ) {
        String[] split = valueObjectName.split( "\\." );
        return className( split[split.length - 1] ) + "Builder";
    }

    public static String propertyName( String name ) {
        return firstLetterLowerCase( String.join( "", Arrays.stream( name.split( "-" ) ).map( NamingUtility::firstLetterUpperCase ).collect( Collectors.toList() ) ) );
    }

    public static String attributeName( String name ) {
        return "_" + firstLetterLowerCase( String.join( "", Arrays.stream( name.split( "-" ) ).map( NamingUtility::firstLetterUpperCase ).collect( Collectors.toList() ) ) );
    }

    public static String getJoinedName( String value ) {
//        return String.join( "", Arrays.stream( value.split( "\\s" ) ).map( NamingUtility::firstLetterUpperCase ).collect( Collectors.toList() ) );
        return String.join( "", Arrays.stream( value.replace("-", " ").split( "\\s" ) ).map( NamingUtility::firstLetterUpperCase ).collect( Collectors.toList() ) );
    }

    public static String builderFullName( String reference ) {
        String name = "globalFlexioImport." + reference.substring( 0, reference.lastIndexOf( "." ) + 1 ) + NamingUtility.builderName( reference );
        return name;
    }

    public static String classFullName( String reference ) {
        String name = "globalFlexioImport." + reference;
        return name;
    }

    public static String namespace( String name ) {
        return name.replaceAll("[\\s-]", "").toLowerCase(Locale.ENGLISH);
    }

    public static String requestName( String displayName, String method ) {
        return className( displayName, method.toLowerCase(), "Request" );
    }

    public static String responseName( String displayName, String method, String... parts ) {
        if( parts.length > 0 ){
            return className( displayName, method.toLowerCase(), className( parts ), "Response" );
        }
        return className( displayName, method.toLowerCase(), "Response" );
    }

    public static String statusClassName( int code ) {
        return "Status" + code;
    }

    public static String statusProperty( int code ) {
        return "status" + code;
    }

    public static String getApiName( String apiNameV10 ) {
        return apiNameV10.toLowerCase().replaceAll( "\\s+", "-" );
    }
}
