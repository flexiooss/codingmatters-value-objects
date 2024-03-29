package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.codingmatters.value.objects.spec.PropertyCardinality;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeToken;
import org.codingmatters.value.objects.spec.ValueSpec;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;

import static org.codingmatters.value.objects.spec.TypeKind.ENUM;
import static org.codingmatters.value.objects.spec.TypeKind.IN_SPEC_VALUE_OBJECT;

/**
 * Created by nelt on 9/28/16.
 */
public class ValueConfiguration {
    private final ValueSpec valueSpec;
    private final ClassName valueType;
    private final ClassName valueImplType;
    private final ClassName builderType;
    private final ClassName changerType;
    private final ClassName optionalValueType;
    private final ClassName namesType;
    private final String rootPackage;
    private final ValueCollectionConfiguration collectionConfiguration;

    public ValueConfiguration(String rootPackage, String packageName, ValueSpec valueSpec) {
        this.rootPackage = rootPackage;
        this.valueSpec = valueSpec;
        String interfaceName = capitalizedFirst(valueSpec.name());
        this.valueType = ClassName.get(packageName, interfaceName);
        this.valueImplType = ClassName.get(packageName, interfaceName + "Impl");
        this.builderType = ClassName.get(packageName, interfaceName + ".Builder");
        this.changerType = ClassName.get(packageName, interfaceName + ".Changer");
        this.optionalValueType = ClassName.get(packageName + ".optional", "Optional" + interfaceName);
        this.collectionConfiguration = new ValueCollectionConfiguration(rootPackage);
        this.namesType = ClassName.get(packageName + ".names", interfaceName + "Names");
    }

