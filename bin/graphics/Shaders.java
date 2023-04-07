package bin.graphics;

import bin.ClientMain;

import com.jogamp.opengl.GL2;
import java.io.File;

public class Shaders {

  public static ShaderProgram terrainShader;
  public static ShaderProgram blackHoleShader;
  public static ShaderProgram turbulenceShader;

  public static void init() {
    GL2 gl2 = ClientMain.gl;

    File vertexShader = new File("bin/graphics/shaders/default.vs");
		File fragmentShader = new File("bin/graphics/shaders/colorGradientShader.fs");
    String uniforms[] = {"heightSampler", "paletteSampler", "biomeSampler"};
    terrainShader = new ShaderProgram();
		if (!terrainShader.init(gl2, vertexShader, fragmentShader, uniforms)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}

    File blackHoleFS = new File("bin/graphics/shaders/blackHoleShader.fs");
    String bHUniforms[] = {"buffer_in", "params"};
    blackHoleShader = new ShaderProgram();
    if (!blackHoleShader.init(gl2, vertexShader, blackHoleFS, bHUniforms)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}

    File turbulenceFS = new File("bin/graphics/shaders/turbulence.fs");
    String turbulenceUniforms[] = {"heightSampler", "warpSampler", "offset"};
    turbulenceShader = new ShaderProgram();
    if (!turbulenceShader.init(gl2, vertexShader, turbulenceFS, turbulenceUniforms)) {
			throw new IllegalStateException("Unable to initiate the shaders!");
		}
  }
}
