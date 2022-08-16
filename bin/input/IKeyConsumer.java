package bin.input;

public interface IKeyConsumer {

  public void setKeyboardHandler(KeyboardHandler keyboardHandler);
  public KeyboardHandler getKeyboardHandler();
  public void cleanFromKeyboardHandler();

  public void keyDown(Key key);
  public void keyUp(Key key);

}
