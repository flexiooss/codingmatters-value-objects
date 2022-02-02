package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.PropertyValue;

public class HasMultipleNonNullPropertyMatcher extends HasNonNullPropertyMatcher {
    public HasMultipleNonNullPropertyMatcher(String propertyName, PropertyValue.Type type) {
        super(propertyName, type, PropertyValue.Cardinality.MULTIPLE);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleProperty(String propertyName, PropertyValue.Type type) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, type);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleDoubleValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.DOUBLE);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleStringValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.STRING);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleBooleanValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.BOOLEAN);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleBytesValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.BYTES);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleDateValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.DATE);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleDateTimeValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.DATETIME);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleLongValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.LONG);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleTimeValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.TIME);
    }

    public static HasMultipleNonNullPropertyMatcher hasMultipleObjectValueProperty(String propertyName) {
        return new HasMultipleNonNullPropertyMatcher(propertyName, PropertyValue.Type.OBJECT);
    }
}
