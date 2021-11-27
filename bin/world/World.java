package bin.world;

import java.util.ArrayList;

public class World {
  private ArrayList<IDrawable> worldObjects;
  public Camera camera;
  public World() {
    this.worldObjects = new ArrayList<IDrawable>();
    this.camera = new Camera(0, 0, 1);
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
