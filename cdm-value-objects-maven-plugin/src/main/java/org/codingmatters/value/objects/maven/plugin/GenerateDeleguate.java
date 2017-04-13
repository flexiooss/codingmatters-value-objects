package org.codingmatters.value.objects.maven.plugin;

import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nelt on 9/16/16.
 */
public class GenerateDeleguate {

    private final String destinationPackage;
    private final File inputSpecification;
    private final File outputDirectory;

    public GenerateDeleguate(String destinationPackage, File inputSpecification, File outputDirectory) {
        this.destinationPackage = destinationPackage;
        this.inputSpecification = inputSpecification;
        this.outputDirectory = outputDirectory;
    }

    public void run() throws SpecSyntaxException, IOException, LowLevelSyntaxException {
        this.outputDirectory.mkdirs();
        SpecReader reader = new SpecReader();

        try(InputStream in = new FileInputStream(this.inputSpecification)) {
            new SpecCodeGenerator(reader.read(in), this.destinationPackage, this.outputDirectory).generate();
        }
    }
}
