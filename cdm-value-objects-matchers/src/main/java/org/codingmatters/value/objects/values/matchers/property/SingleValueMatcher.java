package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class SingleValueMatcher extends TypeSafeMatcher<PropertyValue> {
    private final Matcher<PropertyValue.Value> valueMatcher;

    public SingleValueMatcher(Matcher<PropertyValue.Value> valueMatcher) {
        this.valueMatcher = valueMatcher;
    }

    @Override
    protected boolean matchesSafely(PropertyValue item) {
        return PropertyValue.Cardinality.SINGLE.equals(item.cardinality()) && ! item.isNullValue() && valueMatcher.matches(item.single());
    }

    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(valueMatcher);
    }

    public static SingleValueMatcher single(Matcher<PropertyValue.Value> valueMatcher) {
        return new SingleValueMatcher(valueMatcher);
    }
}
