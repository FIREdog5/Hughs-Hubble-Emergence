package bin.world;

import bin.graphics.Graphics;
import bin.graphics.Color;

public class Star implements IDrawable {
  private float x;
  private float y;
  public Star (float x, float y) {
    this.x = x;
    this.y = y;
  }
  public boolean shouldRender(Camera camera) {
    return true;
  }
  public void render(Camera camera) {
    Graphics.drawColor(new Color("#ffffff"));
    Graphics.drawCircle(camera.scaleToZoom(10), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    // System.out.println(camera.scaleToZoom(10));
    // System.out.println(camera.convertXToCamera(this.x));
    // System.out.println(camera.convertYToCamera(this.y));
  }
}
