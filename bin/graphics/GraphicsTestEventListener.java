package bin.graphics;

import bin.ClientMain;
import bin.editor.Editor;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import bin.graphics.objects.Global;
import bin.test.GraphicsTest;

public class GraphicsTestEventListener extends EventListener {
  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = ClientMain.gl;
    gl.glClearColor(0f, 0f, 0f, 1.0f);
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    // GradientHalo.draw(22, 20, 0, 0, new Color("#03fcf8"), new Color("#0000ff", .3f));
    // Global.drawColor(new Color("#0000ff"));
    // Circle.draw(20, 0, 0);
    //
    // // Shaders.terrainShader.startShader(gl);
    //
    // gl.glActiveTexture(GL2.GL_TEXTURE0+1);
    // gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.biomeShaderTest.getTexture().getTextureObject());
    // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    // gl.glActiveTexture(GL2.GL_TEXTURE0);
    //
    // Global.drawColor(new Color("#ffffff"));
    // Globe.draw(ImageResources.generationTest, 20, 0, 0);
    // // Shaders.terrainShader.stopShader(gl);
    // Globe.draw(ImageResources.capTest, 20, 0, 0);

    // Global.drawColor(new Color("#ffffff"));
    // RoundedBoxOutline.draw(0, 0, 20, 20, 5, .2f);
    // RoundedBoxOutline.draw(0, 0, 17, 10, 2, .1f);
    // RoundedBox.draw(0, 0, 4.5f, 9, 1);

    Global.nextFrame();
    GraphicsTest.render();
  }
}
