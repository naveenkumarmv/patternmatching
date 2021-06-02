public class Matcher {

    static <L, R, E> Matcher2 of (L l, R r){
         return new Matcher2(l, r);
    }

    static <E> Matcher1 of(E e ){ return new Matcher1(e);}

}
