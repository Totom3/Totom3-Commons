package io.github.totom3.commons.misc;

/**
 *
 * @author Totom3
 */
@FunctionalInterface
public interface UnaryOperation {

    UnaryOperation SIN = Math::sin;
    UnaryOperation COS = Math::cos;
    UnaryOperation TAN = Math::tan;
    UnaryOperation ARCSIN = Math::asin;
    UnaryOperation ARCCOS = Math::acos;
    UnaryOperation ARCTAN = Math::atan;
    UnaryOperation SQUARE = n -> n * n;
    UnaryOperation CUBE = n -> n * n * n;
    UnaryOperation SQRT = Math::sqrt;
    UnaryOperation INCREMENT = n -> n + 1;
    UnaryOperation DECREMENT = n -> n - 1;

    double calculate(double n);
}
