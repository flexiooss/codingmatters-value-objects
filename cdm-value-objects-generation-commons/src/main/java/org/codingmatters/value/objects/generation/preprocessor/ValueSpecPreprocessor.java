package org.codingmatters.value.objects.generation.preprocessor;

import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.codingmatters.value.objects.spec.TypeKind.EMBEDDED;
import static org.codingmatters.value.objects.spec.TypeKind.EXTERNAL_VALUE_OBJECT;

/**
 * Created by nelt on 11/23/16.
 */
public class ValueSpecPreprocessor {

    private final String packageName;
    private final ValueSpec valueSpec;

    public ValueSpecPreprocessor(String packageName, ValueSpec valueSpec) {
        this.packageName = packageName;
        this.valueSpec = valueSpec;
    }

    public List<PackagedValueSpec> preprocess() {
        List<PackagedValueSpec> result = new LinkedList<>();

        ValueSpec.Builder valueSpecBuilder = ValueSpec.valueSpec()
                .name(valueSpec.name())
                .addConformsTo(valueSpec.protocols().toArray(new String[valueSpec.protocols().size()]))
                .addBuilderConformsTo(valueSpec.builderProtocols().toArray(new String[valueSpec.builderProtocols().size()]));
        for (PropertySpec propertySpec : valueSpec.propertySpecs()) {
            if(EMBEDDED.equals(propertySpec.typeSpec().typeKind())) {
                String embeddedPackage = this.packageName + "." + valueSpec.name().toLowerCase();

                ValueSpec embeddedValueSpec = this.createEmbeddedValueSpec(propertySpec);
                result.addAll(new ValueSpecPreprocessor(embeddedPackage, embeddedValueSpec).preprocess());

                valueSpecBuilder.addProperty(this.createInSpecPropertyForEmbeddedType(propertySpec, embeddedPackage));
            } else {
                valueSpecBuilder.addProperty(propertySpec);
            }
        }
        result.add(new PackagedValueSpec(this.packageName, valueSpecBuilder.build()));

        return result;
    }

    private PropertySpec createInSpecPropertyForEmbeddedType(PropertySpec propertySpec, String embeddedPackage) {
        return PropertySpec.property()
                .name(propertySpec.name())
                .type(PropertyTypeSpec.type()
                        .typeKind(EXTERNAL_VALUE_OBJECT)
                        .typeRef(embeddedPackage + "." + capitalizedFirst(propertySpec.name()))
                        .cardinality(propertySpec.typeSpec().cardinality())
                )
                .hints(propertySpec.hints() != null ? new HashSet<>(Arrays.asList(propertySpec.hints())) : new HashSet<>())
                .build();
    }

    private ValueSpec createEmbeddedValueSpec(PropertySpec propertySpec) {
        ValueSpec.Builder embeddedValueSpecBuilder = ValueSpec.valueSpec()
                .name(propertySpec.name())
                ;

        if(propertySpec.typeSpec().embeddedValueSpec().protocols() != null) {
            embeddedValueSpecBuilder.addConformsTo(
                        propertySpec.typeSpec().embeddedValueSpec().protocols().toArray(new String[0])
                );
        }
        if(propertySpec.typeSpec().embeddedValueSpec().builderProtocols() != null) {
            embeddedValueSpecBuilder.addBuilderConformsTo(
                        propertySpec.typeSpec().embeddedValueSpec().builderProtocols().toArray(new String[0])
                );
        }

        for (PropertySpec embeddedPropSpec : propertySpec.typeSpec().embeddedValueSpec().propertySpecs()) {
            embeddedValueSpecBuilder.addProperty(embeddedPropSpec);
        }

        return embeddedValueSpecBuilder.build();
    }

    static private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
