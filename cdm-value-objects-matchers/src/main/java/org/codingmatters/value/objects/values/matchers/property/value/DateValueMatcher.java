package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

import java.time.LocalDate;

public final class DateValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final LocalDate value;

    public DateValueMatcher(LocalDate value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.DATE) && ! item.isNull() && item.dateValue().equals(this.value);
    }

    public static DateValueMatcher dateValue(LocalDate value) {
        return new DateValueMatcher(value);
    }
}
