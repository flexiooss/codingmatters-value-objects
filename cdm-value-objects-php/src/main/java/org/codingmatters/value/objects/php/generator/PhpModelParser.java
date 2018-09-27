package org.codingmatters.value.objects.php.generator;

import org.codingmatters.rest.api.generator.utils.Naming;
import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpMethod;
import org.codingmatters.value.objects.php.phpmodel.PhpPackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpParameter;
import org.codingmatters.value.objects.php.phpmodel.PhpPropertySpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.Locale;

public class PhpModelParser {

    private final Naming naming = new Naming();

    public PhpPackagedValueSpec parseValueSpec( PackagedValueSpec valueSpec ) {
        return getDummyPhpClass( valueSpec );
    }

    public boolean needToBeGenerated( PackagedValueSpec valueSpec ) {
        return !(valueSpec.valueSpec().propertySpecs().size() == 1 && valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$value-object" ));
    }

    private PhpPackagedValueSpec getDummyPhpClass( PackagedValueSpec valueSpec ) {
        PhpPackagedValueSpec phpPackagedValueSpec = new PhpPackagedValueSpec( valueSpec.packagename(), valueSpec.valueSpec().name() );

        for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
            PhpPropertySpec property = new PhpPropertySpec( propertySpec.typeSpec(), naming.property( propertySpec.name() ), propertySpec.name() );
            phpPackagedValueSpec.addProperty( property );

            phpPackagedValueSpec.addMethod( createGetter( propertySpec ) );
            phpPackagedValueSpec.addMethod( createSetter( propertySpec, firstLetterUpperCase( valueSpec.valueSpec().name() ) ) );
        }
        return phpPackagedValueSpec;
    }

    private PhpMethod createSetter( PropertySpec propertySpec, String returnType ) {
        String propertyName = naming.property( propertySpec.name() );
        PhpMethod phpMethod = new PhpMethod( "with" + firstLetterUpperCase( propertyName ) );
        String type;
        if( propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
            type = propertySpec.typeSpec().typeRef();
        } else if( propertySpec.typeSpec().typeRef() != null ) {
            type = "\\" + propertySpec.typeSpec().typeRef().replace( ".", "\\" );
        } else {
            type = "";
        }
        phpMethod.addParameters( new PhpParameter( propertyName, type ) );
        phpMethod.addInstruction( "$this->" + propertyName + " = " + "$" + propertyName );
        phpMethod.addInstruction( "return $this" );
        phpMethod.returnType( returnType );
        return phpMethod;
    }

    private PhpMethod createGetter( PropertySpec propertySpec ) {
        String propertyName = naming.property( propertySpec.name() );
        PhpMethod phpMethod = new PhpMethod( propertyName );
        phpMethod.addInstruction( "return $this->" + propertyName );
        String type;
        if( propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
            type = propertySpec.typeSpec().typeRef();
        } else if( propertySpec.typeSpec().typeRef() != null ) {
            type = "\\" + propertySpec.typeSpec().typeRef().replace( ".", "\\" );
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
