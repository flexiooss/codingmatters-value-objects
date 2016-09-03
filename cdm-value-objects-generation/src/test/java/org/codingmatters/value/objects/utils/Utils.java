package org.codingmatters.value.objects.utils;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nelt on 9/3/16.
 */
public class Utils {
    static public InputStream streamFor(String str) throws IOException {
        return new ByteArrayInputStream(str.getBytes("UTF-8"));
    }

    static public StrBuilder string() {
        return new StrBuilder();
    }

    static public class StrBuilder {
        private final StringBuilder internal = new StringBuilder();

        public StrBuilder line(String str) {
            return this.apnd(str).apnd("\n");
        }

        public StrBuilder line(String format, Object ... args) {
            return this.line(String.format(format, args));
        }

        public StrBuilder apnd(String str) {
            this.internal.append(str);
            return this;
        }

        public StrBuilder apnd(String format, Object ... args) {
            return this.apnd(String.format(format, args));
        }

        public String build() {
            return this.internal.toString();
        }
    }
}
