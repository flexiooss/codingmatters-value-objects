package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.*;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class ContainsMultipleValuesInAnyOrderMatcher extends TypeSafeDiagnosingMatcher<PropertyValue> {
    private final IsIterableContainingInAnyOrder<PropertyValue.Value> iterableMatcher;
    private final Collection<Matcher<? super PropertyValue.Value>> matchers;

    public ContainsMultipleValuesInAnyOrderMatcher(Collection<Matcher<? super PropertyValue.Value>> matchers) {
        this.iterableMatcher = new IsIterableContainingInAnyOrder<>(matchers);
        this.matchers = matchers;
    }

    @Override
    protected boolean matchesSafely(PropertyValue item, Description mismatch) {
        return isMultiple(item, mismatch).and(this::itemIsNotNull).matching(iterableMatcher);
    }

    private static Condition<PropertyValue> isMultiple(PropertyValue item, Description mismatch) {
        if (PropertyValue.Cardinality.MULTIPLE.equals(item.cardinality())) {
            return Condition.matched(item, mismatch);
        }

        mismatch.appendText("is not multiple");
        return Condition.notMatched();
    }

    private Condition<Iterable<? extends PropertyValue.Value>> itemIsNotNull(PropertyValue item, Description mismatch) {
        if (item.isNullValue()) {
            mismatch.appendText("is null");
            return Condition.notMatched();
        }

        return Condition.matched(Arrays.asList(item.multiple()), mismatch);
    }

    @Override
    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", matchers).appendText(" in any order");
    }

    @Factory
    public static ContainsMultipleValuesInAnyOrderMatcher multiple(Collection<Matcher<? super PropertyValue.Value>> itemMatchers) {
        return new ContainsMultipleValuesInAnyOrderMatcher(itemMatchers);
    }

    @Factory
    @SafeVarargs
    public static ContainsMultipleValuesInAnyOrderMatcher multiple(Matcher<? super PropertyValue.Value>... itemMatchers) {
        return multiple(Arrays.asList(itemMatchers));
    }

    @Factory
    public static ContainsMultipleValuesInAnyOrderMatcher multiple(PropertyValue.Value... items) {
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
