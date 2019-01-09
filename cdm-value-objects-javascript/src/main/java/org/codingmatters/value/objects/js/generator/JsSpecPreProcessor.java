package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.generation.Naming;
import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.spec.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.codingmatters.value.objects.js.generator.Naming.firstLetterUpperCase;
import static org.codingmatters.value.objects.spec.TypeKind.*;

public class JsSpecPreProcessor {

    private final Spec spec;
    private final String packageName;
    private final org.codingmatters.value.objects.generation.Naming naming = new Naming();

    public JsSpecPreProcessor( Spec spec, String rootPackage ) {
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
        ValueSpec.Builder rootValueSpec = ValueSpec.valueSpec().name( firstLetterUpperCase( valueSpec.name() ) );

        for( PropertySpec propertySpec : valueSpec.propertySpecs() ) {
            String propertyName = naming.property( propertySpec.name() );
            if( propertySpec.typeSpec().typeKind() == TypeKind.EMBEDDED ) {
                PropertySpec listType;
                if( propertySpec.typeSpec().embeddedValueSpec().propertySpecs().isEmpty() ) {
                    listType = PropertySpec.property().build();
                } else {
                    listType = propertySpec.typeSpec().embeddedValueSpec().propertySpecs().get( 0 );
                }
                if( isListOrSet( propertySpec ) ) {
                    processListProperty( valueSpec, valuePackage, rootValueSpec, propertySpec, listType );
                } else if( "$value-object".equals( listType.name() ) ) {
                    // external value object
                    rootValueSpec.addProperty(
                            PropertySpec.property()
                                    .name( propertySpec.name() )
                                    .type( PropertyTypeSpec.type()
                                            .typeRef( listType.typeSpec().typeRef() )
                                            .typeKind( EXTERNAL_VALUE_OBJECT )
                                            .cardinality( PropertyCardinality.SINGLE )
                                    )
                                    .build()
                    );
                } else {
                    ValueSpec embeddedValueSpec = createEmbeddedValueSpec( propertySpec );
                    String embeddedPackage = valuePackage + "." + valueSpec.name().toLowerCase();
                    List<PackagedValueSpec> embeddedSpecs = preProcess( embeddedValueSpec, embeddedPackage );
                    valueSpecs.addAll( embeddedSpecs );
                    rootValueSpec.addProperty( createInSpecPropertyForEmbeddedType( propertySpec, embeddedPackage ) );
                }
            } else if( isEnum( propertySpec ) ) {
                String enumName = firstLetterUpperCase( valueSpec.name() ) + firstLetterUpperCase( propertyName );
                rootValueSpec.addProperty( createEnumProperty(
                        propertySpec.typeSpec().enumValues(),
                        valuePackage + "." + valueSpec.name().toLowerCase() + "." + enumName,
                        propertySpec.typeSpec().cardinality(),
                        propertySpec.name() )
                );
            } else if( propertySpec.typeSpec().typeKind() == IN_SPEC_VALUE_OBJECT ) {
                rootValueSpec.addProperty( PropertySpec.property()
                        .name( propertySpec.name() )
                        .type( PropertyTypeSpec.type()
                                .cardinality( propertySpec.typeSpec().cardinality() )
                                .typeKind( IN_SPEC_VALUE_OBJECT )
                                .typeRef( packageName + "." + firstLetterUpperCase( propertySpec.typeSpec().typeRef() ) )
                        )
                        .build() );
            } else {
                if( propertySpec.typeSpec().typeRef().contains( "date" ) || propertySpec.typeSpec().typeRef().contains( "time" ) ) {
                    rootValueSpec.addProperty( PropertySpec.property()
                            .name( propertySpec.name() )
                            .type( PropertyTypeSpec.type()
                                    .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                                    .typeRef( propertySpec.typeSpec().typeRef() )
                                    .cardinality( propertySpec.typeSpec().cardinality() )
                            )
                            .build() );
                } else {
                    rootValueSpec.addProperty( propertySpec );
                }
            }
        }
        valueSpecs.add( new PackagedValueSpec( valuePackage, rootValueSpec.build() ) );
        return valueSpecs;
    }

