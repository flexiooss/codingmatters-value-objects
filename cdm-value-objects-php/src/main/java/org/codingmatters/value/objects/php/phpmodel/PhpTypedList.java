package org.codingmatters.value.objects.php.phpmodel;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;

public class PhpTypedList {

    public static PhpPackagedValueSpec createPhpPackagedValueSpec( PackagedValueSpec valueSpec, PropertySpec listProperty ) {
        PhpPackagedValueSpec listSpec = new PhpPackagedValueSpec(
                valueSpec.packagename() + "." + valueSpec.valueSpec().name().toLowerCase(),
                capitalizedFirst( valueSpec.valueSpec().name() ) + capitalizedFirst( listProperty.name() ) + "List"
        );
        String type;
        String typeRef = listProperty.typeSpec().typeRef();
        if( listProperty.typeSpec().typeKind() != TypeKind.JAVA_TYPE ) {
            if( listProperty.typeSpec().typeKind() == TypeKind.ENUM ) {
                typeRef = listProperty.typeSpec().typeRef().replace( "List", "" );
                listSpec.addImport( typeRef );
            } else if( listProperty.typeSpec().typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT ) {
                typeRef = String.join( ".", listProperty.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() );
                listSpec.addImport( typeRef );
            } else {
                typeRef = listProperty.typeSpec().typeRef().replace( "List", "" );
                listSpec.addImport( typeRef );
            }
        }
        type = getTypeFromReference( typeRef );

        listSpec.addImport( "io.flexio.utils.TypedArray" );
        listSpec.extend( PropertyTypeSpec.type().typeKind( TypeKind.EXTERNAL_VALUE_OBJECT ).typeRef( "TypedArray" ).build() );

        PhpMethod constructor = new PhpMethod( "__construct" );
        constructor.addParameters( new PhpParameter( "input = array() ", "" ) );
        constructor.addInstruction( "parent::__construct( " + type + "::class, $input )" );
        listSpec.addMethod( constructor );

        PhpMethod addFunction = new PhpMethod( "add" );
        addFunction.addParameters( new PhpParameter( "item", type ) );
        addFunction.addInstruction( "parent::append( $item )" );
        listSpec.addMethod( addFunction );

        return listSpec;
    }

    private static String getTypeFromReference( String typeRef ) {
        return typeRef.substring( typeRef.lastIndexOf( "." ) + 1 ).replace( "List", "" );
    }

    static private String capitalizedFirst( String str ) {
        return str.substring( 0, 1 ).toUpperCase() + str.substring( 1 );
    }
}
