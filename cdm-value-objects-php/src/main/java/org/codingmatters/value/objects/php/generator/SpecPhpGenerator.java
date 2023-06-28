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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpecPhpGenerator {

    private final Spec spec;
    private final String rootPackage;
    private final File rootDirectory;
    private boolean useReturnType = true;

    public SpecPhpGenerator( Spec spec, String rootPackage, File targetDirectory, boolean useReturnType ) {
        this.spec = spec;
        this.rootPackage = rootPackage;
        this.rootDirectory = targetDirectory;
        this.useReturnType = useReturnType;
    }


    public void generate() throws IOException {
        List<PackagedValueSpec> packagedValueSpecs = new PhpSpecPreprocessor( this.spec, this.rootPackage ).packagedValueSpec();
        List<PhpEnum> enumValues = new ArrayList<>();
        ArrayList<PhpPackagedValueSpec> listValues = new ArrayList<>();
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
                if( propertySpec.typeSpec().typeKind() == TypeKind.ENUM && propertySpec.typeSpec().isInSpecEnum()) {
                    String typeRef = propertySpec.typeSpec().typeRef();
                    if( propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST ) {
                        typeRef = typeRef.substring( 0, typeRef.length() - 4 );
                    }
                    enumValues.add( new PhpEnum(
                            typeRef,
                            propertySpec.typeSpec().enumValues() )
                    );
                }
                if (propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST) {
                    if (propertySpec.typeSpec().typeKind() != TypeKind.ENUM || propertySpec.typeSpec().isInSpecEnum()) {
                        listValues.add(PhpTypedList.createPhpPackagedValueSpec(valueSpec, propertySpec));
                    }
                }
            }
        }
        // GENERATE ENUM
        for( PhpEnum enumValue : enumValues ) {
            File packageDestination = new File( rootDirectory, enumValue.packageName().replace( ".", "/" ) );
            this.writePhpEnum( packageDestination, enumValue );
        }

        //GENERATE LIST
        for( PhpPackagedValueSpec listValue : listValues ) {
            File packageDestination = new File( rootDirectory, listValue.packageName().replace( ".", "/" ) );
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, listValue.packageName(), listValue.name() );
            fileWriter.writeValueObject( listValue, false, useReturnType );
        }

        // GENERATE CLASSES
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) );
            this.writePhpFile( packageDestination, valueSpec );
        }

        // GENERATE READERS
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) + "/json" );
            this.writeJsonUtils( packageDestination, valueSpec );
        }
    }

    private void writeJsonUtils( File packageDestination, PackagedValueSpec valueObject ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueObject );
        if( phpValueObject != null ) {
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename() + ".json", valueObject.valueSpec().name() + "Reader" );
            fileWriter.writeReader( phpValueObject );

            fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename() + ".json", valueObject.valueSpec().name() + "Writer" );
            fileWriter.writeWriter( phpValueObject );
        }
    }

    private void writePhpEnum( File packageDestination, PhpEnum enumValue ) throws IOException {
        PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, enumValue.packageName(), enumValue.name() );
        fileWriter.writeEnum( enumValue );
    }

    private void writePhpFile( File packageDestination, PackagedValueSpec valueObject ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueObject );
        if( phpValueObject != null ) {
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename(), valueObject.valueSpec().name() );
            fileWriter.writeValueObject( phpValueObject, true, useReturnType );
        }
    }

}
