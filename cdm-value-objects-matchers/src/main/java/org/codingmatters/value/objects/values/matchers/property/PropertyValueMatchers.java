package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.codingmatters.value.objects.values.matchers.property.value.ValueMatchers;
import org.hamcrest.Matcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static org.codingmatters.value.objects.values.matchers.property.value.ValueMatchers.*;

public final class PropertyValueMatchers {
    public static Matcher<PropertyValue> withSingle(Matcher<PropertyValue.Value> value) {
        return org.codingmatters.value.objects.values.matchers.property.SingleValueMatcher.single(value);
    }

    public static Matcher<PropertyValue> withMultiple(Matcher<PropertyValue.Value>... values) {
        return org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInAnyOrderMatcher.multiple(values);
    }

    public static Matcher<PropertyValue> withMultiple(PropertyValue.Value... values) {
        return org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInAnyOrderMatcher.multiple(values);
    }

    public static Matcher<PropertyValue> withMultiple(Collection<Matcher<? super PropertyValue.Value>> values) {
        return org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInAnyOrderMatcher.multiple(values);
    }

    public static Matcher<PropertyValue> withMultipleOrdered(Matcher<PropertyValue.Value>... values) {
        return org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInOrderMatcher.multipleInOrder(values);
    }

    public static Matcher<PropertyValue> withMultipleOrdered(PropertyValue.Value... values) {
        return org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInOrderMatcher.multipleInOrder(values);
    }

    public static Matcher<PropertyValue> withMultipleOrdered(List<Matcher<? super PropertyValue.Value>> values) {
        return org.codingmatters.value.objects.values.matchers.property.ContainsMultipleValuesInOrderMatcher.multipleInOrder(values);
    }

    public static Matcher<PropertyValue> withValue(String str) {
        return withSingle(stringValue(str));
    }

    public static Matcher<PropertyValue> withStringValue(Matcher<String> matcher) {
        return withSingle(stringValue(matcher));
    }

    public static Matcher<PropertyValue> withValue(Boolean bool) {
        return withSingle(booleanValue(bool));
    }

    public static Matcher<PropertyValue> withValue(Double d) {
        return withSingle(doubleValue(d));
    }

    public static Matcher<PropertyValue> withDoubleValue(Matcher<Double> d) {
        return withSingle(doubleValue(d));
    }

    public static Matcher<PropertyValue> withValue(Long l) {
        return withSingle(longValue(l));
    }

    public static Matcher<PropertyValue> withLongValue(Matcher<Long> l) {
        return withSingle(longValue(l));
    }

    public static Matcher<PropertyValue> withValue(LocalDateTime dateTime) {
        return withSingle(dateTimeValue(dateTime));
    }

    public static Matcher<PropertyValue> withValue(LocalDate date) {
        return withSingle(dateValue(date));
    }
    public static Matcher<PropertyValue> withDateValue(Matcher<LocalDate> date) {
        return withSingle(dateValue(date));
    }

    public static Matcher<PropertyValue> withValue(LocalTime time) {
        return withSingle(timeValue(time));
    }

    public static Matcher<PropertyValue> withTimeValue(Matcher<LocalTime> time) {
        return withSingle(timeValue(time));
    }

    public static Matcher<PropertyValue> withValue(byte[] bytes) {
        return withSingle(bytesValue(bytes));
    }

    public static Matcher<PropertyValue> withBytesValue(Matcher<byte[]> bytes) {
        return withSingle(bytesValue(bytes));
    }

    public static Matcher<PropertyValue> withValue(ObjectValue value) {
        return withSingle(objectValue(value));
    }

    public static Matcher<PropertyValue> withObjectValue(Matcher<ObjectValue> matcher) {
        return withSingle(objectValue(matcher));
    }


    private static <E> PropertyValue.Value[] buildValues(Collection<E> values, Function<E, PropertyValue.Builder> toBuilder) {
        return values.stream()
                .map(toBuilder).map(PropertyValue.Builder::buildValue)
                .toArray(PropertyValue.Value[]::new);
    }

    private static <E> Matcher<PropertyValue> withValuesInAnyOrder(Collection<E> values, Function<E, PropertyValue.Builder> toBuilder) {
        final PropertyValue.Value[] valuesArray = buildValues(values, toBuilder);
        return withMultiple(valuesArray);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(String... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::stringValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(Boolean... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::booleanValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(byte[]... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::bytesValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(LocalDateTime... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::datetimeValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(LocalDate... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::dateValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(LocalTime... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::timeValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(Double... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::doubleValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(Long... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::longValue);
    }

    public static Matcher<PropertyValue> withValuesInAnyOrder(ObjectValue... values) {
        return withValuesInAnyOrder(Arrays.asList(values), PropertyValue.builder()::objectValue);
    }

    private static <E> Matcher<PropertyValue> withValuesInOrder(Collection<E> values, Function<E, PropertyValue.Builder> toBuilder) {
        final PropertyValue.Value[] valuesArray = buildValues(values, toBuilder);
        return withMultipleOrdered(valuesArray);
    }

    public static Matcher<PropertyValue> withValues(String... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::stringValue);
    }

    public static Matcher<PropertyValue> withValues(Boolean... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::booleanValue);
    }

    public static Matcher<PropertyValue> withValues(byte[]... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::bytesValue);
    }

    public static Matcher<PropertyValue> withValues(LocalDateTime... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::datetimeValue);
    }

    public static Matcher<PropertyValue> withValues(LocalDate... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::dateValue);
    }

    public static Matcher<PropertyValue> withValues(LocalTime... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::timeValue);
    }

    public static Matcher<PropertyValue> withValues(Double... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::doubleValue);
    }

    public static Matcher<PropertyValue> withValues(Long... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::longValue);
    }

    public static Matcher<PropertyValue> withValues(ObjectValue... values) {
        return withValuesInOrder(Arrays.asList(values), PropertyValue.builder()::objectValue);
    }

    @SafeVarargs
    public static Matcher<PropertyValue> withValues(Matcher<ObjectValue>... values) {
        List<Matcher<? super PropertyValue.Value>> valuesMatchers = new ArrayList<>();
        for (Matcher<ObjectValue> matcher : values) {
            if (matcher != null) {
                valuesMatchers.add(ValueMatchers.objectValue(matcher));
            }
        }
        return withMultipleOrdered(valuesMatchers);
    }
}
