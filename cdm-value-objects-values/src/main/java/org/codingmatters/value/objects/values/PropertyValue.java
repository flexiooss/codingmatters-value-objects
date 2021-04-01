package org.codingmatters.value.objects.values;

import org.codingmatters.value.objects.values.vals.Val;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface PropertyValue {

    static Builder builder() {
        return new Builder();
    }

    static PropertyValue multiple(Type type, Value ... values) {
        if(values != null) {
            for (Value value : values) {
                assert value.isa(type);
            }
        }

        return new PropertyValueImpl(type, Cardinality.MULTIPLE, values);
    }
    
    static PropertyValue multiple(Type type, Builder ... builders) {
        Value[] values = null;
        if(builders != null) {
            values = new Value[builders.length];
            for (int i = 0; i < builders.length; i++) {
                values[i] = builders[i].buildValue();
            }
        }
        return multiple(type, values);
    }

    static PropertyValue multiple(Type type, Consumer<Builder> ... consumers) {
        Builder[] builders = null;
        if(consumers != null) {
            builders = new Builder[consumers.length];
            for (int i = 0; i < builders.length; i++) {
                builders[i] = PropertyValue.builder();
                consumers[i].accept(builders[i]);
            }
        }
        return multiple(type, builders);
    }


    class Builder {
        private Type type = Type.OBJECT;
        private Object raw = null;

        public static Builder from(PropertyValue value) {
            if(value == null || value.isNullValue()) return new Builder();

            Builder result = new Builder();
            result.type = value.type();
            result.raw = value.rawValue();
            return result;
        }

        public Builder stringValue(String value) {
            this.raw = value;
            this.type = Type.STRING;
            return this;
        }

        public Builder longValue(Long value) {
            this.raw = value;
            this.type = Type.LONG;
            return this;
        }

        public Builder doubleValue(Double value) {
            this.raw = value;
            this.type = Type.DOUBLE;
            return this;
        }

        public Builder booleanValue(Boolean value) {
            this.raw = value;
            this.type = Type.BOOLEAN;
            return this;
        }

        public Builder bytesValue(byte[] value) {
            this.raw = value;
            this.type = Type.BYTES;
            return this;
        }

        public Builder dateValue(LocalDate value) {
            this.raw = value;
            this.type = Type.DATE;
            return this;
        }

        public Builder timeValue(LocalTime value) {
            this.raw = value;
            this.type = Type.TIME;
            return this;
        }

        public Builder datetimeValue(LocalDateTime value) {
            this.raw = value;
            this.type = Type.DATETIME;
            return this;
        }

        public Builder objectValue(ObjectValue value) {
            this.raw = value;
            this.type = Type.OBJECT;
            return this;
        }

        public Builder objectValue(ObjectValue.Builder value) {
            return this.objectValue(value.build());
        }

        public Builder objectValue(Consumer<ObjectValue.Builder> value) {
            ObjectValue.Builder builder = ObjectValue.builder();
            value.accept(builder);
            return this.objectValue(builder);
        }

        public PropertyValue build() {
            return new PropertyValueImpl(
                    this.type,
                    Cardinality.SINGLE,
                    new Value[] {this.buildValue()}
            );
        }

        public Value buildValue() {
            return new PropertyValueImpl.ValueImpl(this.type, this.raw);
        }

    }

    enum Type {
        STRING {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.stringValue((String) value);
            }
        },
        LONG {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.longValue((Long) value);
            }
        },
        DOUBLE {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.doubleValue((Double) value);
            }
        },
        BOOLEAN {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.booleanValue((Boolean) value);
            }
        },
        BYTES {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.bytesValue((byte[]) value);
            }
        },
        DATE {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.dateValue((LocalDate) value);
            }
        },
        TIME {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.timeValue((LocalTime) value);
            }
        },
        DATETIME {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.datetimeValue((LocalDateTime) value);
            }
        },
        OBJECT {
            @Override
            public Builder set(Builder builder, Object value) {
                return builder.objectValue((ObjectValue) value);
            }
        };

        public abstract Builder set(Builder builder, Object value);

        static public Type fromObject(Object o) throws UnsupportedTypeException {
            if(o == null) return STRING;
            if(o instanceof String) {
                return STRING;
            } else if(o instanceof Long || o instanceof Integer) {
                return LONG;
            } else if(o instanceof Double || o instanceof Float) {
                return DOUBLE;
            } else if(o instanceof Boolean) {
                return BOOLEAN;
            } else if(o instanceof byte[]) {
                return BYTES;
            } else if(o instanceof ObjectValue){
                return OBJECT;
            } else {
                throw new UnsupportedTypeException("unsupported type : " + o.getClass());
            }
        }

        static public class UnsupportedTypeException extends Exception {
            public UnsupportedTypeException(String msg) {
                super(msg);
            }
        }
    }

    enum Cardinality {
        SINGLE, MULTIPLE
    }

    Type type();
    Cardinality cardinality();

    Object rawValue();
    boolean isNullValue();

    interface Value {
        String stringValue();
        Long longValue();
        Double doubleValue();
        Boolean booleanValue();
        byte[] bytesValue();
        ObjectValue objectValue();
        Object rawValue();
        LocalDate dateValue();
        LocalTime timeValue();
        LocalDateTime datetimeValue();

        boolean isa(Type type);
        Type type();

        boolean isNull();
    }

    Value single();
    Value[] multiple();

    interface Changer {
        PropertyValue.Builder configure(PropertyValue.Builder builder);
    }

    default Builder toBuilder() {
        return Builder.from(this);
    }
    
    static PropertyValue fromObject(Object object) throws Type.UnsupportedTypeException {
        if(object == null) return null;

        if(object instanceof Object[]) {
            object = Arrays.asList((Object[]) object);
        }
        if(object instanceof Iterable) {
            List<Value> vals = new LinkedList<>();
            for (Object o : ((Iterable) object)) {
                vals.add(fromObject(o).single());
            }
            return PropertyValue.multiple(
                    vals.isEmpty() ? Type.OBJECT : vals.get(0).type(),
                    vals.toArray(new Value[0]));
        } else {
            if (object instanceof String) {
                return PropertyValue.builder().stringValue((String) object).build();
            }
            if (object instanceof Long) {
                return PropertyValue.builder().longValue((long) object).build();
            }
            if (object instanceof Integer) {
                return PropertyValue.builder().longValue((long) (Integer) object).build();
            }
            if (object instanceof Double) {
                return PropertyValue.builder().doubleValue((Double) object).build();
            }
            if (object instanceof Float) {
                return PropertyValue.builder().doubleValue((double) (Float) object).build();
            }
            if (object instanceof Boolean) {
                return PropertyValue.builder().booleanValue((Boolean) object).build();
            }
            if (object instanceof ObjectValue) {
                return PropertyValue.builder().objectValue((ObjectValue) object).build();
            }
            if (object instanceof LocalDateTime) {
                return PropertyValue.builder().datetimeValue((LocalDateTime) object).build();
            }
            if (object instanceof LocalDate) {
                return PropertyValue.builder().dateValue((LocalDate) object).build();
            }
            if (object instanceof LocalTime) {
                return PropertyValue.builder().timeValue((LocalTime) object).build();
            }
            if (object instanceof byte[]) {
                return PropertyValue.builder().bytesValue((byte[]) object).build();
            }
            if(object instanceof Map) {
                return PropertyValue.builder().objectValue(ObjectValue.fromMap((Map) object).build()).build();
            }
        }
        throw new Type.UnsupportedTypeException("unsupported type : " + object.getClass());
    }
}
