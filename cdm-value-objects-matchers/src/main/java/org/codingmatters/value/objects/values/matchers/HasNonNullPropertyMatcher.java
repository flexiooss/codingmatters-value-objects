package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

public class HasNonNullPropertyMatcher extends TypeSafeMatcher<ObjectValue> {
    private final String propertyName;
    private final PropertyValue.Type type;
    private final PropertyValue.Cardinality cardinality;

    public HasNonNullPropertyMatcher(String propertyName, PropertyValue.Type type, PropertyValue.Cardinality cardinality) {
        this.propertyName = propertyName;
        this.type = type;
        this.cardinality = cardinality;
    }

    @Override
    protected boolean matchesSafely(ObjectValue item) {
        return item.nonNullProperty(propertyName, type, cardinality).isPresent();
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(propertyName)
                .appendText(" [").appendValue(cardinality).appendText("]")
                .appendText(" (").appendValue(type).appendText(")");
    }

    @Factory
    public static HasNonNullPropertyMatcher hasNonNullProperty(String propertyName, PropertyValue.Type type, PropertyValue.Cardinality cardinality) {
        return new HasNonNullPropertyMatcher(propertyName, type, cardinality);
    }
}
