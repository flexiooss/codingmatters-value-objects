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
        private TypeSpec type;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(TypeSpec.Builder type) {
            this.type = type.build();
            return this;
        }

//        public Builder type(TypeSpec type) {
//            this.type = type;
//            return this;
//        }

        public PropertySpec build() {
            return new PropertySpec(this.name, this.type.typeRef(), this.type.typeKind());
        }
    }

    private final String name;
    private final String type;
    private final TypeKind typeKind;

    public PropertySpec(String name, String type, TypeKind typeKind) {
        this.name = name;
        this.type = type;
        this.typeKind = typeKind;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public TypeKind typeKind() {
        return typeKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySpec that = (PropertySpec) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                typeKind == that.typeKind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, typeKind);
    }

    @Override
    public String toString() {
        return "PropertySpec{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", typeKind=" + typeKind +
                '}';
    }
}
