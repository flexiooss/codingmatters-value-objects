package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class MultipleValuesInAnyOrderMatcher extends TypeSafeDiagnosingMatcher<PropertyValue> {
    private final IsIterableContainingInAnyOrder<PropertyValue.Value> iterableMatcher;
    private final Collection<Matcher<? super PropertyValue.Value>> matchers;

    public MultipleValuesInAnyOrderMatcher(Collection<Matcher<? super PropertyValue.Value>> matchers) {
        this.iterableMatcher = new IsIterableContainingInAnyOrder<>(matchers);
        this.matchers = matchers;
    }

    @Override
    protected boolean matchesSafely(PropertyValue item, Description mismatchDescription) {
        return PropertyValue.Cardinality.MULTIPLE.equals(item.cardinality())
                && ! item.isNullValue()
                && iterableMatcher.matches(Arrays.asList(item.multiple()));
    }

    @Override
    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", matchers).appendText(" in any order");
    }

    @Factory
    public static MultipleValuesInAnyOrderMatcher multiple(Collection<Matcher<? super PropertyValue.Value>> itemMatchers) {
        return new MultipleValuesInAnyOrderMatcher(itemMatchers);
    }

    @Factory
    @SafeVarargs
    public static MultipleValuesInAnyOrderMatcher multiple(Matcher<? super PropertyValue.Value>... itemMatchers) {
        return multiple(Arrays.asList(itemMatchers));
    }

    @Factory
    public static MultipleValuesInAnyOrderMatcher multiple(PropertyValue.Value... items) {
        final List<Matcher<? super PropertyValue.Value>> itemMatchers = new ArrayList<>();
        if (items != null) {
            for (PropertyValue.Value value : items) {
                if (value != null) {
                    itemMatchers.add(equalTo(value));
                }
            }
        }

        return multiple(itemMatchers);
    }
}
