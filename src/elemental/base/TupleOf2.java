package elemental.base;

public class TupleOf2<A, B> {
  private final A a;
  private final B b;

  public static <A, B> TupleOf2<A, B> create(A a, B b) {
    return new TupleOf2<A, B>(a, b);
  }

  public TupleOf2(A a, B b) {
    this.a = a;
    this.b = b;
  }

  public A a() {
    return a;
  }

  public B b() {
    return b;
  }
}
