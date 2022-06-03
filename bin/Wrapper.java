package bin;

public class Wrapper<T> {
  private T obj;

  public Wrapper(T obj) {
    this.obj = obj;
  }
  public Wrapper() {
    this.obj = null;
  }

  public T get() {
    return obj;
  }

  public void set(T obj) {
    this.obj = obj;
  }
}
