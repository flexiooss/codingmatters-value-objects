package org.codingmatters.value.objects.spec;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
        private boolean isInSpecEnum = false;
        private List<String> enumValues = new LinkedList<>();

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

        public Builder embeddedValueSpec(AnonymousValueSpec spec) {
            this.embeddedValueSpec = spec;
            return this;
        }

        public Builder embeddedValueSpec(AnonymousValueSpec.Builder builder) {
            this.embeddedValueSpec = builder.build();
            return this;
        }

        public Builder enumValues(String ... enumValues) {
            this.isInSpecEnum = true;
            this.enumValues.clear();
            if(enumValues != null) {
                for (String enumValue : enumValues) {
                    this.enumValues.add(enumValue);
                }
            }
            return this;
        }

        public PropertyTypeSpec build() {
            return new PropertyTypeSpec(
                    this.typeRef,
                    this.typeKind,
                    this.cardinality,
                    this.embeddedValueSpec,
                    this.isInSpecEnum,
                    new ArrayList<>(this.enumValues)
            );
        }
    }

    private final String typeRef;
    private final TypeKind typeKind;
    private final PropertyCardinality cardinality;
    private final AnonymousValueSpec embeddedValueSpec;
    private final boolean isInSpecEnum;
    private final List<String> enumValues;

    private PropertyTypeSpec(String typeRef, TypeKind typeKind, PropertyCardinality cardinality, AnonymousValueSpec embeddedValueSpec, boolean isInSpecEnum, List<String> enumValues) {
        this.typeRef = typeRef;
        this.typeKind = typeKind;
        this.cardinality = cardinality;
        this.embeddedValueSpec = embeddedValueSpec;
        this.isInSpecEnum = isInSpecEnum;
        this.enumValues = enumValues;
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

    public boolean isInSpecEnum() {
        return isInSpecEnum;
    }

    public String [] enumValues() {
        return this.enumValues.toArray(new String[this.enumValues.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyTypeSpec that = (PropertyTypeSpec) o;
        return isInSpecEnum == that.isInSpecEnum &&
                Objects.equals(typeRef, that.typeRef) &&
                typeKind == that.typeKind &&
                cardinality == that.cardinality &&
                Objects.equals(embeddedValueSpec, that.embeddedValueSpec) &&
                Objects.equals(enumValues, that.enumValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeRef, typeKind, cardinality, embeddedValueSpec, isInSpecEnum, enumValues);
    }

    @Override
    public String toString() {
        return "PropertyTypeSpec{" +
                "typeRef='" + typeRef + '\'' +
                ", typeKind=" + typeKind +
                ", cardinality=" + cardinality +
                ", embeddedValueSpec=" + embeddedValueSpec +
                ", isInSpecEnum=" + isInSpecEnum +
                ", enumValues=" + enumValues +
                '}';
    }
}
