package org.codingmatters.tests.compile;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatchers {

    static public ClassMatcher classNamed(String name) {
        return new ClassMatcher(name);
    }

    static public Matcher<Class> hasMethod(String name) {
        return new BaseMatcher<Class>() {
            @Override
            public boolean matches(Object o) {
                Class clazz = (Class) o;
                for (Method method : clazz.getMethods()) {
                    if(method.getName().equals(name)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("class has method named ").appendValue(name);
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                description
                        .appendText("class has the following methods ")
                        .appendText(
                                Arrays.asList(((Class) item).getMethods()).stream().map(Method::getName).collect(Collectors.joining(", "))
                        );
            }
        };
    }

}
