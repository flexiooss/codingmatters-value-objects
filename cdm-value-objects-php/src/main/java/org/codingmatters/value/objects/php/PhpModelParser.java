package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpMethod;
import org.codingmatters.value.objects.php.phpmodel.PhpPackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpParameter;
import org.codingmatters.value.objects.php.phpmodel.PhpPropertySpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.PropertyTypeSpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.Locale;

public class PhpModelParser {

    public PhpPackagedValueSpec parseValueSpec( PackagedValueSpec valueSpec ) {

        if( valueSpec.valueSpec().propertySpecs().size() == 1 &&
                (valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$list" ) ||
                        valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$set" ))
                ) {
            // case of set/list
            return getArrayObjectClass( valueSpec );
        }
        return getDummyPhpClass( valueSpec );
    }

    private PhpPackagedValueSpec getArrayObjectClass( PackagedValueSpec valueSpec ) {
        PhpPackagedValueSpec phpPackagedValueSpec = new PhpPackagedValueSpec( valueSpec.packagename(), valueSpec.valueSpec().name() );

        phpPackagedValueSpec.addImport( "\\ArrayObject" );

        phpPackagedValueSpec.extend( PropertyTypeSpec.type().typeKind( TypeKind.EXTERNAL_VALUE_OBJECT ).typeRef( "ArrayObject" ).build() );
        PhpMethod constructor = new PhpMethod( "__construct" );
        constructor.addInstruction( "parent::__construct(array(), self::ARRAY_AS_PROPS);" );
        phpPackagedValueSpec.addMethod( constructor );

        return phpPackagedValueSpec;
    }

    private PhpPackagedValueSpec getDummyPhpClass( PackagedValueSpec valueSpec ) {
        PhpPackagedValueSpec phpPackagedValueSpec = new PhpPackagedValueSpec( valueSpec.packagename(), valueSpec.valueSpec().name() );

        for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
            PhpPropertySpec property = new PhpPropertySpec( propertySpec.typeSpec(), propertySpec.name() );

            if( propertySpec.typeSpec().typeKind() == TypeKind.EXTERNAL_VALUE_OBJECT ) {
                phpPackagedValueSpec.addImport( propertySpec.typeSpec().typeRef().replace( ".", "\\" ) );
            } else if( propertySpec.typeSpec().typeKind() == TypeKind.ENUM ) {
                phpPackagedValueSpec.addImport( valueSpec.packagename().replace( ".", "\\" ) + "\\" + valueSpec.valueSpec().name() + "\\" + propertySpec.typeSpec().typeRef() );
            } else if( propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
                if( propertySpec.typeSpec().typeRef().contains( "date")){
                    phpPackagedValueSpec.addImport( "\\DateTime" );
                }
            }

            phpPackagedValueSpec.addProperty( property );

            phpPackagedValueSpec.addMethod( createGetter( propertySpec ) );
            phpPackagedValueSpec.addMethod( createSetter( propertySpec ) );
        }
        return phpPackagedValueSpec;
    }

    private PhpMethod createSetter( PropertySpec propertySpec ) {
        PhpMethod phpMethod = new PhpMethod( "with" + firstLetterUpperCase( propertySpec.name() ) );
        phpMethod.addParameters( new PhpParameter( propertySpec.name(), propertySpec.typeSpec() ) );
        phpMethod.addInstruction( "$this->" + propertySpec.name() + " = " + "$" + propertySpec.name() );
        phpMethod.addInstruction( "return $this" );
        return phpMethod;
    }

    private PhpMethod createGetter( PropertySpec propertySpec ) {
        PhpMethod phpMethod = new PhpMethod( propertySpec.name() );
        phpMethod.addInstruction( "return $this->" + propertySpec.name() );
        return phpMethod;
    }


    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

}
