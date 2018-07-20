package org.codingmatters.value.objects.php.generator;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpMethod;
import org.codingmatters.value.objects.php.phpmodel.PhpPackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpParameter;
import org.codingmatters.value.objects.php.phpmodel.PhpPropertySpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.Locale;
import java.util.Map;

public class PhpModelParser {

    public PhpPackagedValueSpec parseValueSpec( PackagedValueSpec valueSpec, Map<String, String> classReferencesContext ) {
        return getDummyPhpClass( valueSpec, classReferencesContext );
    }

    public boolean needToBeGenerated( PackagedValueSpec valueSpec ) {
        return !(valueSpec.valueSpec().propertySpecs().size() == 1 && valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$value-object" ));
    }

    private PhpPackagedValueSpec getDummyPhpClass( PackagedValueSpec valueSpec, Map<String, String> classReferencesContext ) {
        PhpPackagedValueSpec phpPackagedValueSpec = new PhpPackagedValueSpec( valueSpec.packagename(), valueSpec.valueSpec().name() );

        for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
            PhpPropertySpec property = new PhpPropertySpec( propertySpec.typeSpec(), propertySpec.name() );

            if( propertySpec.typeSpec().typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT ) {
                String[] split = propertySpec.typeSpec().typeRef().split( "\\." );
                if( classReferencesContext.containsKey( split[split.length - 1] ) ) {
                    phpPackagedValueSpec.addImport( classReferencesContext.get( split[split.length - 1] ) );
                } else {
                    phpPackagedValueSpec.addImport( propertySpec.typeSpec().typeRef().replace( ".", "\\" ) );
                }
            } else if( propertySpec.typeSpec().typeKind() == TypeKind.ENUM ) {
                phpPackagedValueSpec.addImport( valueSpec.packagename().replace( ".", "\\" ) + "\\" + valueSpec.valueSpec().name().toLowerCase() + "\\" + propertySpec.typeSpec().typeRef().substring( propertySpec.typeSpec().typeRef().lastIndexOf( "." ) + 1 ) );
            } else if( propertySpec.typeSpec().typeKind() == TypeKind.IN_SPEC_VALUE_OBJECT ) {
                phpPackagedValueSpec.addImport( propertySpec.typeSpec().typeRef() );
            } else if( propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
                if( propertySpec.typeSpec().typeRef().contains( "date" ) ) {
                    phpPackagedValueSpec.addImport( "io.flexio.utils.FlexDate" );
                } else if( propertySpec.name().equals( "$value-object" ) ) {
                    phpPackagedValueSpec.addImport( propertySpec.typeSpec().typeRef() );
                }
            }
            phpPackagedValueSpec.addProperty( property );

            phpPackagedValueSpec.addMethod( createGetter( propertySpec, classReferencesContext ) );
            phpPackagedValueSpec.addMethod( createSetter( propertySpec, firstLetterUpperCase( valueSpec.valueSpec().name() ) ) );
        }
        return phpPackagedValueSpec;
    }

    private PhpMethod createSetter( PropertySpec propertySpec, String returnType ) {
        PhpMethod phpMethod = new PhpMethod( "with" + firstLetterUpperCase( propertySpec.name() ) );
        String type;
        if( propertySpec.typeSpec().typeRef() != null ) {
            type = propertySpec.typeSpec().typeRef().substring( propertySpec.typeSpec().typeRef().lastIndexOf( "." ) + 1 );
        } else {
            type = "";
        }
        phpMethod.addParameters( new PhpParameter( propertySpec.name(), type ) );
        phpMethod.addInstruction( "$this->" + propertySpec.name() + " = " + "$" + propertySpec.name() );
        phpMethod.addInstruction( "return $this" );
        phpMethod.returnType( returnType );
        return phpMethod;
    }

    private PhpMethod createGetter( PropertySpec propertySpec, Map<String, String> classReferencesContext ) {
        PhpMethod phpMethod = new PhpMethod( propertySpec.name() );
        phpMethod.addInstruction( "return $this->" + propertySpec.name() );
        String type;
        if( propertySpec.typeSpec().typeRef() != null ) {
            type = propertySpec.typeSpec().typeRef().substring( propertySpec.typeSpec().typeRef().lastIndexOf( "." ) + 1 );
        } else {
            type = "";
        }
        phpMethod.returnType( type );
        return phpMethod;
    }


    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

}
