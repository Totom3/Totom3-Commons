package io.github.totom3.commons.misc;

/**
 *
 * @author Totom3
 */
public interface BooleanOperation {

    public static BooleanOperation AND = (a, b) -> a && b;

    public static BooleanOperation OR = (a, b) -> a || b;

    public static BooleanOperation XOR = (a, b) -> a ^ b;

    public static BooleanOperation NAND = (a, b) -> !(a && b);

    boolean eval(boolean a, boolean b);
}
