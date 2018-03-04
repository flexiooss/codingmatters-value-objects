package org.codingmatters.value.objects;

import org.codingmatters.value.objects.spec.Spec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PumlClassFromSpecGenerator {
    private final Spec spec;
    private final String rootPackage;
    private final File rootDirectory;

    public PumlClassFromSpecGenerator(Spec spec, String rootPackage, File toDirectory) {
        this.spec = spec;
        this.rootPackage = rootPackage;
        this.rootDirectory = toDirectory;
    }

    public void generate() throws IOException {
        this.rootDirectory.mkdirs();

        File classesFile = new File(this.rootDirectory, this.rootPackage + ".classes.puml");
        classesFile.createNewFile();

        try(FormattedWriter out = new FormattedWriter(new FileWriter(classesFile))) {
            out.appendLine("@startuml");
            new PackageGenerator(this.spec.valueSpecs(), this.rootPackage).generate(out);
            out.appendLine("@enduml");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
