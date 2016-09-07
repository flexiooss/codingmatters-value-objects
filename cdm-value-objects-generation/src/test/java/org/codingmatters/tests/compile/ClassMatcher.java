package org.codingmatters.tests.compile;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.util.LinkedList;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatcher extends TypeSafeMatcher<Class> {

    private final LinkedList<Matcher> matchers = new LinkedList<>();
    private String name;

    public ClassMatcher() {
    }

    public ClassMatcher withName(String name) {
        this.matchers.add(new TransformedMatcher<Class>(
                "classname",
                o -> o.getName(),
                Matchers.is(name)));
        this.name = name;
        return this;
    }

    @Override
    protected boolean matchesSafely(Class aClass) {
        return this.compoundMatcher().matches(aClass);
    }

    private Matcher<Object> compoundMatcher() {
        return Matchers.allOf(this.matchers.toArray(new Matcher[this.matchers.size()]));
    }

    @Override
    public void describeTo(Description description) {
        this.compoundMatcher().describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Class item, Description mismatchDescription) {
        this.compoundMatcher().describeMismatch(item, mismatchDescription);
    }
}
