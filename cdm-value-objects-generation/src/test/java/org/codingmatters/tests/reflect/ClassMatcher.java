package org.codingmatters.tests.reflect;

import org.codingmatters.tests.reflect.utils.LambdaMatcher;
import org.codingmatters.tests.reflect.utils.MatcherChain;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.reflect.Modifier.*;
import static org.codingmatters.tests.reflect.utils.LambdaMatcher.match;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatcher extends TypeSafeMatcher<Class> {

    static public ClassMatcher aClass() {
        return new ClassMatcher().addMatcher("class", item -> ! Modifier.isInterface(item.getModifiers()));
    }

    static public ClassMatcher anInterface() {
        return new ClassMatcher().addMatcher("interface", item -> Modifier.isInterface(item.getModifiers()));
    }

    private final MatcherChain<Class> matchers = new MatcherChain<>();

    private ClassMatcher() {}

    public ClassMatcher named(String name) {
        this.addMatcher("class named " + name, item -> item.getName().equals(name));
        return this;
    }

    public ClassMatcher with(MethodMatcher methodMatcher) {
        this.matchers.add(new ClassMemberMatcher<Method>(methodMatcher, item -> {
            List<Method> result = new LinkedList<>();
            result.addAll(Arrays.asList(item.getDeclaredMethods()));
            result.addAll(Arrays.asList(item.getMethods()));
            return result;
        }));
        return this;
    }

    public Matcher<Class> with(FieldMatcher fieldMatcher) {
        this.matchers.add(new ClassMemberMatcher<Field>(fieldMatcher, item -> {
            List<Field> result = new LinkedList<>();
            result.addAll(Arrays.asList(item.getDeclaredFields()));
            result.addAll(Arrays.asList(item.getFields()));
            return result;
        }));
        return this;
    }


    public ClassMatcher thatIsStatic() {
        return this.addMatcher("static method", item -> isStatic(item.getModifiers()));
    }

    public ClassMatcher thatIsNotStatic() {
        return this.addMatcher("instance method", item -> ! isStatic(item.getModifiers()));
    }

    public ClassMatcher thatIsPublic() {
        return this.addMatcher("public method", item -> isPublic(item.getModifiers()));
    }

    public ClassMatcher thatIsPrivate() {
        return this.addMatcher("private method", item -> isPrivate(item.getModifiers()));
    }

    public ClassMatcher thatIsProtected() {
        return this.addMatcher("protected method", item -> isProtected(item.getModifiers()));
    }

    public ClassMatcher thatIsPackagePrivateMethod() {
        return this.addMatcher("package private method", item -> ! (isPublic(item.getModifiers()) || isPrivate(item.getModifiers()) || isProtected(item.getModifiers())));
    }



    @Override
    protected boolean matchesSafely(Class aClass) {
        return this.matchers.compoundMatcher().matches(aClass);
    }

    @Override
    public void describeTo(Description description) {
        this.matchers.compoundMatcher().describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(Class item, Description mismatchDescription) {
        this.matchers.compoundMatcher().describeMismatch(item, mismatchDescription);
    }

    private ClassMatcher addMatcher(String description, LambdaMatcher.Lambda<Class> lambda) {
        this.matchers.add(match(description, lambda));
        return this;
    }

    static private class ClassMemberMatcher<T extends Member> extends TypeSafeMatcher<Class> {

        private final TypeSafeMatcher<T> methodMatcher;
        private final MemberCollector<T> memberCollector;

        private ClassMemberMatcher(TypeSafeMatcher<T> methodMatcher, MemberCollector<T> memberCollector) {
            this.methodMatcher = methodMatcher;
            this.memberCollector = memberCollector;
        }

        @Override
        protected boolean matchesSafely(Class item) {
            for (T method : this.memberCollector.candidates(item)) {
                if(this.methodMatcher.matches(method)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {

        }

        private interface MemberCollector<T extends Member> {
            List<T> candidates(Class item);
        }
    }

}
