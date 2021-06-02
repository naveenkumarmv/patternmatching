import java.util.function.Predicate;

public class Predicates {

    public static <E> Predicate<E> eq(E e){
        return arg -> arg.equals(e);
    }

    public static <E extends Comparable<E>> Predicate <E> lt(E e){
        return arg -> arg.compareTo(e) < 0;
    }

    public static <E extends Comparable<E>> Predicate <E> lte(E e){
        return arg -> arg.compareTo(e) <= 0;
    }

    public static <E extends Comparable<E>> Predicate <E> gt(E e){
        return arg -> arg.compareTo(e) > 0;
    }


}
