package org.codingmatters.tests.reflect.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by nelt on 9/7/16.
 */
public class TransformedMatcher<T> extends TypeSafeMatcher<T> {


    public interface Transformation<T> {
        Object transform(T o);
    }


    private String transaformationName;
    private final Transformation<T> transformation;
    private final Matcher matcher;

    public TransformedMatcher(Transformation<T> transformation, Matcher matcher) {
        this("transformed", transformation, matcher);
    }

    public TransformedMatcher(String transformationName, Transformation<T> transformation, Matcher matcher) {
        this.transaformationName = transformationName;
        this.transformation = transformation;
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(T t) {
        return this.matcher.matches(this.transformation.transform(t));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.transaformationName).appendText(" ");
        this.matcher.describeTo(description);
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {

        mismatchDescription
                .appendValue(item)
                .appendText(" ").appendText(this.transaformationName).appendText(" ")
                ;
        this.matcher.describeMismatch(this.transformation.transform(item), mismatchDescription);
    }
}