package org.codingmatters.value.objects.js.generator.packages;

import org.codingmatters.value.objects.js.error.ProcessingException;
import org.codingmatters.value.objects.js.generator.JsFileWriter;

import java.io.File;

public class JsonPackageGenerator {

    private final File rootDirectory;

    public JsonPackageGenerator(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public void generatePackageJson(String vendor, String artifactId, String version, String rootPackage) throws ProcessingException {
        try (JsFileWriter write = new JsFileWriter(new File(rootDirectory, "package.json").getPath())) {
            write.line("{");
            write.line("\"hotballoon-shed\": {");
            write.line("\"module\": {");
            write.line("\"parent\": {");
            write.line("\"name\": \"@flexio-oss/js-commons-bundle\",");
            write.line("\"external\": true");
            write.line("}");
            write.flush();
        } catch (Exception e) {
            throw new ProcessingException("Error generating package.json file", e);
        }
    }
}
