package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

import java.time.LocalDate;

import static org.hamcrest.core.IsEqual.equalTo;

public final class DateValueMatcher extends ValueMatcher<LocalDate> {
    public DateValueMatcher(Matcher<LocalDate> matcher) {
        super(PropertyValue.Type.DATE, matcher);
    }

    @Override
    protected LocalDate internalValue(PropertyValue.Value value) {
        return value.dateValue();
    }

    public static DateValueMatcher dateValue(Matcher<LocalDate> value) {
        return new DateValueMatcher(value);
    }

    public static DateValueMatcher dateValue(LocalDate value) {
        return new DateValueMatcher(equalTo(value));
    }
}
