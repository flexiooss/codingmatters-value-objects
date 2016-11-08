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
    private final ClassName changerType;

    public ValueConfiguration(String packageName, ValueSpec valueSpec) {
        String interfaceName = capitalizedFirst(valueSpec.name());
        this.valueType = ClassName.get(packageName, interfaceName);
        this.valueImplType = ClassName.get(packageName, interfaceName + "Impl");
        this.builderType = ClassName.get(packageName, interfaceName + ".Builder");
        this.changerType = ClassName.get(packageName, interfaceName + ".Changer");
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

    public ClassName valueChangerType() {
        return changerType;
    }

    public ClassName propertyType(PropertySpec propertySpec) {
        if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeSpec().typeKind())) {
            return ClassName.bestGuess(capitalizedFirst(propertySpec.typeSpec().typeRef()));
        } else {
            return ClassName.bestGuess(propertySpec.typeSpec().typeRef());
        }
    }

    public ClassName builderPropertyType(PropertySpec propertySpec) {
        if(propertySpec.typeSpec().typeKind().isValueObject()) {
            String valueType;
            if(propertySpec.typeSpec().typeKind().equals(EXTERNAL_VALUE_OBJECT)) {
                valueType = propertySpec.typeSpec().typeRef();
            } else {
                valueType = capitalizedFirst(propertySpec.typeSpec().typeRef());
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