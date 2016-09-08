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

    static public ClassMatcher isAClass() {
        return new ClassMatcher();
    }

    private final LinkedList<Matcher> matchers = new LinkedList<>();

    private ClassMatcher() {}

    public ClassMatcher withName(String name) {
        this.matchers.add(new TransformedMatcher<Class>(
                "class name",
                o -> o.getName(),
                Matchers.is(name)));
        return this;
    }

    @Override
    protected boolean matchesSafely(Class aClass) {
        return this.compoundMatcher().matches(aClass);
    }

    @Override
    public void describeTo(Description description) {
        this.compoundMatcher().describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Class item, Description mismatchDescription) {
        this.compoundMatcher().describeMismatch(item, mismatchDescription);
    }

    private Matcher<Object> compoundMatcher() {
        return Matchers.allOf(this.matchers.toArray(new Matcher[this.matchers.size()]));
    }
}
