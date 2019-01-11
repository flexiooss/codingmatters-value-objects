package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.generation.Naming;
import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.util.Locale;

public class JsModelParser {


    private final org.codingmatters.value.objects.generation.Naming naming = new Naming();

    public JsPackagedValueSpec parseValueSpec( PackagedValueSpec valueSpec ) {
        return getDummyJsClass( valueSpec );
    }

    public boolean needToBeGenerated( PackagedValueSpec valueSpec ) {
        return !(valueSpec.valueSpec().propertySpecs().size() == 1 && valueSpec.valueSpec().propertySpecs().get( 0 ).name().equals( "$value-object" ));
    }

    private JsPackagedValueSpec getDummyJsClass( PackagedValueSpec valueSpec ) {
        JsPackagedValueSpec phpPackagedValueSpec = new JsPackagedValueSpec( valueSpec.packagename(), valueSpec.valueSpec().name() );

        for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
            JsPropertySpec property = new JsPropertySpec( propertySpec.typeSpec(), naming.property( propertySpec.name() ), propertySpec.name() );
            phpPackagedValueSpec.addProperty( property );

            phpPackagedValueSpec.addMethod( createGetter( propertySpec ) );
            phpPackagedValueSpec.addMethod( createSetter( propertySpec, firstLetterUpperCase( valueSpec.valueSpec().name() ) ) );
        }
        return phpPackagedValueSpec;
    }

    private JsMethod createSetter( PropertySpec propertySpec, String returnType ) {
        String propertyName = naming.property( propertySpec.name() );
        JsMethod phpMethod = new JsMethod( "with" + firstLetterUpperCase( propertyName ) );
        String type;
        if( propertySpec.typeSpec().typeKind() == TypeKind.JAVA_TYPE ) {
            type = propertySpec.typeSpec().typeRef();
        } else if( propertySpec.typeSpec().typeRef() != null ) {
            type = "\\" + propertySpec.typeSpec().typeRef().replace( ".", "\\" );
        } else {
            type = "";
        }
        phpMethod.addParameters( new JsParameter( propertyName, type ) );
        phpMethod.addInstruction( "$this->" + propertyName + " = " + "$" + propertyName );
        phpMethod.addInstruction( "return $this" );
        phpMethod.returnType( returnType );
        return phpMethod;
    }

    private JsMethod createGetter( PropertySpec propertySpec ) {
        String propertyName = naming.property( propertySpec.name() );
        JsMethod phpMethod = new JsMethod( propertyName );
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

