package org.codingmatters.value.objects;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;
import org.codingmatters.value.objects.spec.Spec;
import org.codingmatters.value.objects.spec.ValueSpec;

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
        this.generateSvg(classesFile);

        for (ValueSpec valueSpec : this.spec.valueSpecs()) {
            classesFile = new File(this.rootDirectory, this.rootPackage + "." + valueSpec.name() + ".classes.puml");
            classesFile.createNewFile();

            try(FormattedWriter out = new FormattedWriter(new FileWriter(classesFile))) {
                out.appendLine("@startuml");
                new PackageGenerator(this.spec.valueSpecs(), this.rootPackage).generate(valueSpec, out);
                out.appendLine("@enduml");
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.generateSvg(classesFile);
        }

    }

    private void generateSvg(File classesFile) throws IOException {
        SourceFileReader sourceFileReader = new SourceFileReader(
                classesFile,
                classesFile.getParentFile(),
                new FileFormatOption(FileFormat.SVG));
        if(sourceFileReader.hasError()) {
            for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
                System.err.println(generatedImage);
            }
            throw new AssertionError("failed generating svg from puml : " + classesFile.getAbsolutePath());
        }
        for (GeneratedImage generatedImage : sourceFileReader.getGeneratedImages()) {
            System.out.println("generated : " + generatedImage);
        }
    }


}
