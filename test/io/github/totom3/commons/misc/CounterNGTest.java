package io.github.totom3.commons.misc;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author Totom3
 */
public class CounterNGTest {
    
    public CounterNGTest() {
    }

    /**
     * Test of next method, of class Counter.
     */
    @Test
    public void testGetAsInt() {
	System.out.println("getAsInt");
	
	Counter instance = new Counter();
	
	assertEquals(instance.next(), 0);
	assertEquals(instance.next(), 1);
	assertEquals(instance.next(), 2);
    }
    
}
