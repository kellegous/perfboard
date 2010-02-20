package kellegous.client.data;

public interface Callback<T> {
  void didCallback(T value);

  void didFail();
}
