package bin.graphics;

import bin.ClientMain;
import bin.engine.GameLoop;
import bin.resource.ImageResources;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class GameEventListener extends EventListener {
  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = ClientMain.gl;
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

<<<<<<< HEAD
    Shaders.terrainShader.startShader(gl);

    gl.glActiveTexture(GL2.GL_TEXTURE0+1);
    gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.biomeShaderTest.getTexture().getTextureObject());
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    gl.glActiveTexture(GL2.GL_TEXTURE0);

    Graphics.drawColor(new Color("#ffffff"));
    Graphics.drawImage(ImageResources.generationTest, 0, 16, 96, 32);

    Shaders.terrainShader.stopShader(gl);


    Graphics.drawImage(ImageResources.generationTest, 0, -16, 96, 32);

=======
>>>>>>> 8b0a433d5c7f00baf1497699ca50f9be7b797d9c

    Graphics.nextFrame();
     GameLoop.renderWorld();
  }
}
