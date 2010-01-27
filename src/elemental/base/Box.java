package elemental.base;

public class Box<T> {
  public static class Int {
    private int value;

    public Int(int value) {
      this.value = value;
    }

    public int get() {
      return value;
    }

    public void set(int value) {
      this.value = value;
    }
  }

  private T value;

  public Box(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
  }
}
