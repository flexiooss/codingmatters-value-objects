package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

public class ObjectValueMatchers {
    public static Matcher<ObjectValue> hasProperties(String... properties) {
        return org.codingmatters.value.objects.values.matchers.HasPropertiesMatcher.hasProperties(properties);
    }

    public static Matcher<ObjectValue> hasProperties(Matcher<? super String>... properties) {
        return org.codingmatters.value.objects.values.matchers.HasPropertiesMatcher.hasProperties(properties);
    }

    public static Matcher<ObjectValue> hasProperty(String property) {
        return org.codingmatters.value.objects.values.matchers.HasPropertiesMatcher.hasProperty(property);
    }

    public static Matcher<ObjectValue> hasProperty(String property, Matcher<PropertyValue> with) {
        return org.codingmatters.value.objects.values.matchers.HasNonNullPropertyWithValueMatcher.hasProperty(property, with);
    }

    public static Matcher<ObjectValue> hasNonNullProperty(String propertyName, PropertyValue.Type type, PropertyValue.Cardinality cardinality) {
        return org.codingmatters.value.objects.values.matchers.HasNonNullPropertyMatcher.hasNonNullProperty(propertyName, type, cardinality);
    }

    public static Matcher<ObjectValue> containsProperties(String... properties) {
        return org.codingmatters.value.objects.values.matchers.ContainsPropertiesMatcher.containsProperties(properties);
    }
}
