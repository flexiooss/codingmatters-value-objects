package org.codingmatters.value.objects.maven.plugin;

import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.json.JsonFrameworkGenerator;
import org.codingmatters.value.objects.reader.SpecReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nelt on 4/13/17.
 */
public class GenerateJsonDeleguate {
    private final String destinationPackage;
    private final File inputSpecification;
    private final File outputDirectory;

    public GenerateJsonDeleguate(String destinationPackage, File inputSpecification, File outputDirectory) {
        this.destinationPackage = destinationPackage;
        this.inputSpecification = inputSpecification;
        this.outputDirectory = outputDirectory;
    }

    public void run() throws SpecSyntaxException, IOException, LowLevelSyntaxException {
        this.outputDirectory.mkdirs();
        SpecReader reader = new SpecReader();

        try(InputStream in = new FileInputStream(this.inputSpecification)) {
            new JsonFrameworkGenerator(reader.read(in), this.destinationPackage, this.outputDirectory).generate();
        }
    }
}
