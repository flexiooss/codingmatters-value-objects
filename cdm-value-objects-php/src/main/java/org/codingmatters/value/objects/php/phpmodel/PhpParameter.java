package org.codingmatters.value.objects.php.phpmodel;

public class PhpParameter {

    private String name;
    private String type;

    public PhpParameter( String name, String type ) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return this.name;
    }

    public String type() {
        return this.type;
    }

}


