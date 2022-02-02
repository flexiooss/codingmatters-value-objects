package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.equalTo;

public final class ObjectValueMatcher extends CustomTypeSafeMatcher<PropertyValue.Value> {
    private final Matcher<ObjectValue> valueMatcher;

    public ObjectValueMatcher(ObjectValue value) {
        super(String.valueOf(value));
        this.valueMatcher = equalTo(value);
    }

    public ObjectValueMatcher(Matcher<ObjectValue> valueMatcher) {
        super("ObjectValue ");
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return valueMatcher.matches(item.objectValue());
    }

    public static ObjectValueMatcher objectValue(ObjectValue value) {
        return new ObjectValueMatcher(value);
    }

    public static ObjectValueMatcher objectValueMatching(Matcher<ObjectValue> matcher) {
        return new ObjectValueMatcher(matcher);
    }
}
