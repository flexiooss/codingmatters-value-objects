package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.*;

import static org.hamcrest.core.IsEqual.equalTo;

public final class DoubleValueMatcher extends ValueMatcher<Double> {
    public DoubleValueMatcher(Matcher<Double> matcher) {
        super(PropertyValue.Type.DOUBLE, matcher);
    }

    @Override
    protected Double internalValue(PropertyValue.Value value) {
        return value.doubleValue();
    }

    public static DoubleValueMatcher doubleValue(Double value) {
        return new DoubleValueMatcher(equalTo(value));
    }

    public static DoubleValueMatcher doubleValue(Matcher<Double> value) {
        return new DoubleValueMatcher(value);
    }
}
