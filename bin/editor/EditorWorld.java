package bin.editor;

import java.util.ArrayList;

import bin.world.Camera;
import bin.world.IDrawable;

public class EditorWorld {
  private ArrayList<IDrawable> worldObjects;
  public Camera camera;
  public EditorWorld() {
    this.worldObjects = new ArrayList<IDrawable>();
    this.camera = new Camera(0, 0, 50, 40, 80);
  }
  public void renderWorld() {
    for(IDrawable object : worldObjects) {
      if (object.shouldRender(camera)) {
        object.render(camera);
      }
    }
  }
  public void addWorldObject (IDrawable object) {
    worldObjects.add(object);
  }
}
