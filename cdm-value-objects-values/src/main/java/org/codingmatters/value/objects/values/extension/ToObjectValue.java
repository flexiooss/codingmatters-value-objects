package org.codingmatters.value.objects.values.extension;

import org.codingmatters.value.objects.values.ObjectValue;

import java.util.Map;

public interface ToObjectValue {

    Map toMap();

    default ObjectValue toObjectValue() {
        return ObjectValue.fromMap(this.toMap()).build();
    }
}
