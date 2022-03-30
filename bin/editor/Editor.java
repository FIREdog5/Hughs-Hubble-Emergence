package bin.editor;

import bin.world.Planet;
import bin.graphics.Renderer;
import bin.resource.Palettes;

public class Editor extends Thread{

  private boolean running;
  private static EditorWorld world;

  public static void init() {
    (new Editor()).start();
  }

  public Editor () {
    this.setName("Editor");
    this.running = true;
  }

  public void run() {
    this.running = true;
    world = new EditorWorld();
    world.addWorldObject(new Planet(0, 0, Palettes.earth1));
    world.addWorldObject(new Planet(20, 20, Palettes.earth1));
    world.addWorldObject(new Planet(-20, 20, Palettes.earth1));
    world.addWorldObject(new Planet(-20, -20, Palettes.earth1));
    world.addWorldObject(new Planet(20, -20, Palettes.earth1));

    System.out.println(EditorUtils.openResource());

    while (this.running) {
      Renderer.render();
    }
  }

  public static void render() {
    if (world == null) {
      return;
    }
    world.renderWorld();
  }

  public static void zoomWorld(float deltaZoom) {
    if (world == null) {
      return;
    }
    world.camera.adjustZoom(deltaZoom);
  }

}
