package bin.world;

import bin.graphics.Color;
import bin.resource.ImageResources;
import bin.resource.palette.Palette;
import bin.graphics.objects.GradientHalo;
import bin.graphics.objects.Global;
import bin.graphics.objects.Circle;
import bin.graphics.objects.Globe;
import bin.graphics.Shaders;
import bin.ClientMain;

import com.jogamp.opengl.GL2;


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
    return camera.getCameraRightBound() > this.x - this.size && camera.getCameraLeftBound() < this.x + this.size && camera.getCameraTopBound() > this.y - this.size && camera.getCameraBottomBound() < this.y + this.size;
  }
  public void render(Camera camera) {
    GL2 gl = ClientMain.gl;
    GradientHalo.draw(camera.scaleToZoom(this.size + 2), camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y), this.palette.getLowerAtmosphereColor(), this.palette.getUpperAtmosphereColor());

    Shaders.terrainShader.startShader(gl);
    gl.glActiveTexture(GL2.GL_TEXTURE0+1);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, this.palette.getPaletteSampler().getTexture().getTextureObject());
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glActiveTexture(GL2.GL_TEXTURE0+2);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.generationTest2.getTexture().getTextureObject());
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glActiveTexture(GL2.GL_TEXTURE0);
    Global.drawColor(new Color("#ffffff"));
    Globe.draw(ImageResources.generationTest, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
    Shaders.terrainShader.stopShader(gl);

    Globe.draw(ImageResources.capTest, camera.scaleToZoom(this.size), camera.convertXToCamera(this.x), camera.convertYToCamera(this.y));
  }
}
