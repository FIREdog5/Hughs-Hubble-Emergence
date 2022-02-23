package bin.graphics;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import com.jogamp.opengl.GL2;

/**
 * Manages the shader program.
 *
 * @author serhiy
 */
public class ShaderProgram {
	private int programId;
	private int vertexShaderId;
	private int fragmentShaderId;
	private String[] uniforms;
	private boolean initialized = false;

	/**
	 * Initializes the shader program.
	 *
	 * @param gl2 context.
	 * @param vertexShader file.
	 * @param fragmentShader file.
	 * @return true if initialization was successful, false otherwise.
	 */
	public boolean init(GL2 gl2, File vertexShader, File fragmentShader, String[] uniforms) {
		if (initialized) {
			throw new IllegalStateException(
					"Unable to initialize the shader program! (it was already initialized)");
		}

		try {
			String vertexShaderCode = ShaderUtils.loadResource(vertexShader
					.getPath());
			String fragmentShaderCode = ShaderUtils.loadResource(fragmentShader
					.getPath());

			programId = gl2.glCreateProgram();
			vertexShaderId = ShaderUtils.createShader(gl2, programId,
					vertexShaderCode, GL2.GL_VERTEX_SHADER);
			fragmentShaderId = ShaderUtils.createShader(gl2, programId,
					fragmentShaderCode, GL2.GL_FRAGMENT_SHADER);

			ShaderUtils.link(gl2, programId);

			this.uniforms = uniforms;

			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return initialized;

	}

	/**
	 * Destroys the shader program.
	 *
	 * @param gl2 context.
	 */
	public void dispose(GL2 gl2) {
		initialized = false;
		gl2.glDetachShader(programId, vertexShaderId);
		gl2.glDetachShader(programId, fragmentShaderId);
		gl2.glDeleteProgram(programId);
	}

	/**
	 * @return shader program id.
	 */
	public int getProgramId() {
		if (!initialized) {
			throw new IllegalStateException(
					"Unable to get the program id! The shader program was not initialized!");
		}
		return programId;
	}

	public void startShader(GL2 gl2) {
		if (!initialized) {
			throw new IllegalStateException(
					"Unable to get the attribute location! The shader program was not initialized!");
		}
		gl2.glUseProgram(this.getProgramId());

		for (int i = 0; i < this.uniforms.length; i++) {

	    gl2.glUniform1i(gl2.glGetUniformLocation(this.getProgramId(), uniforms[i]), i);
		}
	}

	public void stopShader(GL2 gl2) {
		gl2.glUseProgram(0);
	}

	public boolean isInitialized() {
		return initialized;
	}
}