    public TypeName propertyType(PropertySpec propertySpec) {
        TypeName singleType = this.propertySingleType(propertySpec);
        if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST)) {
            return this.collectionConfiguration.valueListOfType(singleType);
        } else if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SET)) {
            return this.collectionConfiguration.valueSetOfType(singleType);
        } else {
            return singleType;
        }
    }

    public TypeName propertyOptionalType(PropertySpec propertySpec) {
        TypeName singleType = this.propertySingleType(propertySpec);
        if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST)) {
            return this.collectionConfiguration.optionalValueListOfType(singleType, this.propertySingleOptionalType(propertySpec));
        } else if(propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SET)) {
            return this.collectionConfiguration.optionalValueSetOfType(singleType);
        } else {
            return this.propertySingleOptionalType(propertySpec);
        }
    }

    public TypeName propertySingleType(PropertySpec propertySpec) {
        if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeSpec().typeKind())) {
            return ClassName.get(this.rootPackage, capitalizedFirst(propertySpec.typeSpec().typeRef()));
        } else if(ENUM.equals(propertySpec.typeSpec().typeKind()) && propertySpec.typeSpec().isInSpecEnum()) {
            return this.valueType().nestedClass(this.enumTypeName(propertySpec.name()));
        } else {
            if(propertySpec.typeSpec().typeRef().equals(byte[].class.getName())) {
                return ArrayTypeName.of(byte.class);
            } else {
                try {
                    return ClassName.bestGuess(propertySpec.typeSpec().typeRef().replaceAll("\\$", "."));
                } catch(IllegalArgumentException e) {
                    System.err.println("#######");
                    System.err.println("#######");
                    System.err.println("#######");
                    System.err.println(propertySpec);
                    System.err.println("#######");
                    System.err.println("#######");
                    System.err.println("#######");
                    throw e;
                }
            }
        }
    }

    public ClassName valueObjectSingleType(PropertySpec propertySpec) {
        if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeSpec().typeKind())) {
            return ClassName.get(this.rootPackage, capitalizedFirst(propertySpec.typeSpec().typeRef()));
        } else {
            return ClassName.bestGuess(propertySpec.typeSpec().typeRef());
        }
    }

    public ClassName valueObjectNamesType(PropertySpec propertySpec) {
        ClassName singleType = this.valueObjectSingleType(propertySpec);
        return ClassName.get(singleType.packageName() + ".names", singleType.simpleName() + "Names");
    }

    public TypeName propertySingleOptionalType(PropertySpec propertySpec) {
        TypeName rawType = this.propertySingleType(propertySpec);
        if(propertySpec.typeSpec().typeKind().isValueObject()) {
            if(IN_SPEC_VALUE_OBJECT.equals(propertySpec.typeSpec().typeKind())) {
                return ClassName.get(this.rootPackage + ".optional", "Optional" + capitalizedFirst(propertySpec.typeSpec().typeRef()));
            } else {
                int lastDot = propertySpec.typeSpec().typeRef().lastIndexOf('.');
                if(lastDot == -1) {
                    return ClassName.get("optional", "Optional" + propertySpec.typeSpec().typeRef());
                } else {
                    return ClassName.get(
                            propertySpec.typeSpec().typeRef().substring(0, lastDot) + ".optional",
                            "Optional" + propertySpec.typeSpec().typeRef().substring(lastDot + 1));
                }
            }
        } else if(ENUM.equals(propertySpec.typeSpec().typeKind()) && propertySpec.typeSpec().isInSpecEnum()) {
            return ParameterizedTypeName.get(ClassName.get(Optional.class), rawType);
        } else {
            return ParameterizedTypeName.get(ClassName.get(Optional.class), rawType);
        }
    }


    public ClassName propertyClass(PropertySpec propertySpec) {
        if(propertySpec.typeSpec().isInSpecEnum()) {
            return this.valueType().nestedClass(this.enumTypeName(propertySpec.name()));
        } else {
            try {
                return ClassName.get(Class.forName(propertySpec.typeSpec().typeRef()));
            } catch (ClassNotFoundException e) {
                return ClassName.bestGuess(propertySpec.typeSpec().typeRef());
//                System.err.println("class not found : " + propertySpec.typeSpec().typeRef());
//                System.err.println("classpath :");
//                for (URL url : ((URLClassLoader) Thread.currentThread().getContextClassLoader()).getURLs()) {
//                    System.err.println("\t- " + url.toString());
//                }
//                e.printStackTrace();
//                return null;
            }
        }
    }

    public TypeName collectionRawType(PropertySpec propertySpec) {
        if (propertySpec.typeSpec().cardinality().equals(PropertyCardinality.LIST)) {
            return this.collectionConfiguration.rawValueList();
        } else if (propertySpec.typeSpec().cardinality().equals(PropertyCardinality.SET)) {
            return this.collectionConfiguration.rawValueSet();
        } else {
            return null;
        }
    }

    public String fieldName(PropertySpec propertySpec) {
        Optional<Matcher> hint = propertySpec.matchingHint("property:raw\\(([^)]*)\\)");
        if(hint.isPresent()) {
            return hint.get().group(1);
        } else {
            return propertySpec.name();
        }
    }


    public String rootPackage() {
        return rootPackage;
    }

    public ValueSpec valueSpec() {
        return valueSpec;
    }

    public ValueCollectionConfiguration collectionConfiguration() {
        return collectionConfiguration;
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

    public ClassName optionalValueType() {
        return optionalValueType;
    }



    public ClassName namesType() {
        return namesType;
    }


    public String witherMethodName(PropertySpec propertySpec) {
        return "with" + capitalizedFirst(propertySpec.name());
    }
    public String changedWitherMethodName(PropertySpec propertySpec) {
        return "withChanged" + capitalizedFirst(propertySpec.name());
    }
    public String fromWitherMethodName(PropertySpec propertySpec) {
        return "with" + capitalizedFirst(propertySpec.name());
    }

    public String enumTypeName(String name) {
        return this.capitalizedFirst(name);
    }


    private static String capitalizedFirst(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }

    static public Set<String> localDateTypeRefs = new HashSet<>();
    static {
        localDateTypeRefs.add(TypeToken.DATE.getImplementationType());
        localDateTypeRefs.add(TypeToken.DATE_TIME.getImplementationType());
        localDateTypeRefs.add(TypeToken.TIME.getImplementationType());
    }

    public boolean isDateOrTimeType(String typeRef) {
        return localDateTypeRefs.contains(typeRef);
    }

    static public Set<String> numberTypes = new HashSet<>();
    static {
        numberTypes.add(TypeToken.INT.getImplementationType());
        numberTypes.add(TypeToken.LONG.getImplementationType());
        numberTypes.add(TypeToken.FLOAT.getImplementationType());
        numberTypes.add(TypeToken.DOUBLE.getImplementationType());
    }


    public boolean isNumber(String typeRef) {
        return numberTypes.contains(typeRef);
    }
}
