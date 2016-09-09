package org.codingmatters.tests.reflect;

import org.codingmatters.tests.reflect.utils.LambdaMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.*;
import static org.codingmatters.tests.reflect.utils.LambdaMatcher.match;

/**
 * Created by nelt on 9/8/16.
 */
public class MethodMatcher extends TypeSafeMatcher<Method> {

    static public MethodMatcher aMethod() {
        return new MethodMatcher();
    }

    static public MethodMatcher anInstanceMethod() {
        return new MethodMatcher().thatIsNotStatic();
    }

    static public MethodMatcher aStaticMethod() {
        return new MethodMatcher().thatIsStatic();
    }

    private final LinkedList<Matcher<Method>> matchers = new LinkedList<>();

    private MethodMatcher() {}

    public MethodMatcher named(String name) {
        this.matchers.add(match("method named " + name, item -> item.getName().equals(name)));
        return this;
    }


    public MethodMatcher thatIsStatic() {
        return this.addMatcher("static method", item -> isStatic(item.getModifiers()));
    }

    public MethodMatcher thatIsNotStatic() {
        return this.addMatcher("instance method", item -> ! isStatic(item.getModifiers()));
    }

    public MethodMatcher thatIsPublic() {
        return this.addMatcher("public method", item -> isPublic(item.getModifiers()));
    }

    public MethodMatcher thatIsPrivate() {
        return this.addMatcher("private method", item -> isPrivate(item.getModifiers()));
    }

    public MethodMatcher thatIsProtected() {
        return this.addMatcher("protected method", item -> isProtected(item.getModifiers()));
    }

    public MethodMatcher thatIsPackagePrivateMethod() {
        return this.addMatcher("package private method", item -> ! (isPublic(item.getModifiers()) || isPrivate(item.getModifiers()) || isProtected(item.getModifiers())));
    }

    public MethodMatcher withParameters(Class ... parameters) {
        String paramsSpec = Arrays.stream(parameters).map(aClass -> aClass.getName()).collect(Collectors.joining(", "));
        return this.addMatcher("method parameters are " + paramsSpec, item -> Arrays.equals(item.getParameterTypes(), parameters));
    }

    public MethodMatcher returning(Class aClass) {
        return this.addMatcher("method returns a " + aClass.getName(), item -> aClass.equals(item.getReturnType()));
    }

    public MethodMatcher returningVoid() {
        return this.returning(void.class);
    }



    private MethodMatcher addMatcher(String description, LambdaMatcher.Lambda<Method> lambda) {
        this.matchers.add(match(description, lambda));
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
