package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.ValueSpec;

import static org.codingmatters.value.objects.spec.TypeKind.EXTERNAL_VALUE_OBJECT;
import static org.codingmatters.value.objects.spec.TypeKind.IN_SPEC_VALUE_OBJECT;

/**
 * Created by nelt on 9/28/16.
 */
public class ValueConfiguration {

    private final ClassName valueType;
    private final ClassName valueImplType;
    private final ClassName builderType;

    public ValueConfiguration(String packageName, ValueSpec valueSpec) {
        String interfaceName = capitalizedFirst(valueSpec.name());
        this.valueType = ClassName.get(packageName, interfaceName);
        this.valueImplType = ClassName.get(packageName, interfaceName + "Impl");
        this.builderType = ClassName.get(packageName, interfaceName + ".Builder");
    }

    public ClassName valueType() {
        return valueType;
    }

    public ClassName valueImplType() {
        return valueImplType;
    }

    public ClassName valueBuilderType() {
        return builderType;
    }

    public ClassName propertyType(PropertySpec propertySpec) {
        if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeKind())) {
            return ClassName.bestGuess(capitalizedFirst(propertySpec.type()));
        } else {
            return ClassName.bestGuess(propertySpec.type());
        }
    }

    public ClassName builderPropertyType(PropertySpec propertySpec) {
        if(propertySpec.typeKind().isValueObject()) {
            String valueType;
            if(propertySpec.typeKind().equals(EXTERNAL_VALUE_OBJECT)) {
                valueType = propertySpec.type();
            } else {
                valueType = capitalizedFirst(propertySpec.type());
            }
            return ClassName.bestGuess(valueType + ".Builder");
        } else {
            return propertyType(propertySpec);
        }
    }

    public String witherMethodName(PropertySpec propertySpec) {
        return "with" + capitalizedFirst(propertySpec.name());
    }


    static private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

}
