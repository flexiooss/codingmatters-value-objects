package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.ValueSpec;

import static org.codingmatters.value.objects.spec.TypeKind.*;

/**
 * Created by nelt on 9/28/16.
 */
public class ValueConfiguration {

    private final ClassName valueType;
    private final ClassName valueImplType;
    private final ClassName builderType;
    private final ClassName changerType;
    private final String rootPackage;
    private final ValueCollectionConfiguration collectionConfiguration;

    public ValueConfiguration(String rootPackage, String packageName, ValueSpec valueSpec) {
        this.rootPackage = rootPackage;
        String interfaceName = capitalizedFirst(valueSpec.name());
        this.valueType = ClassName.get(packageName, interfaceName);
        this.valueImplType = ClassName.get(packageName, interfaceName + "Impl");
        this.builderType = ClassName.get(packageName, interfaceName + ".Builder");
        this.changerType = ClassName.get(packageName, interfaceName + ".Changer");
        this.collectionConfiguration = new ValueCollectionConfiguration(rootPackage);
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

    public TypeName propertyType(PropertySpec propertySpec) {
        ClassName singleType = this.propertySingleType(propertySpec);
        if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST)) {
            return this.collectionConfiguration.valueListOfType(singleType);
        } else if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SET)) {
            return this.collectionConfiguration.valueSetOfType(singleType);
        } else {
            return singleType;
        }
    }

    public ClassName propertySingleType(PropertySpec propertySpec) {
        if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeSpec().typeKind())) {
            return ClassName.get(this.rootPackage, capitalizedFirst(propertySpec.typeSpec().typeRef()));
        } else if(ENUM.equals(propertySpec.typeSpec().typeKind()) && propertySpec.typeSpec().typeRef() == null) {
            return ClassName.bestGuess(this.enumTypeName(propertySpec.name()));
        } else {
            return ClassName.bestGuess(propertySpec.typeSpec().typeRef());
        }
    }

    public TypeName propertyImplType(PropertySpec propertySpec) {
        if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST)) {
            return this.collectionConfiguration.valueListImplOfType(this.propertySingleType(propertySpec));
        } else if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SET)) {
            return this.collectionConfiguration.valueSetImplOfType(this.propertySingleType(propertySpec));
        } else {
            return this.builderSinglePropertyType(propertySpec);
        }
    }

    private TypeName builderSinglePropertyType(PropertySpec propertySpec) {
        if(propertySpec.typeSpec().typeKind().isValueObject()) {
            String valueType1;
            if(propertySpec.typeSpec().typeKind().equals(EXTERNAL_VALUE_OBJECT)) {
                valueType1 = propertySpec.typeSpec().typeRef();
            } else {
                valueType1 = capitalizedFirst(propertySpec.typeSpec().typeRef());
            }
            return ClassName.bestGuess(valueType1 + ".Builder");
        } else {
            return propertySingleType(propertySpec);
        }
    }

    public String witherMethodName(PropertySpec propertySpec) {
        return "with" + capitalizedFirst(propertySpec.name());
    }


    public ValueCollectionConfiguration collectionConfiguration() {
        return collectionConfiguration;
    }

    public String package_() {
        return rootPackage;
    }

    static private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    public String enumTypeName(String name) {
        return this.capitalizedFirst(name);
    }
}
