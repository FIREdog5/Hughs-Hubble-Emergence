package bin.engine;

import bin.graphics.Renderer;
import bin.world.World;

import java.awt.MouseInfo;
import java.awt.Point;

public class GameLoop extends Thread{
  private boolean running;
  private static World world;
  private static boolean mouseIsPressed = false;
  private static Point startLoc;

  public static void init() {
    (new GameLoop()).start();
  }

  public GameLoop () {
    this.setName("Game Loop");
    this.running = true;
  }

  public void run() {
    this.running = true;
    world = new World();
    Generation.generateStars(world, 300);

    while (this.running) {
      if (mouseIsPressed) {
        Point currentLoc = MouseInfo.getPointerInfo().getLocation();
        float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
        world.camera.adjustLocation(world.camera.zoomToScale((float)(startLoc.x - currentLoc.x) / Renderer.getWindowWidth() * Renderer.unitsWide), world.camera.zoomToScale((float)(currentLoc.y - startLoc.y) / Renderer.getWindowHeight() * unitsTall));
        startLoc = currentLoc;
      }
      Renderer.render();
    }
  }

  public static void renderWorld() {
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

  public static void mousePressed() {
    startLoc = MouseInfo.getPointerInfo().getLocation();
    mouseIsPressed = true;
  }

  public static void mouseReleased() {
    mouseIsPressed = false;
  }

}
