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
import java.util.ArrayList;
import java.util.List;

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
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) );
            this.writePhpFile( packageDestination, valueSpec );
        }
        for( PhpEnum enumValue : enumValues ) {
            File packageDestination = new File( rootDirectory, enumValue.packageName().replace( ".", "/" ) );
            this.writePhpEnum( packageDestination, enumValue );
        }
    }

    private void writePhpEnum( File packageDestination, PhpEnum enumValue ) throws IOException {
        PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, enumValue.packageName(), enumValue.name() );
        fileWriter.write( enumValue );
    }

    private void writePhpFile( File packageDestination, PackagedValueSpec valueObject ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueObject );
        if( phpValueObject != null ) {
            PhpTypeClassWriter fileWriter = new PhpTypeClassWriter( packageDestination, valueObject.packagename(), valueObject.valueSpec().name() );
            fileWriter.write( phpValueObject );
        }
    }
}
