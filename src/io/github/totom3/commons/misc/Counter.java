package io.github.totom3.commons.misc;

/**
 *
 * @author Totom3
 */
public class Counter {
    
    private int val;

    public Counter() {
    }

    public Counter(int val) {
	this.val = val;
    }
    
    public int next() {
	return val++;
    }
    
    public int current() {
	return val;
    }
    
    public void jumpTo(int n) {
	val = n;
    }
}
