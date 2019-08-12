package org.codingmatters.value.objects.values.vals.optional;

import org.codingmatters.value.objects.values.ObjectValue;
import org.codingmatters.value.objects.values.optional.OptionalObjectValue;
import org.codingmatters.value.objects.values.vals.Val;

public class OptionalVal {
    public static OptionalVal of(Val value) {
        return new OptionalVal(value);
    }
    public OptionalVal(Val value) {

    }
}
