package org.codingmatters.value.objects.values;

import org.codingmatters.value.objects.values.helper.ObjectValueToMap;
import org.codingmatters.value.objects.values.optional.OptionalObjectValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface ObjectValue {

    static Builder builder() {
        return new Builder();
    }

    static Builder from(ObjectValue value) {
        Builder result = builder();
        for (String name : value.propertyNames()) {
            result.property(name, value.property(name));
        }

        return result;
    }

    static Builder fromMap(Map value) {
        if(value == null) {
            return builder();
        }

        Builder result = builder();
        for (Object name : value.keySet()) {
            try {
                result.property(name.toString(), PropertyValue.fromObject(value.get(name)));
            } catch (PropertyValue.Type.UnsupportedTypeException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    class Builder {
        private Map<String, PropertyValue> properties = new HashMap<>();

        public Builder property(String property, PropertyValue value) {
            this.properties.put(property, value);
            return this;
        }
        public Builder property(String property, PropertyValue.Builder value) {
            return this.property(property, value != null ? value.build() : null);
        }
        public Builder property(String property, Consumer<PropertyValue.Builder> value) {
            PropertyValue.Builder builder = PropertyValue.builder();
            if(value != null) {
                value.accept(builder);
            }
            return this.property(property, builder.build());
        }

        public ObjectValue build() {
            return new ObjectValueImpl(this.properties);
        }

    }

    OptionalObjectValue opt();

    boolean has(String property);
    PropertyValue property(String property);
    String [] propertyNames();

    default ObjectValue withProperty(String property, PropertyValue value) {
        return ObjectValue.from(this).property(property, value).build();
    }
    default ObjectValue withProperty(String property, PropertyValue.Builder value) {
        return ObjectValue.from(this).property(property, value).build();
    }
    default ObjectValue withProperty(String property, Consumer<PropertyValue.Builder> value) {
        return ObjectValue.from(this).property(property, value).build();
    }
    default ObjectValue withChangedProperty(String property, PropertyValue.Changer changer) {
        PropertyValue.Builder builder = PropertyValue.Builder.from(this.property(property));
        builder = changer.configure(builder);
        return this.withProperty(property, builder.build());
    }


    interface Changer {
        ObjectValue.Builder configure(ObjectValue.Builder builder);
    }

    default Builder toBuilder() {
        return from(this);
    }
    default Map<String, Object> toMap() {
        return ObjectValueToMap.toMap(this);
    }
}
