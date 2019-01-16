package org.codingmatters.value.objects.js.generator;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.php.generator.PhpModelParser;
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

public class SpecJsGenerator {

    private final Spec spec;
    private final String rootPackage;
    private final File rootDirectory;

    public SpecJsGenerator( Spec spec, String packageName, File rootDir ) {
        this.spec = spec;
        this.rootPackage = packageName;
        this.rootDirectory = rootDir;
    }

    public void generate() throws IOException {
        List<PackagedValueSpec> packagedValueSpecs = new JsSpecPreProcessor( this.spec, this.rootPackage ).packagedValueSpec();
        List<JsEnum> enumValues = new ArrayList<>();
        ArrayList<PhpPackagedValueSpec> listValues = new ArrayList<>();
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            for( PropertySpec propertySpec : valueSpec.valueSpec().propertySpecs() ) {
                if( propertySpec.typeSpec().typeKind() == TypeKind.ENUM ) {
                    String typeRef = propertySpec.typeSpec().typeRef();
                    if( propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST ) {
                        typeRef = typeRef.substring( 0, typeRef.length() - 4 );
                    }
                    enumValues.add( new JsEnum( typeRef, propertySpec.typeSpec().enumValues() ) );
                }
                if( propertySpec.typeSpec().cardinality() == PropertyCardinality.LIST ) {
                    listValues.add( PhpTypedList.createPhpPackagedValueSpec( valueSpec, propertySpec ) );
                }
            }
        }
        // GENERATE ENUM
        for( JsEnum enumValue : enumValues ) {
            File packageDestination = new File( rootDirectory, enumValue.packageName().replace( ".", "/" ) );
            packageDestination.mkdirs();
            this.writeJsEnum( packageDestination, enumValue );
        }


//        GENERATE LIST
        for( PhpPackagedValueSpec listValue : listValues ) {
            File packageDestination = new File( rootDirectory, listValue.packageName().replace( ".", "/" ) );
            packageDestination.mkdirs();
            JsClassWriter fileWriter = new JsClassWriter( packageDestination, listValue );
            fileWriter.writeList( listValue );
        }

        // GENERATE CLASSES
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) );
            packageDestination.mkdirs();
            this.writeJsClass( packageDestination, valueSpec );
        }
    }

    private void writeJsClass( File packageDestination, PackagedValueSpec valueSpec ) throws IOException {
        PhpPackagedValueSpec phpValueObject = new PhpModelParser().parseValueSpec( valueSpec );
        JsClassWriter fileWriter = new JsClassWriter( packageDestination, phpValueObject );
        fileWriter.writeClassAndBuilder();
    }

    private void writeJsEnum( File packageDestination, JsEnum enumValue ) throws IOException {
        JsEnumWriter fileWriter = new JsEnumWriter( packageDestination, enumValue );
        fileWriter.writeEnum();
    }

}
