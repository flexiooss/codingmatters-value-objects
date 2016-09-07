package org.codingmatters.tests.compile;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Created by nelt on 9/6/16.
 */
public class ClassMatcher extends BaseMatcher<Class> {

    private String name;

    public ClassMatcher() {
    }

    public ClassMatcher withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean matches(Object o) {
        return ((Class)o).getName().equals(this.name);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("class named ").appendValue(this.name);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description
                .appendText("class was named ").appendText(((Class)item).getName())
        ;
    }
}
