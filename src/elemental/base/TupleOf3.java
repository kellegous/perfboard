package elemental.base;

public class TupleOf3<A, B, C> {
  private final A a;
  private final B b;
  private final C c;

  public static <A, B, C> TupleOf3<A, B, C> create(A a, B b, C c) {
    return new TupleOf3<A, B, C>(a, b, c);
  }

  public TupleOf3(A a, B b, C c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public A a() {
    return a;
  }

  public B b() {
    return b;
  }

  public C c() {
    return c;
  }
}
