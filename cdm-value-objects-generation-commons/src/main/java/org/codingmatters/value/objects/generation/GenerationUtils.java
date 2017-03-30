package org.codingmatters.value.objects.generation;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;

/**
 * Created by nelt on 3/30/17.
 */
public class GenerationUtils {
    static public File packageDir(File baseDir, String packageName) {
        return new File(baseDir, packageName.replaceAll(".", "/"));
    }

    static public void writeJavaFile(File packageDestination, String pack, TypeSpec type) throws IOException {
        JavaFile file = JavaFile.builder(pack, type).build();
        file.writeTo(packageDestination);
        if(System.getProperty("spec.code.generator.debug", "false").equals("true")) {
            file.writeTo(System.out);
        }
    }
}
