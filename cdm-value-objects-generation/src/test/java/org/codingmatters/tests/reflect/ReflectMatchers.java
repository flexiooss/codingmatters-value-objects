package org.codingmatters.tests.reflect;

/**
 * Created by nelt on 9/8/16.
 */
public class ReflectMatchers {
    static public ClassMatcher isAClass() {
        return ClassMatcher.isAClass();
    }
    static public MethodMatcher isAMethod() {
        return MethodMatcher.isAMethod();
    }
}
