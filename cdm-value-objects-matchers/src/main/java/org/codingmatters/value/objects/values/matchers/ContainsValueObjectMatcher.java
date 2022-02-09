package org.codingmatters.value.objects.values.matchers;

import org.codingmatters.value.objects.values.ObjectValue;
import org.hamcrest.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.codingmatters.value.objects.values.matchers.HasNonNullPropertyWithValueMatcher.hasProperty;
import static org.codingmatters.value.objects.values.matchers.property.PropertyValueMatchers.withValue;
import static org.hamcrest.core.AllOf.allOf;

public class ContainsValueObjectMatcher<E, A> extends TypeSafeDiagnosingMatcher<A> {
    private final Matcher<ObjectValue> allPropertiesOfExpectedValueObject;

    public ContainsValueObjectMatcher(E expectedValueObject) throws AssertionError {
        this.allPropertiesOfExpectedValueObject = allPropertiesOf(convertExpectedValueObject(expectedValueObject));
    }

    @Override
    protected boolean matchesSafely(A item, Description mismatch) {
        return convertActualValue(item, mismatch).matching(allPropertiesOfExpectedValueObject, "was an object ");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an object ").appendDescriptionOf(allPropertiesOfExpectedValueObject);
    }

    private static Matcher<ObjectValue> allPropertiesOf(ObjectValue expectedValueObject) {
        final List<Matcher<? super ObjectValue>> properties = new ArrayList<>();
        for (String propertyName : expectedValueObject.propertyNames()) {
            properties.add(hasProperty(propertyName, withValue(expectedValueObject.property(propertyName))));
        }

        return allOf(properties);
    }

    private Condition<ObjectValue> convertActualValue(A item, Description mismatch) {
        try {
            final ObjectValue objectValue = convertValue(item);
            return Condition.matched(objectValue, mismatch);
        } catch (NoSuchMethodException e) {
            mismatch.appendText("was not a Valid ValueObject (No toMap Method found)");
            return Condition.notMatched();
        } catch (InvocationTargetException e) {
            mismatch.appendText("impossible to convert the expected ValidObject");
            return Condition.notMatched();
        } catch (IllegalAccessException e) {
            mismatch.appendText("unable to access toMap method");
            return Condition.notMatched();
        } catch (ClassCastException e) {
            mismatch.appendText("valueObject doesnt provide a valid convert Map");
            return Condition.notMatched();
        }
    }

    private static <T> ObjectValue convertValue(T item) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (ObjectValue.class.isAssignableFrom(item.getClass())) {
            return (ObjectValue) item;
        }

        final Method toMapMethod = item.getClass().getMethod("toMap");
        toMapMethod.setAccessible(true);
        final Map convertMap = (Map) toMapMethod.invoke(item);
        return ObjectValue.fromMap(convertMap).build();
    }

    private static <A> ObjectValue convertExpectedValueObject(A actualValue) throws AssertionError {
        try {
            return convertValue(actualValue);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AssertionError("Unable to convert actual value. It may not have an accessible toMap method", e);
        }
    }

    @Factory
    public static <T> Matcher<T> containsObjectValue(ObjectValue object) {
        return new ContainsValueObjectMatcher<ObjectValue, T>(object);
    }

    @Factory
    public static <T> Matcher<ObjectValue> containsValueObject(T valueObject) {
        return new ContainsValueObjectMatcher<T, ObjectValue>(valueObject);
    }

    @Factory
    public static Matcher<ObjectValue> containsAnotherObjectValue(ObjectValue objectValue) {
        return new ContainsValueObjectMatcher<ObjectValue, ObjectValue>(objectValue);
    }

    @Factory
    public static <A, E> Matcher<A> containsAnotherValueObject(E valueObject) {
        return new ContainsValueObjectMatcher<E, A>(valueObject);
    }
}
