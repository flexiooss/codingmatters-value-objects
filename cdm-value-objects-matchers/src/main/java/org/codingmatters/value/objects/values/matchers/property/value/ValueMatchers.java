package org.codingmatters.value.objects.values.matchers.property.value;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ValueMatchers {
    public static Matcher<PropertyValue.Value> booleanValue(Boolean value) {
        return org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.booleanValue(value);
    }

    public static Matcher<PropertyValue.Value> trueValue() {
        return org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.trueValue();
    }

    public static Matcher<PropertyValue.Value> falseValue() {
        return org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.falseValue();
    }

    public static Matcher<PropertyValue.Value> bytesValue(byte[] value) {
        return org.codingmatters.value.objects.values.matchers.property.value.BytesValueMatcher.bytesValue(value);
    }

    public static Matcher<PropertyValue.Value> dateTimeValue(LocalDateTime value) {
        return org.codingmatters.value.objects.values.matchers.property.value.DateTimeValueMatcher.dateTimeValue(value);
    }

    public static Matcher<PropertyValue.Value> dateValue(LocalDate value) {
        return org.codingmatters.value.objects.values.matchers.property.value.DateValueMatcher.dateValue(value);
    }

    public static Matcher<PropertyValue.Value> doubleValue(Double value) {
         return org.codingmatters.value.objects.values.matchers.property.value.DoubleValueMatcher.doubleValue(value);
    }

    public static Matcher<PropertyValue.Value> longValue(Long value) {
        return org.codingmatters.value.objects.values.matchers.property.value.LongValueMatcher.longValue(value);
    }

    public static Matcher<PropertyValue.Value> objectValue(ObjectValue value) {
        return org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValue(value);
    }

    public static Matcher<PropertyValue.Value> objectValue(Matcher<? super ObjectValue> value) {
        return org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValueMatching(value);
    }

    public static Matcher<PropertyValue.Value> stringValue(String value) {
        return org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValue(value);
    }

    public static Matcher<PropertyValue.Value> stringValue(Matcher<? super String> value) {
        return org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValueMatching(value);
    }

    public static Matcher<PropertyValue.Value> timeValue(LocalTime value) {
        return org.codingmatters.value.objects.values.matchers.property.value.TimeValueMatcher.timeValue(value);
    }
 }
