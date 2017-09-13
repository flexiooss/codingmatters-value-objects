package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.ValueSpec;

import static org.codingmatters.value.objects.spec.TypeKind.ENUM;
import static org.codingmatters.value.objects.spec.TypeKind.IN_SPEC_VALUE_OBJECT;

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
        } else if(ENUM.equals(propertySpec.typeSpec().typeKind()) && propertySpec.typeSpec().isInSpecEnum()) {
            return ClassName.bestGuess(this.enumTypeName(propertySpec.name()));
        } else {
            return ClassName.bestGuess(propertySpec.typeSpec().typeRef());
        }
    }

    public String witherMethodName(PropertySpec propertySpec) {
        return "with" + capitalizedFirst(propertySpec.name());
    }


    public ValueCollectionConfiguration collectionConfiguration() {
        return collectionConfiguration;
    }

    static private String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    public String enumTypeName(String name) {
        return this.capitalizedFirst(name);
    }

    public ClassName propertyClass(PropertySpec propertySpec) {
        if(propertySpec.typeSpec().isInSpecEnum()) {
            return this.valueType().nestedClass(this.enumTypeName(propertySpec.name()));
        } else {
            return this.classNameForTypeRef(propertySpec);
        }
    }

    public ClassName classNameForTypeRef(PropertySpec propertySpec) {
        try {
            return ClassName.get(Class.forName(propertySpec.typeSpec().typeRef()));
        } catch (ClassNotFoundException e) {
            System.err.println("class not found : " + propertySpec.typeSpec().typeRef());
            return null;
        }
    }
}
