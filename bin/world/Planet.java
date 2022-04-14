package bin.world;

import bin.graphics.Color;
import bin.resource.ImageResources;
import bin.resource.palette.Palette;
import bin.graphics.objects.GradientHalo;
import bin.graphics.objects.Global;
import bin.graphics.objects.Circle;
import bin.graphics.objects.Globe;


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
    GradientHalo.draw(camera.scaleToZoom(this.size + 2), camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y), this.palette.lowerAtmosphereColor, this.palette.upperAtmosphereColor);
    Global.drawColor(this.palette.oceanColor);
    Circle.draw(camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Global.drawColor(this.palette.landColor);
    Globe.draw(ImageResources.maskTest, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Global.drawColor(this.palette.mountainColor);
    Globe.draw(ImageResources.maskTest2, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Global.drawColor(this.palette.snowColor);
    Globe.draw(ImageResources.maskTest3, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Global.drawColor(this.palette.poleColor);
    Globe.draw(ImageResources.capTest, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Global.drawColor(this.palette.cloudColor);
    Globe.draw(ImageResources.cloudTest2, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    // System.out.println(camera.scaleToZoom(10));
    // System.out.println(camera.convertXToCamera(this.x));
    // System.out.println(camera.convertYToCamera(this.y));
  }
}
