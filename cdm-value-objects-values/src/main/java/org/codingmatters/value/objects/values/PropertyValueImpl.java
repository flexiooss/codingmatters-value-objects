package org.codingmatters.value.objects.values;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

class PropertyValueImpl implements PropertyValue {

    static class ValueImpl implements PropertyValue.Value {
        private final Type type;
        private final Object value;

        public ValueImpl(Type type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String stringValue() {
            assert this.type.equals(Type.STRING);
            return (String) this.value;
        }

        @Override
        public Long longValue() {
            if( this.type.equals( Type.DOUBLE ) ){
                return this.doubleValue().longValue();
            } else {
                assert this.type.equals( Type.LONG );
                return (Long) this.value;
            }
        }

        @Override
        public Double doubleValue() {
            if( this.type.equals( Type.LONG ) ){
                return this.longValue().doubleValue();
            } else {
                assert this.type.equals( Type.DOUBLE );
                return (Double) this.value;
            }
        }

        @Override
        public Boolean booleanValue() {
            assert this.type.equals(Type.BOOLEAN);
            return (Boolean) this.value;
        }

        @Override
        public byte[] bytesValue() {
            assert this.type.equals(Type.BYTES);
            return (byte[]) this.value;
        }

        @Override
        public LocalDate dateValue() {
            assert this.type.equals(Type.DATE);
            return (LocalDate) this.value;
        }

        @Override
        public LocalTime timeValue() {
            assert this.type.equals(Type.TIME);
            return (LocalTime) this.value;
        }

        @Override
        public LocalDateTime datetimeValue() {
            assert this.type.equals(Type.DATETIME);
            return (LocalDateTime) this.value;
        }

        @Override
        public ObjectValue objectValue() {
            assert this.type.equals(Type.OBJECT);
            return (ObjectValue) this.value;
        }

        @Override
        public Object rawValue() {
            return this.value;
        }

        @Override
        public boolean isa(Type type) {
            return this.type.equals(type);
        }

        @Override
        public Type type() {
            return this.type;
        }

        @Override
        public boolean isNull() {
            return this.value == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ValueImpl value1 = (ValueImpl) o;
            if (value1.rawValue() == null) {
                return this.rawValue() == null;
            }
            if (type != value1.type) return false;
            return value != null ? value.equals(value1.value) : value1.value == null;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return value + " (" + type + ")";
        }
    }

    private final Type type;
    private final Cardinality cardinality;
    private final Value[] value;

    public PropertyValueImpl(Type type, Cardinality cardinality, Value[] value) {
        this.type = type;
        this.cardinality = cardinality;
        this.value = value;
    }

    @Override
    public Type type() {
        return this.type;
    }

    @Override
    public Cardinality cardinality() {
        return this.cardinality;
    }

    @Override
    public Object rawValue() {
        return this.value;
    }

    @Override
    public boolean isNullValue() {
        if(Cardinality.MULTIPLE.equals(this.cardinality)) {
            return this.value == null;
        } else {
            return this.value == null ||
                            this.value.length == 0 ||
                            this.value[0] == null ||
                            this.value[0].isNull()
                    ;
        }
    }

    @Override
    public Value single() {
        return this.value != null && this.value.length > 0 ? this.value[0] : null;
    }

    @Override
    public Value[] multiple() {
        return value;
    }

    @Override
    public String[] multipleString() {
        return Arrays.stream(multiple()).map(Value::stringValue).toArray(String[]::new);
    }

    @Override
    public Long[] multipleLong() {
        return Arrays.stream(multiple()).map(Value::longValue).toArray(Long[]::new);
    }

    @Override
    public Double[] multipleDouble() {
        return Arrays.stream(multiple()).map(Value::doubleValue).toArray(Double[]::new);
    }

    @Override
    public ObjectValue[] multipleObject() {
        return Arrays.stream(multiple()).map(Value::objectValue).toArray(ObjectValue[]::new);
    }

    @Override
    public LocalDate[] multipleDate() {
        return Arrays.stream(multiple()).map(Value::dateValue).toArray(LocalDate[]::new);
    }

    @Override
    public LocalDateTime[] multipleDatetime() {
        return Arrays.stream(multiple()).map(Value::datetimeValue).toArray(LocalDateTime[]::new);
    }

    @Override
    public LocalTime[] multipleTime() {
        return Arrays.stream(multiple()).map(Value::timeValue).toArray(LocalTime[]::new);
    }

    @Override
    public Boolean[] multipleBoolean() {
        return Arrays.stream(multiple()).map(Value::booleanValue).toArray(Boolean[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyValueImpl that = (PropertyValueImpl) o;
        if((this.type == null || that.type == null) && cardinality.equals(Cardinality.MULTIPLE) && Objects.deepEquals(this.value, new Value[0])) {
            return that.cardinality.equals(Cardinality.MULTIPLE) && Objects.deepEquals(that.value, new Value[0]);
        }
        return type == that.type && cardinality == that.cardinality && Objects.deepEquals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cardinality, Arrays.hashCode(value));
    }

    @Override
    public String toString() {
        if(this.value == null) {
            return "null";
        }
        if(Cardinality.MULTIPLE.equals(this.cardinality)) {
            return Arrays.deepToString(this.value);
        }
        if(this.value.length > 0) {
            return "" + this.value[0];
        }
        return "null";
    }
}
