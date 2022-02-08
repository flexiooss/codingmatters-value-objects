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

public class ContainsValueObjectMatcher<T> extends TypeSafeDiagnosingMatcher<ObjectValue> {
    private final T expectedValueObject;

    public ContainsValueObjectMatcher(T expectedValueObject) {
        this.expectedValueObject = expectedValueObject;
    }

    @Override
    protected boolean matchesSafely(ObjectValue item, Description mismatch) {
        return convertExpectedValue(mismatch).matching(allPropertiesOf(item), "object ");
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ObjectValue must contain ").appendValue(expectedValueObject);
    }

    private Matcher<ObjectValue> allPropertiesOf(ObjectValue actual) {
        final List<Matcher<? super ObjectValue>> properties = new ArrayList<>();
        for (String propertyName : actual.propertyNames()) {
            properties.add(hasProperty(propertyName, withValue(actual.property(propertyName))));
        }

        return allOf(properties);
    }

    private <T> Condition<ObjectValue> convertExpectedValue(Description mismatch) {
        if (ObjectValue.class.isAssignableFrom(expectedValueObject.getClass())) {
            return Condition.matched((ObjectValue) expectedValueObject, mismatch);
        }

        final ObjectValue objectValue;
        try {
            final Method toMapMethod = expectedValueObject.getClass().getMethod("toMap");;
            final Map convertMap = (Map) toMapMethod.invoke(expectedValueObject);
            objectValue = ObjectValue.fromMap(convertMap).build();
        } catch (NoSuchMethodException e) {
            mismatch.appendText("was not a Valid ValueObject (No toMap Method found)");
            return Condition.notMatched();
        } catch (InvocationTargetException e) {
            mismatch.appendText("impossible to map ValidObject to ObjectValue");
            return Condition.notMatched();
        } catch (IllegalAccessException e) {
            mismatch.appendText("unable to access toMap method");
            return Condition.notMatched();
        } catch (ClassCastException e) {
            mismatch.appendText("valueObject doesnt provide a valid convert Map");
            return Condition.notMatched();
        }

        return Condition.matched(objectValue, mismatch);
    }

    @Factory
    public static <T> Matcher<ObjectValue> containsObject(T object) {
        return new ContainsValueObjectMatcher<T>(object);
    }
}
