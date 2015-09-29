package io.github.totom3.commons.misc;

import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An implementation of {@code Stream} that wraps an original one and delegates
 * it's method to it. It behaves like the {@code Stream} would, but the stream
 * is updated after each intermediate operation, excluding
 * {@link #map(java.util.function.Function)} and their derivate. This allows
 * instances to be re-used directly. For instance, the following code would not
 * work, using a <em>java.util.stream</em> {@code Stream}:
 * <pre>
 * {@literal
 * List<String> l = [...];
 * Stream<String> s = l.stream();
 *
 * s.filter((str) -> str.length() > 3);
 * s.filter((str) -> str.contains("something"));
 * }
 * </pre> An {@code Exception} would be thrown when trying to apply for the
 * second time an intermediate operation on the same instance. The simplest way
 * of fixing this is the following:
 * <pre>
 * {@literal
 * List<String> l = [...];
 * Stream<String> s = l.stream();
 *
 * s.filter((str) -> str.length() > 3)
 *   .filter((str) -> str.contains("something"));
 * }
 * </pre> That said, it is not always possible to chain the calls like above.
 * Another solution could be to replace the {@code Stream} with the returned
 * instance. However, this could cause problems with future lambdas and
 * anonymous inner classes.
 *
 * This implementation fixes the problem. It wraps a {@code Stream} and updates
 * it each time an intermediate operation is performed, allowing the user to use
 * a {@code Stream} over a longer period, easier.
 *
 * @author Totom3
 * @param <T> the type of the {@code Steam}
 */
public class WrappedStream<T> implements Stream<T> {

    private Stream<T> stream;

    /**
     * Constructs a new {@code WrappedStream}.
     *
     * @param stream the {@code Stream} to wrap. Cannot be {@code null}.
     *
     * @throws NullPointerException if {@code stream} is {@code null}.
     */
    public WrappedStream(Stream<T> stream) {
	this.stream = Preconditions.checkNotNull(stream, "Stream cannot be null");
    }

    /**
     * Returns the current instance of the {@code Stream} backing this
     * {@code WrappedStream}.
     *
     * @return the internal {@code Stream}.
     */
    public Stream<T> get() {
	return stream;
    }

    @Override
    public Stream<T> filter(Predicate<? super T> predicate) {
	return stream = get().filter(predicate);
    }

    @Override
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
	return get().map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super T> mapper) {
	return get().mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super T> mapper) {
	return get().mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
	return get().mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
	return get().flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
	return get().flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
	return get().flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
	return get().flatMapToDouble(mapper);
    }

    @Override
    public Stream<T> distinct() {
	return stream = get().distinct();
    }

    @Override
    public Stream<T> sorted() {
	return stream = get().sorted();
    }

    @Override
    public Stream<T> sorted(Comparator<? super T> comparator) {
	return stream = get().sorted(comparator);
    }

    @Override
    public Stream<T> peek(Consumer<? super T> action) {
	return stream = get().peek(action);
    }

    @Override
    public Stream<T> limit(long maxSize) {
	return stream = get().limit(maxSize);
    }

    @Override
    public Stream<T> skip(long n) {
	return stream = get().skip(n);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
	get().forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
	get().forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
	return get().toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
	return get().toArray(generator);
    }

    @Override
    public T reduce(T identity, BinaryOperator<T> accumulator) {
	return get().reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
	return get().reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
	return get().reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
	return get().collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, A, R> collector) {
	return get().collect(collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) {
	return get().min(comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) {
	return get().max(comparator);
    }

    @Override
    public long count() {
	return get().count();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
	return get().anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
	return get().allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
	return get().noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
	return get().findFirst();
    }

    @Override
    public Optional<T> findAny() {
	return get().findAny();
    }

    @Override
    public Iterator<T> iterator() {
	return get().iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
	return get().spliterator();
    }

    @Override
    public boolean isParallel() {
	return get().isParallel();
    }

    @Override
    public Stream<T> sequential() {
	return stream = get().sequential();
    }

    @Override
    public Stream<T> parallel() {
	return stream = get().parallel();
    }

    @Override
    public Stream<T> unordered() {
	return stream = get().unordered();
    }

    @Override
    public Stream<T> onClose(Runnable closeHandler) {
	return stream = get().onClose(closeHandler);
    }

    @Override
    public void close() {
	get().close();
    }

}
