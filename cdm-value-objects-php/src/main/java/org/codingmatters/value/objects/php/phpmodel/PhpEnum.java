package org.codingmatters.value.objects.php.phpmodel;

public class PhpEnum {

    private final String packageName;
    private final String[] enumValues;
    private final String name;

    public PhpEnum( String name, String packageName, String... enumValues ) {
        this.name = name;
        this.packageName = packageName;
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

