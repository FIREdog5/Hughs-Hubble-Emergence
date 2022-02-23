package bin.graphics;

import bin.ClientMain;

import com.jogamp.opengl.GL2;
import java.io.File;

public class Shaders {

  public static ShaderProgram terrainShader;

  public static void init() {
    GL2 gl2 = ClientMain.gl;

    File vertexShader = new File("bin/graphics/shaders/default.vs");
		File fragmentShader = new File("bin/graphics/shaders/colorGradientShader.fs");
    String uniforms[] = {"text_in", "inSampler"};

    terrainShader = new ShaderProgram();
		if (!terrainShader.init(gl2, vertexShader, fragmentShader, uniforms)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}
  }
}