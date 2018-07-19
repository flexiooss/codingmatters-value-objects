package org.codingmatters.value.objects.php.generator;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.spec.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.codingmatters.value.objects.spec.TypeKind.ENUM;
import static org.codingmatters.value.objects.spec.TypeKind.IN_SPEC_VALUE_OBJECT;

public class PhpSpecPreprocessor {

    private final Spec spec;
    private final String packageName;

    public PhpSpecPreprocessor( Spec spec, String rootPackage ) {
        this.spec = spec;
        this.packageName = rootPackage;
    }

    public List<PackagedValueSpec> packagedValueSpec() {
        List<PackagedValueSpec> result = new LinkedList<>();
        for( ValueSpec valueSpec : this.spec.valueSpecs() ) {
            result.addAll( this.preProcess( valueSpec, this.packageName ) );
        }
        return result;
    }

    private List<PackagedValueSpec> preProcess( ValueSpec valueSpec, String valuePackage ) {
        List<PackagedValueSpec> valueSpecs = new ArrayList<>();
        ValueSpec.Builder rootValueSpec = ValueSpec.valueSpec().name( valueSpec.name() );

        for( PropertySpec propertySpec : valueSpec.propertySpecs() ) {
            if( propertySpec.typeSpec().typeKind() == TypeKind.EMBEDDED ) {
                if( isListOrSet( propertySpec ) ) {
                    if( propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind() == ENUM ) {
                        if( propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef().equals( capitalizedFirst( valueSpec.name().toLowerCase() ) + capitalizedFirst( propertySpec.name() ) + propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).name() ) ) {
                            String enumName = uncapitalizedFirst( valueSpec.name() ) + capitalizedFirst( propertySpec.name() );
                            rootValueSpec.addProperty( createEnumProperty(
                                    propertySpec,
                                    propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().enumValues(),
                                    valuePackage + "." + enumName ) );
                        } else {
                            rootValueSpec.addProperty( createEnumProperty(
                                    propertySpec,
                                    null,
                                    propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() ) );
                        }
                    } else {
                        rootValueSpec.addProperty( PropertySpec.property()
                                .name( propertySpec.name() )
                                .type( new PropertyTypeSpec.Builder()
                                        .cardinality( propertySpec.typeSpec().cardinality() )
                                        .typeKind( propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind() )
                                        .typeRef( propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() )
                                ).build()
                        );
                    }
                } else {
                    ValueSpec embeddedValueSpec = createEmbeddedValueSpec( propertySpec );
                    String embeddedPackage = valuePackage + "." + valueSpec.name().toLowerCase();
                    List<PackagedValueSpec> embeddedSpecs = preProcess( embeddedValueSpec, embeddedPackage );
                    valueSpecs.addAll( embeddedSpecs );
                    rootValueSpec.addProperty( createInSpecPropertyForEmbeddedType( propertySpec, embeddedPackage ) );
                }
            } else if( isEnum( propertySpec ) ) {
                if( propertySpec.typeSpec().typeRef().equals( capitalizedFirst( valueSpec.name().toLowerCase() ) + capitalizedFirst( propertySpec.name() ) ) ) {
                    String enumName = uncapitalizedFirst( valueSpec.name() ) + capitalizedFirst( propertySpec.name() );
                    rootValueSpec.addProperty( createEnumProperty(
                            propertySpec,
                            propertySpec.typeSpec().enumValues(),
                            valuePackage + "." + enumName ) );
                } else {
                    rootValueSpec.addProperty( createEnumProperty(
                            propertySpec,
                            null,
                            propertySpec.typeSpec().typeRef() ) );
                }
            } else {
                rootValueSpec.addProperty( propertySpec );
            }
        }
        valueSpecs.add( new PackagedValueSpec( valuePackage, rootValueSpec.build() ) );
        return valueSpecs;
    }



    private PropertySpec createEnumProperty( PropertySpec propertySpec, String[] enumValues, String typeRef ) {
        PropertyTypeSpec.Builder type = PropertyTypeSpec.type()
                .typeRef( typeRef )
                .typeKind( TypeKind.ENUM )
                .cardinality( propertySpec.typeSpec().cardinality() );

        if( enumValues != null ) {
            type.enumValues( enumValues );
        }

        return PropertySpec.property()
                .name( propertySpec.name() )
                .type( type )
                .build();
    }

    private boolean isEnum( PropertySpec propertySpec ) {
        return propertySpec.typeSpec().typeKind() == ENUM;
    }

    private PropertySpec createInSpecPropertyForEmbeddedType( PropertySpec propertySpec, String embeddedPackage ) {
        return PropertySpec.property()
                .name( propertySpec.name() )
                .type( PropertyTypeSpec.type()
                        .typeKind( IN_SPEC_VALUE_OBJECT )
                        .typeRef( embeddedPackage + "." + capitalizedFirst( propertySpec.name() ) )
                        .cardinality( propertySpec.typeSpec().cardinality() )
                ).build();
    }

    private ValueSpec createEmbeddedValueSpec( PropertySpec propertySpec ) {
        ValueSpec.Builder embeddedValueSpecBuilder = ValueSpec.valueSpec()
                .name( propertySpec.name() );
        for( PropertySpec embeddedPropSpec : propertySpec.typeSpec().embeddedValueSpec().propertySpecs() ) {
            embeddedValueSpecBuilder.addProperty( embeddedPropSpec );
        }
        return embeddedValueSpecBuilder.build();
    }

    private boolean isListOrSet( PropertySpec propertySpec ) {
        return (propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST || propertySpec.typeSpec().cardinality() == PropertyCardinality.SET);
    }

    static private String capitalizedFirst( String str ) {
        return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
    }

    static private String uncapitalizedFirst( String str ) {
        return str.substring( 0, 1 ).toLowerCase() + str.substring( 1 );
    }

}
