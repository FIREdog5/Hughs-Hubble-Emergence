package bin.graphics;

import bin.ClientMain;
import bin.resource.ImageResources;
import bin.engine.GameLoop;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public abstract class EventListener implements GLEventListener {
  private int rot = 0;

  @Override
  public abstract void display(GLAutoDrawable drawable); //{
    // GL2 gl = ClientMain.gl;
    // gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    // Graphics.drawColor(new Color("#ff0000"));
    // Graphics.drawRect(-2, 0, 1, 2, 30);

    // Graphics.drawColor(new Color("#00ff00"));
    // Graphics.drawCircle(1, 2, 0);

    // Graphics.drawColor(new Color("#ffff00"));
    // Graphics.drawImage(ImageResources.starTest1, 0, 0, 10, 10, rot/10f);
    // rot++;
    // rot %= 3600;

    // Graphics.drawColor(new Color("#ffffff"));
    // Graphics.drawCircle(20f, 0, 0);
    // Graphics.drawGlobe(ImageResources.earthTest, 20, 0, 0);

    // Graphics.drawColor(new Color("#ffff00"));
    // Graphics.drawRect(0, 0, 80, 30);
    // Graphics.drawColor(new Color("#00ff00"));
    // Graphics.drawImage(ImageResources.maskTest, 0, 0, 80, 30);

    // Graphics.drawColor(new Color("#00ff00"));
    // Graphics.drawRect(0,0,10,10);
    // Graphics.drawColor(new Color("#ffff00"));
    // Graphics.drawImage(ImageResources.transparencyTest, 0, 0, 10, 10);

    // Graphics.drawGradientHalo(22, 20, 0, 0, new Color("#03fcf8"), new Color("#0000ff", .3f));
    // Graphics.drawColor(new Color("#0000ff"));
    // Graphics.drawCircle(20f, 0, 0);
    // Graphics.drawColor(new Color("#00ff00"));
    // Graphics.drawGlobe(ImageResources.maskTest, 20, 0, 0);
    // Graphics.drawColor(new Color("#BC5E20"));
    // Graphics.drawGlobe(ImageResources.maskTest2, 20, 0, 0);
    // Graphics.drawColor(new Color("#ffffff"));
    // Graphics.drawGlobe(ImageResources.maskTest3, 20, 0, 0);
    // Graphics.drawColor(new Color("#ffffff"));
    // Graphics.drawGlobe(ImageResources.capTest, 20, 0, 0);

  //   Graphics.nextFrame();
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
