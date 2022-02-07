package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import static org.hamcrest.core.IsEqual.equalTo;

public final class BooleanValueMatcher extends ValueMatcher<Boolean> {
    public BooleanValueMatcher(Matcher<Boolean> matcher) {
        super(PropertyValue.Type.BOOLEAN, matcher);
    }

    @Override
    protected Boolean internalValue(PropertyValue.Value value) {
        return value.booleanValue();
    }

    @Factory
    public static BooleanValueMatcher booleanValue(Boolean value) {
        return new BooleanValueMatcher(equalTo(value));
    }

    @Factory
    public static BooleanValueMatcher trueValue() {
        return new BooleanValueMatcher(equalTo(true));
    }

    @Factory
    public static BooleanValueMatcher falseValue() {
        return new BooleanValueMatcher(equalTo(false));
    }
}
