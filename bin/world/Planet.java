package bin.world;

import bin.graphics.Graphics;
import bin.graphics.Color;
import bin.resource.ImageResources;
import bin.resource.palette.Palette;


public class Planet implements IDrawable {
  private float x;
  private float y;
  private float size;
  private Palette palette;
  public Planet (float x, float y, Palette palette) {
    this.x = x;
    this.y = y;
    this.size = 10;
    this.palette = palette;
  }
  public boolean shouldRender(Camera camera) {
    // System.out.println(camera.getCameraTopBound() + " " + camera.getCameraBottomBound() + " " + camera.getCameraLeftBound() + " " + camera.getCameraRightBound());
    // System.out.println(this.x + " " + this.y);
    return camera.getCameraRightBound() > this.x - this.size && camera.getCameraLeftBound() < this.x + this.size && camera.getCameraTopBound() > this.y - this.size && camera.getCameraBottomBound() < this.y + this.size;
  }
  public void render(Camera camera) {
    Graphics.drawGradientHalo(camera.scaleToZoom(this.size + 2), camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y), this.palette.lowerAtmosphereColor, this.palette.upperAtmosphereColor);
    Graphics.drawColor(this.palette.oceanColor);
    Graphics.drawCircle(camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Graphics.drawColor(this.palette.landColor);
    Graphics.drawGlobe(ImageResources.maskTest, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Graphics.drawColor(this.palette.mountainColor);
    Graphics.drawGlobe(ImageResources.maskTest2, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Graphics.drawColor(this.palette.snowColor);
    Graphics.drawGlobe(ImageResources.maskTest3, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Graphics.drawColor(this.palette.poleColor);
    Graphics.drawGlobe(ImageResources.capTest, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Graphics.drawColor(this.palette.cloudColor);
    Graphics.drawGlobe(ImageResources.cloudTest2, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    // System.out.println(camera.scaleToZoom(10));
    // System.out.println(camera.convertXToCamera(this.x));
    // System.out.println(camera.convertYToCamera(this.y));
  }
}
