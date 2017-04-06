package org.codingmatters.value.objects.json;

import com.squareup.javapoet.TypeSpec;
import org.codingmatters.value.objects.generation.ValueConfiguration;
import org.codingmatters.value.objects.generation.preprocessor.PackagedValueSpec;
import org.codingmatters.value.objects.generation.preprocessor.SpecPreprocessor;
import org.codingmatters.value.objects.spec.Spec;

import java.io.File;
import java.io.IOException;

import static org.codingmatters.value.objects.generation.GenerationUtils.packageDir;
import static org.codingmatters.value.objects.generation.GenerationUtils.writeJavaFile;

/**
 * Created by nelt on 3/30/17.
 */
public class JsonFrameworkGenerator {
    private final Spec spec;
    private final String rootPackage;
    private final File rootDirectory;

    public JsonFrameworkGenerator(Spec spec, String rootPackage, File toDirectory) {
        this.spec = spec;
        this.rootPackage = rootPackage;
        this.rootDirectory = toDirectory;
    }

    public void generate() throws IOException {
        this.rootDirectory.mkdirs();

        for (PackagedValueSpec valueSpec : new SpecPreprocessor(this.spec, this.rootPackage).packagedValueSpec()) {
            this.generate(valueSpec);
        }
    }

    private void generate(PackagedValueSpec valueSpec) throws IOException {
        String valueJsonPackageName = valueSpec.packagename() + ".json";
        File jsonDir = packageDir(this.rootDirectory, valueJsonPackageName);
        jsonDir.mkdirs();

        ValueConfiguration types = new ValueConfiguration(this.rootPackage, valueSpec.packagename(), valueSpec.valueSpec());

        TypeSpec valueWriter = new ValueWriter(types, valueSpec.valueSpec().propertySpecs()).type();
        writeJavaFile(jsonDir, valueJsonPackageName, valueWriter);

        TypeSpec valueReader = new ValueReader(types, valueSpec.valueSpec().propertySpecs()).type();
        writeJavaFile(jsonDir, valueJsonPackageName, valueReader);
    }
}
