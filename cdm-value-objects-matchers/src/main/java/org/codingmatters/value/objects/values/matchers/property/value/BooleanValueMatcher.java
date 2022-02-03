package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

public final class BooleanValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Boolean value;

    public BooleanValueMatcher(Boolean value) {
        super(String.format("<%s (BOOLEAN)>", value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.BOOLEAN) && ! item.isNull() && item.booleanValue().equals(value);
    }

    public static BooleanValueMatcher booleanValue(Boolean value) {
        return new BooleanValueMatcher(value);
    }

    public static BooleanValueMatcher trueValue() {
        return new BooleanValueMatcher(true);
    }

    public static BooleanValueMatcher falseValue() {
        return new BooleanValueMatcher(false);
    }
}
