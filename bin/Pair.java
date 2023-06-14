package bin;

public class Pair<T, V> {
  private T key;
  private V value;

  public Pair(T key, V value) {
    this.key = key;
    this.value = value;
  }
  public Pair() {
    this.key = null;
    this.value = null;
  }

  public T getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public void set(T key, V value) {
    this.key = key;
    this.value = value;
  }
}
