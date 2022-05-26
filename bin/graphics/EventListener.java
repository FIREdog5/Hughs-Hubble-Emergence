package bin.graphics;

import bin.ClientMain;
import bin.resource.ImageResources;
import bin.engine.GameLoop;
import bin.graphics.objects.Global;
import bin.graphics.objects.Rect;
import bin.graphics.objects.Circle;
import bin.graphics.objects.Image;
import bin.graphics.objects.Globe;
import bin.graphics.objects.GradientHalo;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextRenderer.DefaultRenderDelegate;
import java.awt.Font;

public abstract class EventListener implements GLEventListener {
  private int rot = 0;

  @Override
  public abstract void display(GLAutoDrawable drawable); //{
    // GL2 gl = ClientMain.gl;
    // gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    // Global.drawColor(new Color("#ff0000"));
    // Rect.draw(-2, 0, 1, 2, 30);

    // Global.drawColor(new Color("#00ff00"));
    // Circle.draw(1, 2, 0);

    // Global.drawColor(new Color("#ffff00"));
    // Image.draw(ImageResources.starTest1, 0, 0, 10, 10, rot/10f);
    // rot++;
    // rot %= 3600;

    // Global.drawColor(new Color("#ffffff"));
    // Circle.draw(20f, 0, 0);
    // Globe.draw(ImageResources.earthTest, 20, 0, 0);

    // Global.drawColor(new Color("#ffff00"));
    // Rect.draw(0, 0, 80, 30);
    // Global.drawColor(new Color("#00ff00"));
    // Image.draw(ImageResources.maskTest, 0, 0, 80, 30);

    // Global.drawColor(new Color("#00ff00"));
    // Rect.draw(0,0,10,10);
    // Global.drawColor(new Color("#ffff00"));
    // Image.draw(ImageResources.transparencyTest, 0, 0, 10, 10);

    // GradientHalo.draw(22, 20, 0, 0, new Color("#03fcf8"), new Color("#0000ff", .3f));
    // Global.drawColor(new Color("#0000ff"));
    // Circle.draw(20f, 0, 0);
    // Global.drawColor(new Color("#00ff00"));
    // Globe.draw(ImageResources.maskTest, 20, 0, 0);
    // Global.drawColor(new Color("#BC5E20"));
    // Globe.draw(ImageResources.maskTest2, 20, 0, 0);
    // Global.drawColor(new Color("#ffffff"));
    // Globe.draw(ImageResources.maskTest3, 20, 0, 0);
    // Global.drawColor(new Color("#ffffff"));
    // Globe.draw(ImageResources.capTest, 20, 0, 0);

  //   Global.nextFrame();
  //
  //   GameLoop.renderWorld();
  //
  // }

  @Override
  public void dispose(GLAutoDrawable drawable) {

  }

  @Override
  public void init(GLAutoDrawable drawable) {
    ClientMain.gl = drawable.getGL().getGL2();
    GL2 gl = ClientMain.gl;
    gl.glEnable(GL2.GL_BLEND);
    gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    gl.glClearColor(0,0,0,1);
    gl.glEnable(GL2.GL_TEXTURE_2D);
    // TODO: graphics settings for text here
    ClientMain.textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
    Shaders.init();
    ImageResources.init();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL2 gl = ClientMain.gl;
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    gl.glOrtho(-1 * Renderer.unitsWide / 2, Renderer.unitsWide / 2, -unitsTall / 2, unitsTall / 2, -1, 1);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
  }

}
