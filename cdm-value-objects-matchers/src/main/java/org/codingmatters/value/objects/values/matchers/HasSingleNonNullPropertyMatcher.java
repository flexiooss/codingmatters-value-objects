package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.PropertyValue;

public class HasSingleNonNullPropertyMatcher extends HasNonNullPropertyMatcher {
    public HasSingleNonNullPropertyMatcher(String propertyName, PropertyValue.Type type) {
        super(propertyName, type, PropertyValue.Cardinality.SINGLE);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleProperty(String propertyName, PropertyValue.Type type) {
        return new HasSingleNonNullPropertyMatcher(propertyName, type);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleDoubleValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.DOUBLE);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleStringValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.STRING);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleBooleanValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.BOOLEAN);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleBytesValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.BYTES);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleDateValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.DATE);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleDateTimeValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.DATETIME);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleLongValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.LONG);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleTimeValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.TIME);
    }

    public static HasSingleNonNullPropertyMatcher hasSingleObjectValueProperty(String propertyName) {
        return new HasSingleNonNullPropertyMatcher(propertyName, PropertyValue.Type.OBJECT);
    }
}
