package bin.input;

import bin.engine.GameLoop;
import bin.editor.Editor;
import bin.test.GraphicsTest;

import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.MouseEvent;
import java.awt.Point;

public class MouseInput implements MouseListener {
  @Override
  public void mouseClicked(MouseEvent event) {
  }
  @Override
  public void mouseDragged(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GraphicsTest.mouseDragged(location);
    Editor.mouseDragged(location);
  }
  @Override
  public void mouseEntered(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GraphicsTest.mouseMoved(location);
    Editor.mouseMoved(location);
  }
  @Override
  public void mouseExited(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GameLoop.mouseReleased(location);
    GraphicsTest.mouseReleased(location);
    Editor.mouseReleased(location);
  }
  @Override
  public void mouseMoved(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GraphicsTest.mouseMoved(location);
    Editor.mouseMoved(location);
  }
  @Override
  public void mousePressed(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GameLoop.mousePressed(location);
    GraphicsTest.mousePressed(location);
    Editor.mousePressed(location);
    //Point startLoc = MouseInfo.getPointerInfo().getLocation();
  }
  @Override
  public void mouseReleased(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GameLoop.mouseReleased(location);
    GraphicsTest.mouseReleased(location);
    Editor.mouseReleased(location);
  }
  @Override
  public void mouseWheelMoved(MouseEvent event) {
    Point location = new Point(event.getX(), event.getY());
    GameLoop.zoomWorld(location, event.getRotation()[1] + event.getRotation()[0]);
    Editor.mouseScrolled(location, event.getRotation()[1] + event.getRotation()[0]);
    GraphicsTest.mouseScrolled(location, event.getRotation()[1] + event.getRotation()[0]);
  }
}
