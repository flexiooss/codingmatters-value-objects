package org.codingmatters.value.objects.generation.main;

import org.codingmatters.value.objects.exception.LowLevelSyntaxException;
import org.codingmatters.value.objects.exception.SpecSyntaxException;
import org.codingmatters.value.objects.generation.SpecCodeGenerator;
import org.codingmatters.value.objects.reader.SpecReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nelt on 4/14/17.
 */
public class Generate {
    public static void main(String[] args) {
        String inputFilePath = args[0];
        String destinationPackage = args[1];
        String outputDirectoryPath = args[2];

        SpecReader reader = new SpecReader();
        try(InputStream in = new FileInputStream(inputFilePath)) {
            new SpecCodeGenerator(reader.read(in), destinationPackage, new File(outputDirectoryPath)).generate();
        } catch (IOException | SpecSyntaxException | LowLevelSyntaxException e) {
            throw new RuntimeException("error generating spec", e);
        }
    }
}
