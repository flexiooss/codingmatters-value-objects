package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.collection.IsIterableContainingInOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;

public class MultipleValuesInOrderMatcher extends TypeSafeDiagnosingMatcher<PropertyValue> {
    private final IsIterableContainingInOrder<PropertyValue.Value> iterableMatcher;
    private final List<Matcher<? super PropertyValue.Value>> matchers;

    public MultipleValuesInOrderMatcher(List<Matcher<? super PropertyValue.Value>> matchers) {
        this.iterableMatcher = new IsIterableContainingInOrder<>(matchers);
        this.matchers = matchers;
    }

    @Override
    protected boolean matchesSafely(PropertyValue item, Description mismatchDescription) {
        return PropertyValue.Cardinality.MULTIPLE.equals(item.cardinality())
                && ! item.isNullValue()
                && iterableMatcher.matches(asList(item.multiple()));
    }


    @Override
    public void describeTo(Description description) {
        description.appendList("[", ", ", "]", matchers);
    }

    @Factory
    public static MultipleValuesInOrderMatcher multipleInOrder(List<Matcher<? super PropertyValue.Value>> itemMatchers) {
        return new MultipleValuesInOrderMatcher(itemMatchers);
    }

    @Factory
    @SafeVarargs
    public static MultipleValuesInOrderMatcher multipleInOrder(Matcher<? super PropertyValue.Value>... itemMatchers) {
        return multipleInOrder(Arrays.asList(itemMatchers));
    }

    @Factory
    public static MultipleValuesInOrderMatcher multipleInOrder(PropertyValue.Value... items) {
        final List<Matcher<? super PropertyValue.Value>> itemMatchers = new ArrayList<>();
        if (items != null) {
            for (PropertyValue.Value value : items) {
                if (value != null) {
                    itemMatchers.add(equalTo(value));
                }
            }
        }

        return multipleInOrder(itemMatchers);
    }
}
