package io.github.totom3.commons.misc;

/**
 *
 * @author Totom3
 */
public interface PredicateOperation {

    PredicateOperation LARGER = (a, b) -> a > b;
    
    PredicateOperation LARGER_OR_EQUAL = (a, b) -> a >= b;

    PredicateOperation SMALLER = (a, b) -> a < b;
    
    PredicateOperation SMALLER_OR_EQUAL = (a, b) -> a <= b;

    PredicateOperation EQUAL = (a, b) -> a == b;

    boolean eval(double a, double b);
}
