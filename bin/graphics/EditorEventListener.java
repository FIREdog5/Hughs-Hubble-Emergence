package bin.graphics;

import bin.ClientMain;
import bin.editor.Editor;
import bin.resource.ImageResources;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class EditorEventListener extends EventListener {
  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = ClientMain.gl;
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    // Graphics.drawGradientHalo(22, 20, 0, 0, new Color("#03fcf8"), new Color("#0000ff", .3f));
    // Graphics.drawColor(new Color("#0000ff"));
    // Graphics.drawCircle(20, 0, 0);
    //
    // Shaders.terrainShader.startShader(gl);
    //
    // gl.glActiveTexture(GL2.GL_TEXTURE0+1);
    // gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.biomeShaderTest.getTexture().getTextureObject());
    // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    // gl.glActiveTexture(GL2.GL_TEXTURE0);
    //
    // Graphics.drawColor(new Color("#ffffff"));
    // Graphics.drawGlobe(ImageResources.generationTest, 20, 0, 0);
    // Shaders.terrainShader.stopShader(gl);
    // Graphics.drawGlobe(ImageResources.capTest, 20, 0, 0);


    Graphics.nextFrame();
    Editor.render();
  }
}
