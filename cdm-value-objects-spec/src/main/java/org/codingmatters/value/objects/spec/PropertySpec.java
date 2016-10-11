package org.codingmatters.value.objects.spec;

import java.util.Objects;

/**
 * Created by nelt on 9/3/16.
 */
public class PropertySpec {

    static public Builder property() {
        return new Builder();
    }

    static public class Builder {
        private String name;
        private PropertyTypeSpec type;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(PropertyTypeSpec.Builder type) {
            this.type = type.build();
            return this;
        }

        public PropertySpec build() {
            return new PropertySpec(this.name, this.type);
        }
    }

    private final String name;
    private PropertyTypeSpec type;

    public PropertySpec(String name, PropertyTypeSpec type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public PropertyTypeSpec typeSpec() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySpec that = (PropertySpec) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "PropertySpec{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
