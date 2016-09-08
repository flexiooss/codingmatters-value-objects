package org.codingmatters.tests.reflect;

import org.codingmatters.tests.reflect.utils.LambdaMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Method;
import java.util.LinkedList;

import static java.lang.reflect.Modifier.*;
import static org.codingmatters.tests.reflect.utils.LambdaMatcher.match;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcher extends TypeSafeMatcher<Method> {

    static public MethodMatcher aMethod() {
        return new MethodMatcher();
    }

    private final LinkedList<Matcher<Method>> matchers = new LinkedList<>();

    private MethodMatcher() {}

    public MethodMatcher named(String name) {
        this.matchers.add(match("method name is " + name, item -> item.getName().equals(name)));
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

    public MethodMatcher thatIsPublic() {
        return this.addMatcher("method is public", item -> isPublic(item.getModifiers()));
    }

    public MethodMatcher thatIsPrivate() {
        return this.addMatcher("method is private", item -> isPrivate(item.getModifiers()));
    }

    public MethodMatcher thatIsProtected() {
        return this.addMatcher("method is protected", item -> isProtected(item.getModifiers()));
    }

    public MethodMatcher thatIsPackagePrivateMethod() {
        return this.addMatcher("methode is package private", item -> ! (isPublic(item.getModifiers()) || isPrivate(item.getModifiers()) || isProtected(item.getModifiers())));
    }

    private MethodMatcher addMatcher(String description, LambdaMatcher.Lambda<Method> lambda) {
        this.matchers.add(match(description, lambda));
        return this;
    }
}
