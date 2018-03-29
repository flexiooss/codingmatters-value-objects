package org.codingmatters.value.objects;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public class FormattedWriter implements Closeable {
    private final Writer out;

    public FormattedWriter(Writer out) {
        this.out = out;
    }

    public FormattedWriter appendLine(String format, Object ... args) throws IOException {
        return this.append(format + "\n", args);
    }

    public FormattedWriter append(String format, Object ... args) throws IOException {
        this.out.append(String.format(format, args));
        return this;
    }

    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
