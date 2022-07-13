package org.codingmatters.value.objects.demo;

public interface ConformingValueBuilderExtension {
    default ConformingValue.Builder initialzeStringPropWithPlok() {
        return ((ConformingValue.Builder)this).stringProperty("plok");
    }
}