    private void processListProperty( ValueSpec valueSpec, String valuePackage, ValueSpec.Builder rootValueSpec, PropertySpec propertySpec, PropertySpec listType ) {
        String name = naming.property( propertySpec.name() );
        if( listType.typeSpec().typeKind() == ENUM ) {
            String enumName = firstLetterUpperCase( valueSpec.name() ) + firstLetterUpperCase( name );
            rootValueSpec.addProperty( createEnumProperty(
                    listType.typeSpec().enumValues(),
                    valuePackage + "." + valueSpec.name().toLowerCase() + "." + enumName + "List",
                    propertySpec.typeSpec().cardinality(),
                    name
            ) );
        } else if( listType.typeSpec().typeKind() == TypeKind.EMBEDDED ) {
            if( "$value-object".equals( listType.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).name() ) ) {
                rootValueSpec.addProperty(
                        PropertySpec.property()
                                .name( propertySpec.name() )
                                .type( PropertyTypeSpec.type()
                                        .cardinality( PropertyCardinality.LIST )
                                        .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                                        .typeRef( packageName + "." + valueSpec.name().toLowerCase() + "." + firstLetterUpperCase( valueSpec.name() ) + firstLetterUpperCase( name ) + "List" )
                                        .embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty( PropertySpec.property().type( PropertyTypeSpec.type().typeRef( listType.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() ) ).build() ).build() )
                                )
                                .build()
                );
            }
        } else if( listType.typeSpec().typeKind() == IN_SPEC_VALUE_OBJECT ) {
            PropertyTypeSpec.Builder elementType = PropertyTypeSpec.type().typeRef(
                    packageName + "." + firstLetterUpperCase( listType.typeSpec().typeRef() )
            );


            AnonymousValueSpec listElementType = AnonymousValueSpec.anonymousValueSpec()
                    .addProperty( PropertySpec.property().type( elementType ).build() ).build();

            String listTypeName = firstLetterUpperCase( valueSpec.name() ) + firstLetterUpperCase( name ) + "List";

            rootValueSpec.addProperty( PropertySpec.property()
                    .name( propertySpec.name() )
                    .type( new PropertyTypeSpec.Builder()
                            .cardinality( propertySpec.typeSpec().cardinality() )
                            .typeKind( listType.typeSpec().typeKind() )
                            .typeRef( packageName + "." + valueSpec.name().toLowerCase() + "." + listTypeName )
                            .embeddedValueSpec( listElementType )
                    ).build()
            );
        } else {
            PropertyTypeSpec.Builder list;
            if( listType.typeSpec().typeRef().contains( "date" ) || listType.typeSpec().typeRef().contains( "time" ) ) {
                list = PropertyTypeSpec.type()
                        .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                        .typeRef( listType.typeSpec().typeRef() );
            } else {
                list = PropertyTypeSpec.type()
                        .typeKind( listType.typeSpec().typeKind() )
                        .typeRef( listType.typeSpec().typeRef() );
            }
            AnonymousValueSpec listElementType = AnonymousValueSpec.anonymousValueSpec()
                    .addProperty( PropertySpec.property().type( list ).build() ).build();

            PropertySpec prop = PropertySpec.property()
                    .name( propertySpec.name() )
                    .type( PropertyTypeSpec.type()
                            .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                            .typeRef( valuePackage + "." + valueSpec.name().toLowerCase() + "." + firstLetterUpperCase( valueSpec.name() ) + firstLetterUpperCase( name ) + "List" )
                            .cardinality( PropertyCardinality.LIST )
                            .embeddedValueSpec( listElementType )
                    )
                    .build();
            rootValueSpec.addProperty( prop );
        }
    }


    private PropertySpec createEnumProperty( String[] enumValues, String typeRef, PropertyCardinality cardinality, String propName ) {
        PropertyTypeSpec.Builder type = PropertyTypeSpec.type()
                .typeRef( typeRef )
                .typeKind( TypeKind.ENUM )
                .cardinality( cardinality );

        if( enumValues != null ) {
            type.enumValues( enumValues );
        }
//        if( embeddedType != null){
//            type.embeddedValueSpec( AnonymousValueSpec.anonymousValueSpec().addProperty( PropertySpec.property().type( PropertyTypeSpec.type().typeRef( embeddedType ) ).build() ).build() );
//        }

        return PropertySpec.property()
                .name( propName )
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
                        .typeRef( embeddedPackage + "." + firstLetterUpperCase( propertySpec.name() ) )
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


}
