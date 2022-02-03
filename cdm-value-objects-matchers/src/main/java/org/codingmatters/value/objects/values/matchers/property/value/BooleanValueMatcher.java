package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Factory;

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

    @Factory
    public static BooleanValueMatcher booleanValue(Boolean value) {
        return new BooleanValueMatcher(value);
    }

    @Factory
    public static BooleanValueMatcher trueValue() {
        return new BooleanValueMatcher(true);
    }

    @Factory
    public static BooleanValueMatcher falseValue() {
        return new BooleanValueMatcher(false);
    }
}
