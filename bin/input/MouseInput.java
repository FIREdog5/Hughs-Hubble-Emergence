package bin.input;

import bin.engine.GameLoop;
import bin.editor.Editor;

import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.MouseEvent;

public class MouseInput implements MouseListener {
  @Override
  public void mouseClicked(MouseEvent event) {
  }
  @Override
  public void mouseDragged(MouseEvent event) {
  }
  @Override
  public void mouseEntered(MouseEvent event) {

  }
  @Override
  public void mouseExited(MouseEvent event) {
    GameLoop.mouseReleased();
  }
  @Override
  public void mouseMoved(MouseEvent event) {
  }
  @Override
  public void mousePressed(MouseEvent event) {
    GameLoop.mousePressed();
    // startLoc = MouseInfo.getPointerInfo().getLocation();
  }
  @Override
  public void mouseReleased(MouseEvent event) {
    GameLoop.mouseReleased();
  }
  @Override
  public void mouseWheelMoved(MouseEvent event) {
    GameLoop.zoomWorld(event.getRotation()[1] + event.getRotation()[0]);
    Editor.zoomWorld(event.getRotation()[1] + event.getRotation()[0]);
  }
}
