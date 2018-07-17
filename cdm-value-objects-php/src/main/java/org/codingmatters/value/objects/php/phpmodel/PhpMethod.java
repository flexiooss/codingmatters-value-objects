package org.codingmatters.value.objects.php.phpmodel;

import org.codingmatters.value.objects.php.TypeTokenPhp;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhpMethod {

    private String name;
    private List<PhpParameter> parameters;
    private List<String> instructions;
    private PropertyTypeSpec retrunType;

    public PhpMethod( String name ) {
        this.name = name;
        this.parameters = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }

    public void addParameters( PhpParameter parameter ) {
        this.parameters.add( parameter );
    }

    public void addInstruction( String instruction ) {
        this.instructions.add( instruction );
    }

    public String name() {
        return this.name;
    }

    public void returnType( PropertyTypeSpec returnType ) {
        this.retrunType = returnType;
    }

    public List<PhpParameter> parameters() {
        return this.parameters;
    }

    public List<String> instructions() {
        return instructions;
    }

    public String type() {
        if( this.retrunType == null ) {
            return null;
        }
        try {
            return getTypeName( this.retrunType );
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
