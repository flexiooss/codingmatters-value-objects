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
import java.util.Map;

public class PhpModelParser {

    public PhpPackagedValueSpec parseValueSpec( PackagedValueSpec valueSpec, Map<String, String> classReferencesContext ) {
        if( !needToBeGenerated( valueSpec ) ) {
            return null;
        }
        if( isArrayClass( valueSpec ) ) {
            return getArrayObjectClass( valueSpec, classReferencesContext );
        }
        return getDummyPhpClass( valueSpec, classReferencesContext );
    }

    public boolean needToBeGenerated( PackagedValueSpec valueSpec ) {
        return !(valueSpec.valueSpec().propertySpecs().size() == 1 && valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$value-object" ));
    }

    public boolean isArrayClass( PackagedValueSpec valueSpec ) {
        return valueSpec.valueSpec().propertySpecs().size() == 1 &&
                (valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$list" ) ||
                        valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$set" ));
    }

    private PhpPackagedValueSpec getArrayObjectClass( PackagedValueSpec valueSpec, Map<String, String> classReferencesContext ) {
        PhpPackagedValueSpec phpPackagedValueSpec = new PhpPackagedValueSpec( valueSpec.packagename(), valueSpec.valueSpec().name() );
        PropertySpec collectionProperty = valueSpec.valueSpec().propertySpecs().get( 0 );
        String arrayType = collectionProperty.typeSpec().typeRef().replace( collectionProperty.name(), "" );
        PropertyTypeSpec arrayTypeSpec = new PropertyTypeSpec.Builder()
                .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                .typeRef( arrayType )
                .build();
        PhpParameter typedParameter = new PhpParameter( "item", arrayTypeSpec );
        String type = typedParameter.type();

        phpPackagedValueSpec.addImport( "io.flexio.utils.TypedArray" );
        phpPackagedValueSpec.addImport( classReferencesContext.get( type ) );

        phpPackagedValueSpec.extend( PropertyTypeSpec.type().typeKind( TypeKind.EXTERNAL_VALUE_OBJECT ).typeRef( "TypedArray" ).build() );
        PhpMethod constructor = new PhpMethod( "__construct" );
        constructor.addParameters( new PhpParameter( "input = array()", null ) );
        constructor.addInstruction( "parent::__construct( " + type + "::class, $input )" );
        phpPackagedValueSpec.addMethod( constructor );

        PhpMethod addFunction = new PhpMethod( "add" );

        addFunction.addParameters( typedParameter );
        addFunction.addInstruction( "parent::append( $item )" );
        phpPackagedValueSpec.addMethod( addFunction );

        return phpPackagedValueSpec;
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
                phpPackagedValueSpec.addImport( valueSpec.packagename().replace( ".", "\\" ) + "\\" + valueSpec.valueSpec().name() + "\\" + propertySpec.typeSpec().typeRef() );
            } else if( propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
                if( propertySpec.typeSpec().typeRef().contains( "date" ) ) {
                    phpPackagedValueSpec.addImport( "io.flexio.utils.FlexDate" );
                } else if( propertySpec.name().equals( "$value-object" ) ) {
                    phpPackagedValueSpec.addImport( propertySpec.typeSpec().typeRef() );
                }
            }
            phpPackagedValueSpec.addProperty( property );

            phpPackagedValueSpec.addMethod( createGetter( propertySpec, classReferencesContext ) );
            phpPackagedValueSpec.addMethod( createSetter( propertySpec, classReferencesContext, firstLetterUpperCase( valueSpec.valueSpec().name() ) ) );
        }
        return phpPackagedValueSpec;
    }

    private PhpMethod createSetter( PropertySpec propertySpec, Map<String, String> classReferencesContext, String returnType ) {
        PhpMethod phpMethod = new PhpMethod( "with" + firstLetterUpperCase( propertySpec.name() ) );
        String[] split = propertySpec.typeSpec().typeRef().split( "\\." );
        if( classReferencesContext.containsKey( split[split.length - 1] ) ) {
            phpMethod.addParameters( new PhpParameter( propertySpec.name(), PropertyTypeSpec.type()
                    .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                    .typeRef( classReferencesContext.get( split[split.length - 1] ) )
                    .build() ) );
        } else {
            phpMethod.addParameters( new PhpParameter( propertySpec.name(), propertySpec.typeSpec() ) );
        }
        phpMethod.addInstruction( "$this->" + propertySpec.name() + " = " + "$" + propertySpec.name() );
        phpMethod.addInstruction( "return $this" );
        phpMethod.returnType( PropertyTypeSpec.type().typeKind( TypeKind.EXTERNAL_VALUE_OBJECT ).typeRef( returnType ).build() );
        return phpMethod;
    }

    private PhpMethod createGetter( PropertySpec propertySpec, Map<String, String> classReferencesContext ) {
        PhpMethod phpMethod = new PhpMethod( propertySpec.name() );
        phpMethod.addInstruction( "return $this->" + propertySpec.name() );
        String[] split = propertySpec.typeSpec().typeRef().split( "\\." );
        if( classReferencesContext.containsKey( split[split.length - 1] ) ) {
            phpMethod.returnType( PropertyTypeSpec.type()
                    .typeKind( TypeKind.EXTERNAL_VALUE_OBJECT )
                    .typeRef( classReferencesContext.get( split[split.length - 1] ) )
                    .build() );
        } else {
            phpMethod.returnType( propertySpec.typeSpec() );
        }
        return phpMethod;
    }


    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

}
