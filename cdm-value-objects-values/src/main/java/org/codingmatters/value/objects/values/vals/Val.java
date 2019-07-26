package org.codingmatters.value.objects.values.vals;

import org.omg.CORBA.DATA_CONVERSION;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public interface Val {

    VType type();

    static BaseTypeVal<String> stringValue(String v) {
        return new BaseTypeValImpl<>(VType.STRING, v);
    }

    static BaseTypeVal<Long> longValue(Long v) {
        return new BaseTypeValImpl<>(VType.LONG, v);
    }

    static BaseTypeVal<Double> doubleValue(Double v) {
        return new BaseTypeValImpl<>(VType.DOUBLE, v);
    }

    static BaseTypeVal<Boolean> booleanValue(Boolean v) {
        return new BaseTypeValImpl<>(VType.BOOLEAN, v);
    }

    static BaseTypeVal<byte[]> bytesValue(byte[] v) {
        return new BaseTypeValImpl<>(VType.BYTES, v);
    }

    static BaseTypeVal<LocalDate> dateValue(LocalDate v) {
        return new BaseTypeValImpl<>(VType.DATE, v);
    }

    static BaseTypeVal<LocalTime> timeValue(LocalTime v) {
        return new BaseTypeValImpl<>(VType.TIME, v);
    }

    static BaseTypeVal<LocalDateTime> datetimeValue(LocalDateTime v) {
        return new BaseTypeValImpl<>(VType.DATETIME, v);
    }

    static ArrayVal.Builder array() {
        return ArrayVal.builder();
    }

    static ObjectVal.Builder object() {
        return ObjectVal.builder();
    }

    interface ArrayVal extends Val {
        Val[] values();

        static Builder builder() {
            return new Builder();
        }
        class Builder {
            private final LinkedList<Val> values = new LinkedList<>();

            public Builder values(Val...vals) {
                this.values.clear();
                if(vals != null) {
                    for (Val val : vals) {
                        this.values.add(val);
                    }
                }
                return this;
            }
            public Builder with(Val... vals) {
                if(vals != null) {
                    for (Val val : vals) {
                        this.values.add(val);
                    }
                }
                return this;
            }

            public ArrayVal build() {
                return new ArrayValImpl(this.values);
            }
        }
    }

    interface ObjectVal extends Val {
        Val property(String name);

        static Builder builder() {
            return new Builder();
        }

        class Builder {
            private final HashMap<String, Val> properties = new HashMap<>();

            public Builder property(String name, Val v) {
                this.properties.put(name, v);
                return this;
            }

            public ObjectVal build() {
                return new ObjectValImpl(this.properties);
            }
        }
    }

    interface BaseTypeVal<T> extends Val {
        T value();
    }



}
