package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import static org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInAnyOrderMatcher.multiple;
import static org.codingmatters.value.objects.values.matchers.property.SingleValueMatcher.single;
import static org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.booleanValue;
import static org.codingmatters.value.objects.values.matchers.property.value.BytesValueMatcher.bytesValue;
import static org.codingmatters.value.objects.values.matchers.property.value.DateTimeValueMatcher.dateTimeValue;
import static org.codingmatters.value.objects.values.matchers.property.value.DateValueMatcher.dateValue;
import static org.codingmatters.value.objects.values.matchers.property.value.DoubleValueMatcher.doubleValue;
import static org.codingmatters.value.objects.values.matchers.property.value.LongValueMatcher.longValue;
import static org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValue;
import static org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValueMatching;
import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValue;
import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValueMatching;
import static org.codingmatters.value.objects.values.matchers.property.value.TimeValueMatcher.timeValue;

public final class PropertyValueMatchers {
    public static Matcher<PropertyValue> withValue(String str) {
        return single(stringValue(str));
    }

    public static Matcher<PropertyValue> withStringValueMatching(Matcher<String> matcher) {
        return single(stringValueMatching(matcher));
    }

    public static Matcher<PropertyValue> withValue(Boolean bool) {
        return single(booleanValue(bool));
    }

    public static Matcher<PropertyValue> withValue(Double d) {
        return single(doubleValue(d));
    }

    public static Matcher<PropertyValue> withValue(Long l) {
        return single(longValue(l));
    }

    public static Matcher<PropertyValue> withValue(LocalDateTime dateTime) {
        return single(dateTimeValue(dateTime));
    }

    public static Matcher<PropertyValue> withValue(LocalDate date) {
        return single(dateValue(date));
    }

    public static Matcher<PropertyValue> withValue(LocalTime time) {
        return single(timeValue(time));
    }

    public static Matcher<PropertyValue> withValue(byte[] bytes) {
        return single(bytesValue(bytes));
    }

    public static Matcher<PropertyValue> withValue(ObjectValue value) {
        return single(objectValue(value));
    }

    public static Matcher<PropertyValue> withObjectMatching(Matcher<ObjectValue> matcher) {
        return single(objectValueMatching(matcher));
    }


    private static <E> Matcher<PropertyValue> withValues(Collection<E> values, Function<E, PropertyValue.Builder> toBuilder) {
        final PropertyValue.Value[] valuesArray = values.stream()
                .map(toBuilder).map(PropertyValue.Builder::buildValue)
                .toArray(PropertyValue.Value[]::new);
        return multiple(valuesArray);
    }

    public static Matcher<PropertyValue> withValues(String... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::stringValue);
    }

    public static Matcher<PropertyValue> withValues(Boolean... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::booleanValue);
    }

    public static Matcher<PropertyValue> withValues(byte[]... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::bytesValue);
    }

    public static Matcher<PropertyValue> withValues(LocalDateTime... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::datetimeValue);
    }

    public static Matcher<PropertyValue> withValues(LocalDate... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::dateValue);
    }

    public static Matcher<PropertyValue> withValues(LocalTime... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::timeValue);
    }

    public static Matcher<PropertyValue> withValues(Double... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::doubleValue);
    }

    public static Matcher<PropertyValue> withValues(Long... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::longValue);
    }

    public static Matcher<PropertyValue> withValues(ObjectValue... values) {
        return withValues(Arrays.asList(values), PropertyValue.builder()::objectValue);
    }
}
