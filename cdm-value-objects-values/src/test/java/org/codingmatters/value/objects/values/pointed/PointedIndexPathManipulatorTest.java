package org.codingmatters.value.objects.values.pointed;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class PointedIndexPathManipulatorTest {
    @Test
    public void empty() {
        PointedIndexPathManipulator pathManipulator = new PointedIndexPathManipulator(null);
        assertThat(pathManipulator.hasIndex(), is(false));

        pathManipulator = new PointedIndexPathManipulator("");
        assertThat(pathManipulator.hasIndex(), is(false));

        pathManipulator = new PointedIndexPathManipulator("plok");
        assertThat(pathManipulator.hasIndex(), is(false));
    }

    @Test
    public void withPath() {
        PointedIndexPathManipulator pathManipulator = new PointedIndexPathManipulator("plok[12]");
        assertThat(pathManipulator.hasIndex(), is(true));
        assertThat(pathManipulator.getProperty(), is("plok"));
        assertThat(pathManipulator.getIndex(), is(12));
    }

}
