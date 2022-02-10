package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AllOf.allOf;

public class HasPropertiesMatcher extends TypeSafeDiagnosingMatcher<ObjectValue> {
    private final Matcher<? super String> propertyMatcher;

    public HasPropertiesMatcher(Matcher<? super String> matcher) {
        this.propertyMatcher = matcher;
    }

    @Override
    protected boolean matchesSafely(ObjectValue value, Description mismatch) {
        boolean isPastFirst = false;
        for (String property : value.propertyNames()) {
            if (propertyMatcher.matches(property)){
                return true;
            }
            if (isPastFirst) {
                mismatch.appendText(", ");
            }
            propertyMatcher.describeMismatch(value, mismatch);
            isPastFirst = true;
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("object with properties ").appendDescriptionOf(this.propertyMatcher);
    }

    @SafeVarargs
    public static Matcher<ObjectValue> hasProperties(Matcher<? super String>... properties) {
        List<Matcher<? super ObjectValue>> all = new ArrayList<>(properties.length);

        for (Matcher<? super String> property : properties) {
            all.add(new HasPropertiesMatcher(property));
        }

        return allOf(all);
    }

    public static Matcher<ObjectValue> hasProperties(String... properties) {
        List<Matcher<? super ObjectValue>> all = new ArrayList<>(properties.length);
        for (String property : properties) {
            all.add(hasProperty(property));
        }

        return allOf(all);
    }

    public static HasPropertiesMatcher hasProperty(String property) {
        return new HasPropertiesMatcher(equalTo(property));
    }
}
