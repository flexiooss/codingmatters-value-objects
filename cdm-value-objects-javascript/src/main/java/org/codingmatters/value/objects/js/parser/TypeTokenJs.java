package org.codingmatters.value.objects.js.parser;

import org.codingmatters.value.objects.spec.TypeToken;

public enum TypeTokenJs {
    STRING( "string" ),

    INT( "int" ),

    LONG( "int" ),

    FLOAT( "float" ),

    DOUBLE( "float" ),

    BOOL( "bool" ),

    BYTES( "string" ),

    DATE( "date","\\io\\flexio\\utils\\FlexDate" ),

    TIME( "time","\\io\\flexio\\utils\\FlexDate" ),

    DATE_TIME( "date-time","\\io\\flexio\\utils\\FlexDate" ),

    TZ_DATE_TIME( "tz-date-time","\\io\\flexio\\utils\\FlexDate" );

    private final String type;
    private String implementationType;

    public static TypeTokenJs parse( String typeSpec ) {
        for( TypeToken typeToken : TypeToken.values() ) {
            if( typeToken.getImplementationType().equals( typeSpec ) ) {
                return TypeTokenJs.valueOf( typeToken.name() );
            }
        }
        String name = TypeToken.parse( typeSpec ).name();
        return TypeTokenJs.valueOf( name );
    }

    TypeTokenJs( String type ) {
        this( type, type );
    }

    TypeTokenJs( String type, String implementationType ) {
        this.type = type;
        this.implementationType = implementationType;
    }

    public String getTypeName() {
        return this.type;
    }
}
