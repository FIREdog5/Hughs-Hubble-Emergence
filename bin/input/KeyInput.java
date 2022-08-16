package bin.input;

import bin.engine.GameLoop;
import bin.editor.Editor;
import bin.test.GraphicsTest;

import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.KeyEvent;

public class KeyInput implements KeyListener {
  @Override
  public void keyPressed(KeyEvent event) {
    // if (event.isPrintableKey()){
    //   System.out.println(event.getKeyChar() + "  |  " + event.getKeySymbol());
    // } else {
    //   System.out.println(event.getKeySymbol());
    // }
    Key key = new Key(event);
    GraphicsTest.keyPressed(key);
    Editor.keyPressed(key);
  }

  @Override
  public void keyReleased(KeyEvent event) {
    Key key = new Key(event);
    GraphicsTest.keyReleased(key);
    Editor.keyReleased(key);
  }
}
