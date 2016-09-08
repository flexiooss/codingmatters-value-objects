package org.codingmatters.tests.reflect.utils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Created by nelt on 9/8/16.
 */
public class LambdaMatcher<T> extends BaseMatcher<T> {

    static public <T> LambdaMatcher<T> match(String description, Lambda<T> lamda) {
        return new LambdaMatcher<T>(description, lamda);
    }

    private final String description;
    private final Lambda lambda;

    public LambdaMatcher(String description, Lambda<T> lambda) {
        this.description = description;
        this.lambda = lambda;
    }

    @Override
    public boolean matches(Object item) {
        return lambda.matches(item);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }

    public interface Lambda<T> {
        boolean matches(T item);
    }
}
