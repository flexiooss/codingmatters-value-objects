package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
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

    public static BooleanValueMatcher booleanValue(Boolean value) {
        return new BooleanValueMatcher(equalTo(value));
    }

    public static BooleanValueMatcher trueValue() {
        return new BooleanValueMatcher(equalTo(true));
    }

    public static BooleanValueMatcher falseValue() {
        return new BooleanValueMatcher(equalTo(false));
    }
}
