package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.generation.preprocessor.SpecPreprocessor;
import org.codingmatters.value.objects.php.phpmodel.PhpEnum;
import org.codingmatters.value.objects.php.phpmodel.PhpPackagedValueSpec;
import org.codingmatters.value.objects.spec.PropertySpec;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.TypeKind;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpecPhpGenerator {

    private final Spec spec;
    private final String rootPackage;
    private final File rootDirectory;

    public SpecPhpGenerator( Spec spec, String rootPackage, File targetDirectory ) {
        this.spec = spec;
        this.rootPackage = rootPackage;
        this.rootDirectory = targetDirectory;
    }

    public void generate() throws IOException {
        List<PackagedValueSpec> packagedValueSpecs = new SpecPreprocessor( this.spec, this.rootPackage ).packagedValueSpec();
        List<PhpEnum> enumValues = new ArrayList<>();
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
                if( propertySpec.typeSpec().typeKind() == TypeKind.ENUM ) {
                    enumValues.add( new PhpEnum(
                            propertySpec.typeSpec().typeRef().replace( "$list", "" ).replace( "$set", "" ),
                            String.join( ".", valueSpec.packagename(), valueSpec.valueSpec().name().replace( "$list", "" ).replace( "$set", "" ) ),
                            propertySpec.typeSpec().enumValues() )
                    );
                }
            }
        }
        Map<String, String> classReferencesContext = new HashMap<>();
        for( PhpEnum enumValue : enumValues ) {
            File packageDestination = new File( rootDirectory, enumValue.packageName().replace( ".", "/" ) );
            this.writePhpEnum( packageDestination, enumValue, classReferencesContext );
        }
        fillClassContext( classReferencesContext, packagedValueSpecs );
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) );
            this.writePhpFile( packageDestination, valueSpec, classReferencesContext );
        }
    }

    private void fillClassContext( Map<String, String> classReferencesContext, List<PackagedValueSpec> packagedValueSpecs ) {
        for( PackagedValueSpec packagedValueSpec : packagedValueSpecs ) {
            if( new PhpModelParser().needToBeGenerated( packagedValueSpec ) ) {
                String name = firstLetterUpperCase( packagedValueSpec.valueSpec().name() );
                classReferencesContext.put( name, packagedValueSpec.packagename() + "." + name );
            }else{
                classReferencesContext.put( firstLetterUpperCase( packagedValueSpec.valueSpec().name() ), packagedValueSpec.valueSpec().propertySpecs().get( 0 ).typeSpec().typeRef() );
            }
            for( PropertySpec propertySpec : packagedValueSpec.valueSpec().propertySpecs() ) {
                if( propertySpec.name().equals( "$value-object" ) ) {
                    String[] split = propertySpec.typeSpec().typeRef().split( "\\." );
                    classReferencesContext.put( split[split.length - 1], propertySpec.typeSpec().typeRef() );
                }
            }
        }
    }

    private void writePhpEnum( File packageDestination, PhpEnum enumValue, Map<String, String> classReferencesContext ) throws IOException {
        PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, enumValue.packageName(), enumValue.name() );
        fileWriter.write( enumValue, classReferencesContext );
    }

    private void writePhpFile( File packageDestination, PackagedValueSpec valueObject, Map<String, String> classReferencesContext ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueObject, classReferencesContext );
        if( phpValueObject != null ) {
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename(), valueObject.valueSpec().name() );
            fileWriter.write( phpValueObject, classReferencesContext );
        }
    }

    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

}
