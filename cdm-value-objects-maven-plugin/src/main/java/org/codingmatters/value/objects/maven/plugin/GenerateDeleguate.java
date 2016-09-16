package org.codingmatters.value.objects.maven.plugin;

import java.io.File;

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

    }
}
