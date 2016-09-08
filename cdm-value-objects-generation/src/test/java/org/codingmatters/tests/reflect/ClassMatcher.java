package org.codingmatters.tests.reflect;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Method;
import java.util.LinkedList;

import static org.codingmatters.tests.reflect.utils.LambdaMatcher.match;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatcher extends TypeSafeMatcher<Class> {

    static public ClassMatcher aClass() {
        return new ClassMatcher();
    }

    private final LinkedList<Matcher<Class>> matchers = new LinkedList<>();

    private ClassMatcher() {}

    public ClassMatcher named(String name) {
        this.matchers.add(match("class name is " + name, item -> item.getName().equals(name)));
        return this;
    }

    public ClassMatcher with(MethodMatcher methodMatcher) {
        this.matchers.add(new ClassWithMatchingMethodMatcher(methodMatcher));
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

    private class ClassWithMatchingMethodMatcher extends TypeSafeMatcher<Class> {

        private final MethodMatcher methodMatcher;

        private ClassWithMatchingMethodMatcher(MethodMatcher methodMatcher) {
            this.methodMatcher = methodMatcher;
        }

        @Override
        protected boolean matchesSafely(Class item) {
            for (Method method : item.getDeclaredMethods()) {
                if(this.methodMatcher.matches(method)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {

        }
    }
}
