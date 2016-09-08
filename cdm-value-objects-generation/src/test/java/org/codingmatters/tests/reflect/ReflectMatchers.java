package org.codingmatters.tests.reflect;

/**
 * Created by nelt on 9/8/16.
 */
public class ReflectMatchers {
    static public ClassMatcher aClass() {
        return ClassMatcher.aClass();
    }
    static public MethodMatcher aMethod() {
        return MethodMatcher.aMethod();
    }
}
