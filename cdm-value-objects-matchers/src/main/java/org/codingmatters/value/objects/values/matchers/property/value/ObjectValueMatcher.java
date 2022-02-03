package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.equalTo;

public final class ObjectValueMatcher extends TypeSafeMatcher<PropertyValue.Value> {
    private final Matcher<ObjectValue> valueMatcher;

    public ObjectValueMatcher(ObjectValue value) {
        this(equalTo(value));
    }

    public ObjectValueMatcher(Matcher<ObjectValue> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(PropertyValue.Value item) {
        return item.isa(PropertyValue.Type.OBJECT) && ! item.isNull() && valueMatcher.matches(item.objectValue());
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(this.valueMatcher);
    }

    @Factory
    public static ObjectValueMatcher objectValue(ObjectValue value) {
        return new ObjectValueMatcher(value);
    }

    @Factory
    public static ObjectValueMatcher objectValueMatching(Matcher<ObjectValue> matcher) {
        return new ObjectValueMatcher(matcher);
    }
}
