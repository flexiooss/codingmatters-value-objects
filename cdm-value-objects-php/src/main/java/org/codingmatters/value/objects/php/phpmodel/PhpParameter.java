package org.codingmatters.value.objects.php.phpmodel;

import org.codingmatters.value.objects.php.TypeTokenPhp;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.io.IOException;
import java.util.Locale;

public class PhpParameter {

    private String name;
    private PropertyTypeSpec type;

    public PhpParameter( String name, PropertyTypeSpec type ) {
        this.name = name;
        this.type = type;
    }



    public String name() {
        return this.name;
    }

    public String type() {
//        return this.type.typeRef();
        try {
            return getTypeName( this.type );
        } catch( IOException e ) {
            e.printStackTrace();
            return "";
        }
    }

    private String getTypeName( PropertyTypeSpec fieldSpec ) throws IOException {
        if( fieldSpec == null ){
            return "";
        }
        if( fieldSpec.typeKind() == TypeKind.JAVA_TYPE ) {
            return TypeTokenPhp.parse( fieldSpec.typeRef() ).getImplementationType();
        } else if( fieldSpec.typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT ) {
//            if( fieldSpec.name().equals( "$list" ) ) {
//                return "array";
//            }
            return firstLetterUpperCase( fieldSpec.typeRef() );
        } else if( fieldSpec.typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT ) {
            return firstLetterUpperCase(
                    fieldSpec.typeRef().substring( fieldSpec.typeRef().lastIndexOf( "." ) + 1 )
            );
        } else if( fieldSpec.typeKind() == TypeKind.ENUM ) {
            return firstLetterUpperCase( fieldSpec.typeRef() );
        } else {
            throw new IOException( "Impossible to recognize type : " + fieldSpec.typeRef() );
        }
    }

    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }
}


