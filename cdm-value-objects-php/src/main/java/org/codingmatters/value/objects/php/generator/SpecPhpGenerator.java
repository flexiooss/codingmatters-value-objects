package org.codingmatters.value.objects.php.generator;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpEnum;
import org.codingmatters.value.objects.php.phpmodel.PhpPackagedValueSpec;
import org.codingmatters.value.objects.php.phpmodel.PhpTypedList;
import org.codingmatters.value.objects.spec.PropertyCardinality;
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
        List<PackagedValueSpec> packagedValueSpecs = new PhpSpecPreprocessor( this.spec, this.rootPackage ).packagedValueSpec();
        List<PhpEnum> enumValues = new ArrayList<>();
        ArrayList<PhpPackagedValueSpec> listValues = new ArrayList<>();
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
                if( propertySpec.typeSpec().typeKind() == TypeKind.ENUM ) {
                    System.out.println( "GENERATE ENUM " + Arrays.toString( propertySpec.typeSpec().enumValues() ) );
                    String typeRef = rootPackage + "." + valueSpec.valueSpec().name().toLowerCase() + "." + valueSpec.valueSpec().name() + firstLetterUpperCase( propertySpec.name() );
                    enumValues.add( new PhpEnum(
                            typeRef,
                            propertySpec.typeSpec().enumValues() )
                    );
                }
                if( propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST ) {
                    System.out.println( "Generate LIST:" + propertySpec.name() );
                    listValues.add( PhpTypedList.createPhpPackagedValueSpec( valueSpec, propertySpec ) );
                }
            }
        }
        // GENERATE ENUM
        Map<String, String> classReferencesContext = new HashMap<>();
        for( PhpEnum enumValue : enumValues ) {
            File packageDestination = new File( rootDirectory, enumValue.packageName().replace( ".", "/" ) );
            this.writePhpEnum( packageDestination, enumValue, classReferencesContext );
        }
        fillClassContext( classReferencesContext, packagedValueSpecs );

        //GENERATE LIST
        for( PhpPackagedValueSpec listValue : listValues ) {
            File packageDestination = new File( rootDirectory, listValue.packageName().replace( ".", "/" ) );
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, listValue.packageName(), listValue.name() );
            fileWriter.writeValueObject( listValue, classReferencesContext, false );
        }

        // GENERATE CLASSES
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) );
            this.writePhpFile( packageDestination, valueSpec, classReferencesContext );
        }

        // GENERATE READERS
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) + "/json" );
            this.writeJsonUtils( packageDestination, valueSpec, classReferencesContext );
        }
    }

    private void fillClassContext( Map<String, String> classReferencesContext, List<PackagedValueSpec> packagedValueSpecs ) {
        for( PackagedValueSpec packagedValueSpec : packagedValueSpecs ) {
            if( new PhpModelParser().needToBeGenerated( packagedValueSpec ) ) {
                String name = firstLetterUpperCase( packagedValueSpec.valueSpec().name() );
                classReferencesContext.put( name, packagedValueSpec.packagename() + "." + name );
            } else {
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

    private void writeJsonUtils( File packageDestination, PackagedValueSpec valueObject, Map<String, String> classReferencesContext ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueObject, classReferencesContext );
        if( phpValueObject != null ) {
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename() + ".json", valueObject.valueSpec().name() + "Reader" );
            fileWriter.writeReader( phpValueObject );
        }
    }

    private void writePhpEnum( File packageDestination, PhpEnum enumValue, Map<String, String> classReferencesContext ) throws IOException {
        PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, enumValue.packageName(), enumValue.name() );
        fileWriter.writeEnum( enumValue, classReferencesContext );
    }

    private void writePhpFile( File packageDestination, PackagedValueSpec valueObject, Map<String, String> classReferencesContext ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueObject, classReferencesContext );
        if( phpValueObject != null ) {
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename(), valueObject.valueSpec().name() );
            fileWriter.writeValueObject( phpValueObject, classReferencesContext, true );
        }
    }

    private String firstLetterUpperCase( String name ) {
        return name.substring( 0, 1 ).toUpperCase( Locale.ENGLISH ) + name.substring( 1 );
    }

}
