package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.spec.TypeToken;

public enum TypeTokenPhp {
    STRING( "string" ),
    INT( "int" ),
    LONG( "int" ),
    FLOAT( "float" ),
    DOUBLE( "float" ),
    BOOL( "bool" ),
    BYTES( "string" ),
    DATE( "date-time", "DateTime" ),
    TIME( "date-time", "DateTime" ),
    DATE_TIME( "date-time", "DateTime" ),
    TZ_DATE_TIME( "date-time", "DateTime" );

    private final String type;
    private String implementationType;

    public static TypeTokenPhp parse( String typeSpec ) {
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

    public String getImplementationType() {
        return this.implementationType;
    }
}