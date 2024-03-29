package bin.graphics;

import bin.ClientMain;
import bin.editor.Editor;
import bin.resource.ImageResources;
import bin.graphics.objects.GradientHalo;
import bin.graphics.objects.Global;
import bin.graphics.objects.Circle;
import bin.graphics.objects.Globe;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.RoundedBox;

import com.jogamp.opengl.GL;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class EditorEventListener extends EventListener {

  private static boolean firstLoop = true;

  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = ClientMain.gl;
    gl.glClearColor(0f, 0f, 0f, 1.0f);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    if (firstLoop) {
      GL2 gl2 = drawable.getGL().getGL2();
      String vendor = gl2.glGetString(GL.GL_VENDOR);
      String renderer = gl2.glGetString(GL.GL_RENDERER);
      System.out.println("GPU Vendor: " + vendor);
      System.out.println("GPU Renderer: " + renderer);
      firstLoop = false;
    }


    //start better planets

    // GradientHalo.draw(22, 20, 0, 0, new Color("#03fcf8"), new Color("#0000ff", .3f));
    // Global.drawColor(new Color("#0000ff"));
    // Circle.draw(20, 0, 0);
    //
    // Shaders.terrainShader.startShader(gl);
    //
    // gl.glActiveTexture(GL2.GL_TEXTURE0+1);
    // gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.biomeShaderTest.getTexture().getTextureObject());
    // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    // gl.glActiveTexture(GL2.GL_TEXTURE0);
    //
    // Global.drawColor(new Color("#ffffff"));
    // Globe.draw(ImageResources.generationTest, 20, 0, 0);
    // Shaders.terrainShader.stopShader(gl);
    // Globe.draw(ImageResources.capTest, 20, 0, 0);

    //end better planets

    Global.nextFrame();
    Editor.render();
  }
}
