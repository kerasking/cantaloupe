package edu.illinois.library.cantaloupe.resource.iiif.v2;

import edu.illinois.library.cantaloupe.image.Dimension;
import edu.illinois.library.cantaloupe.image.Identifier;
import edu.illinois.library.cantaloupe.operation.Crop;
import edu.illinois.library.cantaloupe.operation.Operation;
import edu.illinois.library.cantaloupe.operation.OperationList;
import edu.illinois.library.cantaloupe.operation.Rotate;
import edu.illinois.library.cantaloupe.operation.Scale;
import edu.illinois.library.cantaloupe.processor.UnsupportedOutputFormatException;
import edu.illinois.library.cantaloupe.resource.IllegalClientArgumentException;
import edu.illinois.library.cantaloupe.test.BaseTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class ParametersTest extends BaseTest {

    private static final float DELTA = 0.00000001f;

    private Parameters instance;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        instance = new Parameters(
                new Identifier("identifier"),
                "0,0,200,200",
                "pct:50",
                "5",
                "default",
                "jpg");
    }

    @Test
    public void testFromUri() {
        instance = Parameters.fromUri("bla/20,20,50,50/pct:90/15/bitonal.jpg");
        assertEquals("bla", instance.getIdentifier().toString());
        assertEquals("20,20,50,50", instance.getRegion().toString());
        assertEquals(90f, instance.getSize().getPercent(), DELTA);
        assertEquals(15f, instance.getRotation().getDegrees(), DELTA);
        assertEquals(Quality.BITONAL, instance.getQuality());
        assertEquals(OutputFormat.JPG, instance.getOutputFormat());
    }

    @Test(expected = IllegalClientArgumentException.class)
    public void testFromUriWithInvalidURI1() {
        Parameters.fromUri("bla/20,20,50,50/15/bitonal.jpg");
    }

    @Test(expected = IllegalClientArgumentException.class)
    public void testFromUriWithInvalidURI2() {
        Parameters.fromUri("bla/20,20,50,50/pct:90/15/bitonal");
    }

    @Test
    public void testCopyConstructor() {
        Parameters copy = new Parameters(instance);
        assertEquals(copy.getIdentifier(), instance.getIdentifier());
        assertEquals(copy.getRegion(), instance.getRegion());
        assertEquals(copy.getSize(), instance.getSize());
        assertEquals(copy.getRotation(), instance.getRotation());
        assertEquals(copy.getQuality(), instance.getQuality());
        assertEquals(copy.getOutputFormat(), instance.getOutputFormat());
        assertEquals(copy.getQuery(), instance.getQuery());
    }

    @Test(expected = UnsupportedOutputFormatException.class)
    public void testConstructor3ThrowsUnsupportedOutputFormatException() {
        new Parameters(new Identifier("identifier"), "0,0,200,200", "pct:50",
                "5", "default", "bogus");
    }

    @Test
    public void testToOperationList() {
        final OperationList opList = instance.toOperationList();
        Iterator<Operation> it = opList.iterator();
        assertTrue(it.next() instanceof Crop);
        assertTrue(it.next() instanceof Scale);
        assertTrue(it.next() instanceof Rotate);
    }

    /**
     * N.B.: the individual path components are tested more thoroughly in the
     * specific component classes (e.g. {@link Size} etc.).
     */
    @Test
    public void testToCanonicalString() {
        final Dimension fullSize = new Dimension(1000, 800);
        assertEquals("identifier/0,0,200,200/500,/5/default.jpg",
                instance.toCanonicalString(fullSize));
    }

}
