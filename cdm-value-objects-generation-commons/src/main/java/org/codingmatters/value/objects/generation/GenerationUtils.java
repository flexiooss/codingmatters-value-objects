package org.codingmatters.value.objects.generation;

import java.io.File;

/**
 * Created by nelt on 3/30/17.
 */
public class GenerationUtils {
    static public File packageDir(File baseDir, String packageName) {
        return new File(baseDir, packageName.replaceAll(".", "/"));
    }
}
