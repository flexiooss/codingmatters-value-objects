package org.codingmatters.value.objects.values.matchers.optional;

import org.hamcrest.Condition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.beans.PropertyUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.Condition.matched;
import static org.hamcrest.Condition.notMatched;
import static org.hamcrest.beans.PropertyUtil.NO_ARGUMENTS;

public class Matchers {
    public static <T> org.hamcrest.Matcher<T> isPresent() {
        return new IsPresent<>();
    }

    private static class IsPresent<T> extends TypeSafeDiagnosingMatcher<T> {
        @Override
        public boolean matchesSafely(T bean, Description mismatch) {
            return propertyOn(bean,"present", mismatch)
                    .and(Matchers::withReadMethod)
                    .and(withPropertyValue(bean))
                    .matching(IS_VALID_MATCHER, mismatchMessage);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("isPresent()");
        }
    }

    public static <T> org.hamcrest.Matcher<T> isEmpty() {
        return new IsEmpty<>();
    }

    private static class IsEmpty<T> extends TypeSafeDiagnosingMatcher<T> {
        @Override
        public boolean matchesSafely(T bean, Description mismatch) {
            return propertyOn(bean,"empty", mismatch)
                    .and(Matchers::withReadMethod)
                    .and(withPropertyValue(bean))
                    .matching(IS_VALID_MATCHER, mismatchMessage);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("isEmpty()");
        }
    }

    private static <T> Condition<PropertyDescriptor> propertyOn(T bean, String propertyName, Description mismatch) {
        PropertyDescriptor property = PropertyUtil.getPropertyDescriptor(propertyName, bean);
        if (property == null) {
            mismatch.appendText("Not a valid Optional Class");
            return notMatched();
        }

        return matched(property, mismatch);
    }

    private static final Matcher<Object> IS_VALID_MATCHER = org.hamcrest.core.IsEqual.equalTo(true);
    private static final String mismatchMessage = "Not a valid Optional Class";

    private static <T> Condition.Step<Method, Object> withPropertyValue(final T bean) {
        return (readMethod, mismatch) -> {
            try {
                return matched(readMethod.invoke(bean, NO_ARGUMENTS), mismatch);
            } catch (InvocationTargetException e) {
                mismatch.appendText(mismatchMessage);
                return notMatched();
            } catch (Exception e) {
                throw new IllegalStateException("Calling: '" + readMethod + "' should not have thrown " + e);
            }
        };
    }

    private static Condition<Method> withReadMethod(PropertyDescriptor property, Description mismatch) {
        final Method readMethod = property.getReadMethod();
        if (null == readMethod) {
            mismatch.appendText("Not a valid Optional Class");
            return notMatched();
        }
        return matched(readMethod, mismatch);
    }
}
