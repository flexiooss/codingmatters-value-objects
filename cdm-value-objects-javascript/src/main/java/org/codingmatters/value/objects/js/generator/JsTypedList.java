package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;

public class JsTypedList {

    public static JsPackagedValueSpec createJsPackagedValueSpec( PackagedValueSpec valueSpec, PropertySpec listProperty ) {
        JsPackagedValueSpec listSpec = new JsPackagedValueSpec(
                valueSpec.packagename() + "." + valueSpec.valueSpec().name().toLowerCase(),
                capitalizedFirst( valueSpec.valueSpec().name() ) + capitalizedFirst( listProperty.name() ) + "List"
        );
        String type;
        String typeRef = listProperty.typeSpec().typeRef();
        if( listProperty.typeSpec().typeKind() != TypeKind.JAVA_TYPE ) {
            if( listProperty.typeSpec().typeKind() == TypeKind.ENUM ) {
                typeRef = replaceLast( listProperty.typeSpec().typeRef(), "List", "" );
                listSpec.addImport( typeRef );
            } else if( listProperty.typeSpec().typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT ) {
                typeRef = String.join( ".", listProperty.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() );
                listSpec.addImport( typeRef );
            } else {
                typeRef = listProperty.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeRef();
                if( listProperty.typeSpec().embeddedValueSpec().propertySpecs().get( 0 ).typeSpec().typeKind() != TypeKind.JAVA_TYPE ) {
                    listSpec.addImport( typeRef );
                }
            }
        }
        type = getTypeFromReference( typeRef );

        listSpec.addImport( "io.flexio.utils.TypedArray" );
        listSpec.extend( PropertyTypeSpec.type().typeKind( TypeKind.EXTERNAL_VALUE_OBJECT ).typeRef( "TypedArray" ).build() );

        JsMethod constructor = new JsMethod( "__construct" );
        constructor.addParameters( new JsParameter( "input = array() ", "" ) );
        String parentConstructorCall = "parent::__construct( function( " + type + " $item ){";
        switch( type ) {
            case "string":
                parentConstructorCall += "return strval( $item ); }, $input )";
                break;
            case "double":
            case "float":
                parentConstructorCall += "return floatval( $item ); }, $input )";
                break;
            case "int":
            case "long":
                parentConstructorCall += "return intval( $item ); }, $input )";
                break;
            case "bool":
                parentConstructorCall += "return boolval( $item ); }, $input )";
                break;
            case "\\ArrayObject":
                parentConstructorCall += "return $item; }, $input, true )"; // true to cast array to ArrayObject
                break;
            default:
                parentConstructorCall += "return $item; }, $input )";
        }
        constructor.addInstruction( parentConstructorCall );
        listSpec.addMethod( constructor );

        JsMethod addFunction = new JsMethod( "add" );
        addFunction.addParameters( new JsParameter( "item", type ) );
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

    public static String replaceLast( String string, String toReplace, String replacement ) {
        int pos = string.lastIndexOf( toReplace );
        if( pos > -1 ) {
            return string.substring( 0, pos )
                    + replacement
                    + string.substring( pos + toReplace.length(), string.length() );
        } else {
            return string;
        }
    }
}
