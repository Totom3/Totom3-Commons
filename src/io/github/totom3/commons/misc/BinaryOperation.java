package io.github.totom3.commons.misc;

/**
 *
 * @author Totom3
 */
@FunctionalInterface
public interface BinaryOperation {

    BinaryOperation ADD = (a, b) -> a + b;

    BinaryOperation SUBSTRACT = (a, b) -> a - b;

    BinaryOperation MULTIPLY = (a, b) -> a * b;

    BinaryOperation DIVIDE = (a, b) -> a / b;

    BinaryOperation MODULUS = (a, b) -> a % b;

    BinaryOperation POWER = (a, b) -> Math.pow(a, b);

    BinaryOperation ROOT = (a, b) -> Math.pow(a, 1 / b);

    double calculate(double a, double b);
}
