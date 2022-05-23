package bin.input;

import bin.engine.GameLoop;
import bin.editor.Editor;
import bin.test.GraphicsTest;

import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.MouseEvent;

public class MouseInput implements MouseListener {
  @Override
  public void mouseClicked(MouseEvent event) {
  }
  @Override
  public void mouseDragged(MouseEvent event) {
    GraphicsTest.mouseDragged();
    Editor.mouseDragged();
  }
  @Override
  public void mouseEntered(MouseEvent event) {
    GraphicsTest.mouseMoved();
    Editor.mouseMoved();
  }
  @Override
  public void mouseExited(MouseEvent event) {
    GameLoop.mouseReleased();
    GraphicsTest.mouseReleased();
    Editor.mouseReleased();
  }
  @Override
  public void mouseMoved(MouseEvent event) {
    GraphicsTest.mouseMoved();
    Editor.mouseMoved();
  }
  @Override
  public void mousePressed(MouseEvent event) {
    GameLoop.mousePressed();
    GraphicsTest.mousePressed();
    Editor.mousePressed();
    //Point startLoc = MouseInfo.getPointerInfo().getLocation();
  }
  @Override
  public void mouseReleased(MouseEvent event) {
    GameLoop.mouseReleased();
    GraphicsTest.mouseReleased();
    Editor.mouseReleased();
  }
  @Override
  public void mouseWheelMoved(MouseEvent event) {
    GameLoop.zoomWorld(event.getRotation()[1] + event.getRotation()[0]);
    Editor.mouseScrolled(event.getRotation()[1] + event.getRotation()[0]);
    GraphicsTest.mouseScrolled(event.getRotation()[1] + event.getRotation()[0]);
  }
}
