package org.codingmatters.value.objects.spec;

import java.util.Objects;

/**
 * Created by nelt on 9/5/16.
 */
public class PropertyTypeSpec {

    static public Builder type() {
        return new Builder();
    }

    static public class Builder {
        private String typeRef;
        private TypeKind typeKind;
        private PropertyCardinality cardinality = PropertyCardinality.SINGLE;
        private AnonymousValueSpec embeddedValueSpec;

        public Builder typeRef(String type) {
            this.typeRef = type;
            return this;
        }

        public Builder typeKind(TypeKind typeKind) {
            this.typeKind = typeKind;
            return this;
        }

        public Builder cardinality(PropertyCardinality cardinality) {
            this.cardinality = cardinality;
            return this;
        }

        public Builder embeddedValueSpec(AnonymousValueSpec embeddedValueSpec) {
            this.embeddedValueSpec = embeddedValueSpec;
            return this;
        }

        public PropertyTypeSpec build() {
            return new PropertyTypeSpec(this.typeRef, this.typeKind, this.cardinality, this.embeddedValueSpec);
        }
    }

    private final String typeRef;
    private final TypeKind typeKind;
    private final PropertyCardinality cardinality;
    private AnonymousValueSpec embeddedValueSpec;

    private PropertyTypeSpec(String typeRef, TypeKind typeKind, PropertyCardinality cardinality, AnonymousValueSpec embeddedValueSpec) {
        this.typeRef = typeRef;
        this.typeKind = typeKind;
        this.cardinality = cardinality;
        this.embeddedValueSpec = embeddedValueSpec;
    }

    public String typeRef() {
        return typeRef;
    }

    public TypeKind typeKind() {
        return typeKind;
    }

    public PropertyCardinality cardinality() {
        return cardinality;
    }

    public AnonymousValueSpec embeddedValueSpec() {
        return embeddedValueSpec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyTypeSpec that = (PropertyTypeSpec) o;
        return Objects.equals(typeRef, that.typeRef) &&
                typeKind == that.typeKind &&
                cardinality == that.cardinality &&
                Objects.equals(embeddedValueSpec, that.embeddedValueSpec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeRef, typeKind, cardinality, embeddedValueSpec);
    }

    @Override
    public String toString() {
        return "PropertyTypeSpec{" +
                "typeRef='" + typeRef + '\'' +
                ", typeKind=" + typeKind +
                ", cardinality=" + cardinality +
                ", embeddedValueSpec=" + embeddedValueSpec +
                '}';
    }
}
