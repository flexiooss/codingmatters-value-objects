package org.codingmatters.value.objects.values.matchers.property;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;
import org.hamcrest.Matcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.codingmatters.value.objects.values.matchers.property.SingleValueMatcher.single;
import static org.codingmatters.value.objects.values.matchers.property.value.BooleanValueMatcher.booleanValue;
import static org.codingmatters.value.objects.values.matchers.property.value.BytesValueMatcher.bytesValue;
import static org.codingmatters.value.objects.values.matchers.property.value.DateTimeValueMatcher.dateTimeValue;
import static org.codingmatters.value.objects.values.matchers.property.value.DateValueMatcher.dateValue;
import static org.codingmatters.value.objects.values.matchers.property.value.DoubleValueMatcher.doubleValue;
import static org.codingmatters.value.objects.values.matchers.property.value.LongValueMatcher.longValue;
import static org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValueMatching;
import static org.codingmatters.value.objects.values.matchers.property.value.ObjectValueMatcher.objectValue;
import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValue;
import static org.codingmatters.value.objects.values.matchers.property.value.StringValueMatcher.stringValueMatching;
import static org.codingmatters.value.objects.values.matchers.property.value.TimeValueMatcher.timeValue;

public final class PropertyValueMatchers {
    public static Matcher<PropertyValue> withValue(String str) {
        return single(stringValue(str));
    }

    public static Matcher<PropertyValue> withValueMatching(Matcher<String> matcher) {
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
}
