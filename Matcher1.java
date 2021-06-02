import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Pattern matching is a commonly applied pattern in Functional Programming. algos.Matcher is an
 * abstraction that allows checking of multiple patterns against a target and conditionally setting
 * the corresponding matched value. If more than one predicates match, the value set by the first
 * match is returned.
 *
 * @param <E>
 */
public class Matcher1<E> {

    private final E _e;

    public Matcher1(E e) {
        _e = e;
    }

    /**
     * function to set the value to match/test
     *
     * @param u argument
     * @param <U> type of argument
     * @return algos.Matcher wrapping the argument
     */
    public static <U> Matcher1<U> of(U u) {
        return new Matcher1<>(u);
    }

    /**
     * function to 1)test the wrapped value, and conditionally set the return value if not set
     *
     * @param predicate predicate to test
     * @param supplier supplier of the return value
     * @param <T> type of the return value
     * @return algos.Matcher, which is contains one of 1)previously wrapped value 2)value from supplier if
     *     predicate evaluates to true or 3)unset matcher
     */
    public <T> Result<E, T> when(Predicate<E> predicate, Supplier<T> supplier) {
        Optional<T> optional =
                predicate.test(_e)
                        ? Optional.ofNullable(supplier.get())
                        : Optional.empty();
        return new Result<>(optional, this);
    }

    /**
     * function to 1)test the wrapped value, and conditionally set the return value if not set
     *
     * @param predicate predicate to test
     * @param function function that can be applied to the wrapped value
     * @param <T> type of the return value
     * @return algos.Matcher, which is contains one of 1)previously wrapped value 2)value from application
     *     of the function if predicate evaluates to true or 3)unset matcher
     */
    public <T> Result<E, T> when(Predicate<E> predicate, Function<E, T> function) {
       return when(predicate, () -> function.apply(_e));
    }

    /**
     * function to 1)test the wrapped value, and conditionally set the return value if not set
     *
     * @param predicate predicate to test
     * @param t value that can be returned
     * @param <T> type of the return value
     * @return algos.Matcher, which is contains one of 1)previously wrapped value 2)supplied value if
     *     predicate evaluates to true or 3)unset matcher
     */
    public <T> Result<E, T> when(Predicate<E> predicate, T t) {
        return when(predicate, () -> t);
    }

    public static class Result<U, T> {
        private final Optional<T> data;
        private final Matcher1<U> matcher1;

        private Result(Optional<T> data, Matcher1<U> matcher1) {
            this.data = data;
            this.matcher1 = matcher1;
        }

        public Result<U, T> when(Predicate<U> predicate, Supplier<T> supplier) {

            Optional<T> optional = data.isPresent()
                    ? data
                    : predicate.test(matcher1._e)
                    ? Optional.of(supplier.get())
                    : Optional.empty();
            return new Result<>(
                    optional,
                   matcher1
           );
        }

        public Result<U, T> when(Predicate<U> predicate, Function<U, T> function) {
           return when(predicate, () -> function.apply(matcher1._e));
        }

        public Result<U, T> when(Predicate<U> predicate, T t) {
            return when(predicate, () -> t);
        }

        public T orElse(Supplier<T> supplier) {
            return data.orElseGet(supplier);
        }

        public T orElse(T t){
            return data.orElse(t);
        }

        public T orElse(Function<U, T> function) {
            return orElse(() -> function.apply(matcher1._e));
        }
    }
}