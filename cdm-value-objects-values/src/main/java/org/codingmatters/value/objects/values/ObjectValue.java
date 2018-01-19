package org.codingmatters.value.objects.values;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface ObjectValue {

    static Builder builder() {
        return new Builder();
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

    boolean has(String property);

    PropertyValue property(String property);
}
