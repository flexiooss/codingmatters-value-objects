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
        private PropertyType type;
        private String referencedType;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(PropertyType type) {
            this.type = type;
            return this;
        }

        public Builder referencedType(String referencedType) {
            this.referencedType = referencedType;
            return this;
        }

        public PropertySpec build() {
            return new PropertySpec(this.name, this.type, this.referencedType);
        }
    }

    private final String name;
    private final PropertyType type;
    private final String referencedType;

    public PropertySpec(String name, PropertyType type, String referencedType) {
        this.name = name;
        this.type = type;
        this.referencedType = referencedType;
    }

    public String name() {
        return name;
    }

    public PropertyType type() {
        return type;
    }

    public String referencedType() {
        return referencedType;
    }

    @Override
    public String toString() {
        return "PropertySpec{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", referencedType='" + referencedType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySpec that = (PropertySpec) o;
        return Objects.equals(name, that.name) &&
                type == that.type &&
                Objects.equals(referencedType, that.referencedType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, referencedType);
    }
}
