package org.codingmatters.value.objects.php.phpmodel;

public class PhpEnum {

    private final String packageName;
    private final String[] enumValues;
    private final String name;

    public PhpEnum( String typeRef, String... enumValues ) {
        this.name = typeRef.substring( typeRef.lastIndexOf( "." ) + 1 );
        this.packageName = typeRef.substring( 0, typeRef.lastIndexOf( "." ) );
        this.enumValues = enumValues;
    }

    public String packageName() {
        return packageName;
    }

    public String[] enumValues() {
        return enumValues;
    }

    public String name() {
        return this.name;
    }
}

