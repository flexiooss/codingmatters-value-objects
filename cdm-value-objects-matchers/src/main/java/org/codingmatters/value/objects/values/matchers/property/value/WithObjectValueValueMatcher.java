package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;

public final class WithObjectValueValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final ObjectValue value;

    public WithObjectValueValueMatcher(ObjectValue value) {
        super(String.valueOf(value));
        this.value = value;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.objectValue().equals(this.value);
    }

    public static WithObjectValueValueMatcher objectValue(ObjectValue value) {
        return new WithObjectValueValueMatcher(value);
    }
}
