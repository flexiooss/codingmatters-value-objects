package org.codingmatters.value.objects.generation;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 5/6/17.
 */
public class NamingTest {

    @Test
    public void nullAndEmpty() throws Exception {
        assertThat(new Naming().type(null), Matchers.is(nullValue()));
        assertThat(new Naming().type(), Matchers.is(""));
        assertThat(new Naming().type(new String[0]), Matchers.is(""));

        assertThat(new Naming().property(null), Matchers.is(nullValue()));
        assertThat(new Naming().property(), Matchers.is(""));
        assertThat(new Naming().property(new String[0]), Matchers.is(""));
    }

    @Test
    public void dollarsRemoved() throws Exception {
        assertThat(new Naming().type("$camel", "case"), is("CamelCase"));
        assertThat(new Naming().type("camel", "$case"), is("CamelCase"));
        assertThat(new Naming().type("ca$mel", "cas$e"), is("CamelCase"));
        assertThat(new Naming().property("$camel", "case"), is("camelCase"));
        assertThat(new Naming().property("camel", "$case"), is("camelCase"));
        assertThat(new Naming().property("came$l", "ca$se"), is("camelCase"));
        assertThat(new Naming().property("came$l", "ca$se"), is("camelCase"));
    }

    @Test
    public void punctuationRemoved() throws Exception {
        String punctuation = "!\"#$%&'()*+,/:;<=>?@[]^`{|}~";
        for (char c : punctuation.toCharArray()) {
            assertThat(new Naming().type("" + c + "camel", "case"), is("CamelCase"));
            assertThat(new Naming().type("camel", "" + c + "case"), is("CamelCase"));
            assertThat(new Naming().type("ca" + c + "mel", "cas" + c + "e"), is("CamelCase"));
            assertThat(new Naming().property("" + c + "camel", "case"), is("camelCase"));
            assertThat(new Naming().property("camel", "" + c + "case"), is("camelCase"));
            assertThat(new Naming().property("came" + c + "l", "ca" + c + "se"), is("camelCase"));
        }
    }

    @Test
    public void underscoreNotRemoved() throws Exception {
        assertThat(new Naming().type("" + '_' + "camel", "case"), is("_camelCase"));
        assertThat(new Naming().type("camel", "" + '_' + "case"), is("Camel_case"));
        assertThat(new Naming().type("ca" + '_' + "mel", "cas" + '_' + "e"), is("Ca_melCas_e"));
        assertThat(new Naming().property("" + '_' + "camel", "case"), is("_camelCase"));
        assertThat(new Naming().property("camel", "" + '_' + "case"), is("camel_case"));
        assertThat(new Naming().property("came" + '_' + "l", "ca" + '_' + "se"), is("came_lCa_se"));
    }



    @Test
    public void camelCase() throws Exception {
        assertThat(new Naming().type("camel", "case"), is("CamelCase"));
        assertThat(new Naming().property("camel", "case"), is("camelCase"));
    }

    @Test
    public void dashesAreWordSeparators() throws Exception {
        assertThat(new Naming().type("camel-case"), is("CamelCase"));
        assertThat(new Naming().type("camel------case"), is("CamelCase"));
        assertThat(new Naming().type("-camel-case"), is("CamelCase"));
        assertThat(new Naming().property("camel-case"), is("camelCase"));
        assertThat(new Naming().property("camel-------case"), is("camelCase"));
        assertThat(new Naming().property("-camel-case"), is("camelCase"));
    }

    @Test
    public void dotsAreWordSeparators() throws Exception {
        assertThat(new Naming().type("camel.case"), is("CamelCase"));
        assertThat(new Naming().type("camel......case"), is("CamelCase"));
        assertThat(new Naming().type(".camel.case"), is("CamelCase"));
        assertThat(new Naming().property("camel.case"), is("camelCase"));
        assertThat(new Naming().property("camel.......case"), is("camelCase"));
        assertThat(new Naming().property(".camel.case"), is("camelCase"));
    }

    @Test
    public void spacesAreWordSeparators() throws Exception {
        assertThat(new Naming().type("camel case"), is("CamelCase"));
        assertThat(new Naming().type("camel      case"), is("CamelCase"));
        assertThat(new Naming().property("camel case"), is("camelCase"));
        assertThat(new Naming().property("camel       case"), is("camelCase"));
    }
}