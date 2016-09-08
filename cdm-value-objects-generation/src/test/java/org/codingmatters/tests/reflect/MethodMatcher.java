package org.codingmatters.tests.reflect;

import org.codingmatters.tests.reflect.utils.TransformedMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcher extends TypeSafeMatcher<Method> {

    static public MethodMatcher isAMethod() {
        return new MethodMatcher();
    }

    private final LinkedList<Matcher> matchers = new LinkedList<>();

    private MethodMatcher() {}

    public MethodMatcher withName(String name) {
        this.matchers.add(new TransformedMatcher<Method>(
                "method name",
                o -> o.getName(),
                Matchers.is(name)));
        return this;
    }

    @Override
    protected boolean matchesSafely(Method aMethod) {
        return this.compoundMatcher().matches(aMethod);
    }

    @Override
    public void describeTo(Description description) {
        this.compoundMatcher().describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Method item, Description mismatchDescription) {
        this.compoundMatcher().describeMismatch(item, mismatchDescription);
    }

    private Matcher<Object> compoundMatcher() {
        return Matchers.allOf(this.matchers.toArray(new Matcher[this.matchers.size()]));
    }
}
