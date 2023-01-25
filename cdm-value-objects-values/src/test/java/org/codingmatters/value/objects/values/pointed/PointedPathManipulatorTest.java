package org.codingmatters.value.objects.values.pointed;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PointedPathManipulatorTest {

    @Test
    public void emptyPath() {
        List<String> path = new PointedPathManipulator(null).getPath();
        assertThat(path.size(), is(0));

        path = new PointedPathManipulator("").getPath();
        assertThat(path.size(), is(0));
    }

    @Test
    public void single() {
        List<String> path = new PointedPathManipulator("plok").getPath();
        assertThat(path.size(), is(1));
        assertThat(path.get(0), is("plok"));
    }

    @Test
    public void multiple() {
        List<String> path = new PointedPathManipulator("plok.truc[0].toto").getPath();
        assertThat(path.size(), is(3));
        assertThat(path.get(0), is("plok"));
        assertThat(path.get(1), is("truc[0]"));
        assertThat(path.get(2), is("toto"));
    }

    @Test
    public void multipleSeparated() {
        List<String> path = new PointedPathManipulator("plok.truc[0].toto").getPathIndexSeparated();
        assertThat(path.size(), is(4));
        assertThat(path.get(0), is("plok"));
        assertThat(path.get(1), is("truc"));
        assertThat(path.get(2), is("[0]"));
        assertThat(path.get(3), is("toto"));
    }
}
