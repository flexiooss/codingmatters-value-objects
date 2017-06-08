package org.codingmatters.value.objects.spec;

/**
 * Created by nelt on 9/5/16.
 */
public enum TypeKind {
    JAVA_TYPE, EXTERNAL_VALUE_OBJECT, IN_SPEC_VALUE_OBJECT, EMBEDDED, ENUM;

    public boolean isValueObject() {
        return this.equals(EXTERNAL_VALUE_OBJECT) || this.equals(IN_SPEC_VALUE_OBJECT);
    }
}
