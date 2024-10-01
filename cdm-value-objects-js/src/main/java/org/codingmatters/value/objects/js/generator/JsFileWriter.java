package org.codingmatters.value.objects.js.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsFileWriter implements AutoCloseable {

    public static final String INDENTATION_UNITY = "  ";
    protected BufferedWriter writer;
    protected int indent;

    public JsFileWriter(String filePath) {
        this.indent = 0;
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            this.writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void line(String line) throws IOException {
        if (line.endsWith("{")) {
            writeLine(line);
            indent++;
        } else if (line.equals("}")) {
            indent--;
            writeLine(line);
        } else {
            writeLine(line);
        }
    }

    public void writeLine(String line) throws IOException {
        indent();
        writer.write(line);
        writer.newLine();
    }

    public void string(String line) throws IOException {
        writer.write(line);
    }

    public void indent() throws IOException {
        for (int i = 0; i < indent; i++) {
            writer.write(INDENTATION_UNITY);
        }
    }

    public void newLine() throws IOException {
        writer.newLine();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws Exception {
        writer.flush();
        writer.close();
    }

    public void unindent() {
        indent--;
    }
}
