package org.codingmatters.tests.reflect;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

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
        this.matchers.add(
                match("method is public", item -> Modifier.isPublic(item.getModifiers()))
        );
        return this;
    }
}
