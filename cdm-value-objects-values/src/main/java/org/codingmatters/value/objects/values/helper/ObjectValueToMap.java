package org.codingmatters.value.objects.values.helper;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.PropertyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectValueToMap {
    static public Map<String, Object> toMap(ObjectValue value) {
        if (value == null) {
            return null;
        }

        HashMap<String, Object> result = new HashMap<>();
        for (String key : value.propertyNames()) {
            result.put(key, toMap(value.property(key)));
        }
        return result;
    }

    private static Object toMap(PropertyValue property) {
        if (property == null || property.isNullValue()) {
            return null;
        }
        if (property.cardinality().equals(PropertyValue.Cardinality.MULTIPLE)) {
            List<Object> values = new ArrayList<>(property.multiple().length);
            for (PropertyValue.Value value : property.multiple()) {
                values.add(toObject(value));
            }
            return values;
        } else {
            return toObject(property.single());
        }
    }

    private static Object toObject(PropertyValue.Value value) {
        return switch (value.type()) {
            case STRING -> value.stringValue();
            case LONG -> value.longValue();
            case DOUBLE -> value.doubleValue();
            case BOOLEAN -> value.booleanValue();
            case BYTES -> value.bytesValue();
            case DATE -> value.dateValue();
            case TIME -> value.timeValue();
            case DATETIME -> value.datetimeValue();
            case OBJECT -> toMap(value.objectValue());
        };
    }
}
