package org.codingmatters.value.objects.php;

import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.generation.preprocessor.SpecPreprocessor;
import org.codingmatters.value.objects.spec.Spec;

import java.io.File;
import java.io.IOException;
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
        for( PackagedValueSpec valueSpec : packagedValueSpecs ) {
            File packageDestination = new File( rootDirectory, valueSpec.packagename().replace( ".", "/" ) );
            this.generateValueTypesTo( packageDestination, valueSpec );
        }
    }

    private void generateValueTypesTo( File packageDestination, PackagedValueSpec packagedValueSpec ) throws IOException {
        writePhpFile( packageDestination, packagedValueSpec.packagename(), packagedValueSpec );
    }

    private void writePhpFile( File packageDestination, String packageName, PackagedValueSpec valueInterface ) throws IOException {
        PhpTypeFileWriter fileWriter = new PhpTypeFileWriter( packageDestination, packageName, valueInterface );
        fileWriter.write();
    }
}
