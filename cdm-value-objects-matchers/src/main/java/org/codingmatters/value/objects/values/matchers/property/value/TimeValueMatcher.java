package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.time.LocalTime;

import static org.hamcrest.core.IsEqual.equalTo;

public final class TimeValueMatcher extends ValueMatcher<LocalTime> {
    public TimeValueMatcher(Matcher<LocalTime> matcher) {
        super(PropertyValue.Type.TIME, matcher);
    }

    @Override
    protected LocalTime internalValue(PropertyValue.Value value) {
        return value.timeValue();
    }

    @Factory
    public static TimeValueMatcher timeValue(Matcher<LocalTime> value) {
        return new TimeValueMatcher(value);
    }

    @Factory
    public static TimeValueMatcher timeValue(LocalTime value) {
        return new TimeValueMatcher(equalTo(value));
    }
}
