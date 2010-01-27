package elemental.base;

public class TupleOf4<A, B, C, D> {
  private final A a;
  private final B b;
  private final C c;
  private final D d;

  public static <A, B, C, D> TupleOf4<A, B, C, D> create(A a, B b, C c, D d) {
    return new TupleOf4<A, B, C, D>(a, b, c, d);
  }

  public TupleOf4(A a, B b, C c, D d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
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

  public D d() {
    return d;
  }
}
