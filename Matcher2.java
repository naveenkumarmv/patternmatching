import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;


public class Matcher2<L, R> {

    public static class Partial<L, R, E> {
        public Partial(Matcher2<L, R> matcher, Optional<E> result) {
            this.matcher = matcher;
            this.result = result;
        }

        private final Matcher2<L, R> matcher;
        private final Optional<E> result;

        public E orElse(E e) {
            return result.orElse(e);
        }

        public E orElse(BiFunction<L, R, E> function) {
            return result.orElseGet(() -> function.apply(matcher.l, matcher.r));
        }

        public Partial<L, R, E> when(BiFunction<L, R, Boolean> guard, E e) {
            return partial(result(guard, e));
        }

        public Partial<L, R, E> when(BiFunction<L, R, Boolean> guard, BiFunction<L, R, E> function) {
            return partial(result(guard, function));
        }

        public Partial<L, R, E> when(
                Predicate<L> lPredicate, Predicate<R> rPredicate, BiFunction<L, R, E> function) {
            return partial(result(lPredicate, rPredicate, function));
        }

        private Partial<L, R, E> partial(Optional<E> result) {
            return new Partial<>(matcher, this.result.isPresent() ? this.result : result);
        }

        private <T> Optional<T> result(BiFunction<L, R, Boolean> guard, Supplier<T> supplier) {
            return guard.apply(matcher.l, matcher.r)
                    ? Optional.ofNullable(supplier.get())
                    : Optional.empty();
        }

        private <T> Optional<T> result(BiFunction<L, R, Boolean> guard, T t) {
            return result(guard, () -> t);
        }

        private <T> Optional<T> result(BiFunction<L, R, Boolean> guard, BiFunction<L, R, T> function) {
            return result(guard, () -> function.apply(matcher.l, matcher.r));
        }

        private <T> Optional<T> result(
                Predicate<L> lPredicate, Predicate<R> rPredicate, BiFunction<L, R, T> function) {
            return result((l, r) -> lPredicate.test(l) && rPredicate.test(r), function);
        }
    }

    private final L l;
    private final R r;

    public Matcher2(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public <E> Partial<L, R, E> when(BiFunction<L, R, Boolean> guard, Supplier<E> supplier) {
        Optional<E> optional =
                guard.apply(l, r) ? Optional.ofNullable(supplier.get()) : Optional.empty();
        return new Partial<>(this, optional);
    }

    public <E> Partial<L, R, E> when(BiFunction<L, R, Boolean> guard, E e) {
        return when(guard, () -> e);
    }

    public <E> Partial<L, R, E> when(BiFunction<L, R, Boolean> guard, BiFunction<L, R, E> function) {
        return when(guard, () -> function.apply(l, r));
    }

    public <E> Partial<L, R, E> when(
            Predicate<L> lPredicate, Predicate<R> rPredicate, BiFunction<L, R, E> function) {
        return when((l, r) -> lPredicate.test(l) && rPredicate.test(r), function);
    }
}