package bin.graphics.ui.colorUtils;

import bin.graphics.ui.UIElement;
import bin.graphics.ui.UIBox;

import bin.graphics.objects.Globe;
import bin.graphics.objects.Global;
import bin.graphics.Color;
import bin.resource.ImageResource;
import bin.resource.ImageResources;
import bin.Wrapper;
import bin.resource.ImageResources;
import bin.resource.palette.Palette;
import bin.graphics.Shaders;
import bin.ClientMain;

import com.jogamp.opengl.GL2;
import java.lang.UnsupportedOperationException;

public class UIGlobeDisplay extends UIBox {

  public ImageResource image;
  public Wrapper<ImageResource> imageWrapper;

  public Palette palette;

  public UIGlobeDisplay(UIElement parent) {
    super(parent);
    this.image = ImageResources.error;
  }

  public UIGlobeDisplay(UIElement parent, ImageResource image, Palette palette) {
    super(parent);
    this.image = image;
    this.palette = palette;
  }

  public UIGlobeDisplay(UIElement parent, Wrapper<ImageResource> imageWrapper, Palette palette) {
    super(parent);
    this.imageWrapper = imageWrapper;
    this.palette = palette;
  }

  public void setRadius(float r) {
    this.minWidth = r;
    this.minHeight = r;
  }

  public float getRadius() {
    return this.minWidth / 2f;
  }

  @Override
  public float getWidth() {
    float minimum = Math.max(this.minWidth, this.margin * 2);
    if (this.maxWidth != -1) {
      minimum = Math.min(minimum, maxWidth);
    }
    return minimum;
  }

  @Override
  public float getHeight() {
    float minimum = Math.max(this.minHeight, this.margin * 2);
    if (this.maxHeight != -1) {
      minimum = Math.min(minimum, maxHeight);
    }
    return minimum;
  }

  @Override
  public float getChildX(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public float getChildY(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addChild(UIElement child) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void render() {
    GL2 gl = ClientMain.gl;
    if (this.image != null) {
      if (this.palette != null) {
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
      }
      Global.drawColor(new Color("#ffffff"));
      Globe.draw(this.image, this.getRadius(), this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2);
      if (this.palette != null) {
        Shaders.terrainShader.stopShader(gl);
      }
    }
    if (imageWrapper != null) {
      if (this.palette != null) {
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
      }
      Global.drawColor(new Color("#ffffff"));
      Globe.draw(this.imageWrapper.get(), this.getRadius(), this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2);
      if (this.palette != null) {
        Shaders.terrainShader.stopShader(gl);
      }
    }
  }
}
