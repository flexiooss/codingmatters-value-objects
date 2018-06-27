package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.spec.TypeToken;

public enum TypeTokenPhp {
    STRING( "string" ),
    INT( "integer" ),
    LONG( "integer" ),
    FLOAT( "float" ),
    DOUBLE( "float" ),
    BOOL( "boolean" ),
    BYTES( "" ),
    DATE( "DateTime" ),
    TIME( "DateTime" ),
    DATE_TIME( "DateTime" ),
    TZ_DATE_TIME( "DateTime" );

    private final String type;

    public static TypeTokenPhp parse( String typeSpec ) {

        return TypeTokenPhp.valueOf( TypeToken.parse( typeSpec ).name() );
    }

    TypeTokenPhp( String type ) {
        this.type = type;
    }

    public String getImplementationType() {
        return this.type;
    }

}