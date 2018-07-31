package org.codingmatters.value.objects.php.generator;

import org.codingmatters.value.objects.spec.TypeToken;

public enum TypeTokenPhp {
    STRING( "string" ),
    INT( "int" ),
    LONG( "int" ),
    FLOAT( "float" ),
    DOUBLE( "float" ),
    BOOL( "bool" ),
    BYTES( "string" ),
    DATE( "date", "\\io\\flexio\\utils\\FlexDate" ),
    TIME( "time", "\\io\\flexio\\utils\\FlexDate" ),
    DATE_TIME( "date-time", "\\io\\flexio\\utils\\FlexDate" ),
    TZ_DATE_TIME( "date-time", "\\io\\flexio\\utils\\FlexDate" );

    private final String type;
    private String implementationType;

    public static TypeTokenPhp parse( String typeSpec ) {
        for( TypeToken typeToken : TypeToken.values() ) {
            if( typeToken.getImplementationType().equals( typeSpec ) ) {
                return TypeTokenPhp.valueOf( typeToken.name() );
            }
        }
        String name = TypeToken.parse( typeSpec ).name();
        return TypeTokenPhp.valueOf( name );
    }

    TypeTokenPhp( String type ) {
        this( type, type );
    }

    TypeTokenPhp( String type, String implementationType ) {
        this.type = type;
        this.implementationType = implementationType;
    }

    public String getTypeName() {
        return this.type;
    }

}