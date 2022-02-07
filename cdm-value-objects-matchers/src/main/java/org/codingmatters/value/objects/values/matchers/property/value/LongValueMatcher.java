package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import static org.hamcrest.core.IsEqual.equalTo;

public final class LongValueMatcher extends ValueMatcher<Long> {
    public LongValueMatcher(Matcher<Long> matcher) {
        super(PropertyValue.Type.LONG, matcher);
    }

    @Override
    protected Long internalValue(PropertyValue.Value value) {
        return value.longValue();
    }

    @Factory
    public static LongValueMatcher longValue(Matcher<Long> value) {
        return new LongValueMatcher(value);
    }

    @Factory
    public static LongValueMatcher longValue(Long value) {
        return new LongValueMatcher(equalTo(value));
    }
}
