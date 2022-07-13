package org.codingmatters.value.objects.demo;

public interface ConformingValueExtension {
    String stringProperty();

    default String decoratedValue(String prefix, String postfix) {
        return String.format("%s%s%s", prefix, this.stringProperty(), postfix);
    }
}
