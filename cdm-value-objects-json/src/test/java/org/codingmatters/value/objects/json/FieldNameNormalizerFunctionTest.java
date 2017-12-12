package org.codingmatters.value.objects.json;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class FieldNameNormalizerFunctionTest {

    private final Function<String, String> normalizer = fieldName -> {
        if(fieldName == null) return null;
        if(fieldName.trim().equals("")) return "";
        fieldName = Arrays.stream(fieldName.split("(\\s|-)+")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1)).collect(Collectors.joining());
        fieldName =  fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
        return fieldName;
    };

    @Test
    public void uncapitalizeFirst() throws Exception {
        assertThat(this.normalizer.apply("StringProp"), is("stringProp"));
    }

    @Test
    public void camelCasePhrase() throws Exception {
        assertThat(this.normalizer.apply("string prop"), is("stringProp"));
    }

    @Test
    public void dashesAreWordSeparators() throws Exception {
        assertThat(this.normalizer.apply("string-prop"), is("stringProp"));
    }

    @Test
    public void nullString() throws Exception {
        assertThat(this.normalizer.apply(null), is(nullValue()));
    }

    @Test
    public void emptyString() throws Exception {
        assertThat(this.normalizer.apply(""), is(""));
    }

    @Test
    public void trailingSpacesString() throws Exception {
        assertThat(this.normalizer.apply("prop  "), is("prop"));
    }

    @Test
    public void oneChar() throws Exception {
        assertThat(this.normalizer.apply("a"), is("a"));
    }
}