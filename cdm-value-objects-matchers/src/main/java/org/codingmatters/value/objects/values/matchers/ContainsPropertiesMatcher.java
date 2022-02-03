package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;

public class ContainsPropertiesMatcher extends TypeSafeMatcher<ObjectValue> {
    private final Matcher<String[]> propertiesArrayMatcher;

    public ContainsPropertiesMatcher(String... properties) {
        this.propertiesArrayMatcher = arrayContainingInAnyOrder(properties);
    }

    @Override
    protected boolean matchesSafely(ObjectValue item) {
        return propertiesArrayMatcher.matches(item.propertyNames());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("object with properties ").appendDescriptionOf(this.propertiesArrayMatcher);
    }

    @Factory
    public static Matcher<ObjectValue> containsProperties(String... properties) {
        return new ContainsPropertiesMatcher(properties);
    }
}
