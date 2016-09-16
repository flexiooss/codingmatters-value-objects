package org.codingmatters.value.objects.maven.plugin;

import org.codingmatters.value.objects.SpecCodeGenerator;
import org.codingmatters.value.objects.SpecReader;
import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nelt on 9/16/16.
 */
public class GenerateDeleguate implements Runnable {

    private final File inputSpecification;
    private final File outputDirectory;

    public GenerateDeleguate(File inputSpecification, File outputDirectory) {
        this.inputSpecification = inputSpecification;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void run() {
        this.outputDirectory.mkdirs();
        SpecReader reader = new SpecReader();
        try {
            try(InputStream in = new FileInputStream(this.inputSpecification)) {
                new SpecCodeGenerator(reader.read(in), "org.generated").generateTo(this.outputDirectory);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SpecSyntaxException e) {
            e.printStackTrace();
        } catch (LowLevelSyntaxException e) {
            e.printStackTrace();
        }
    }
}
