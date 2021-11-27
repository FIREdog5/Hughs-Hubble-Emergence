package bin.graphics;

import bin.input.MouseInput;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

public class Renderer {

  private static GLWindow window = null;
  private static GLProfile profile = null;

  public static int screenWidth = 640;
  public static int screenHeight = 360;

  public static float unitsWide = 100;

  public static void init(boolean editor) {
    GLProfile.initSingleton();
    profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities caps = new GLCapabilities(profile);
    caps.setSampleBuffers(true);
    caps.setNumSamples(5);

    window = GLWindow.create(caps);
    window.setSize(screenWidth, screenHeight);
    window.setResizable(false);
    window.setFullscreen(true);
    if (editor) {
      window.addGLEventListener(new EditorEventListener());
    } else {
      window.addGLEventListener(new GameEventListener());
    }
    window.addMouseListener(new MouseInput());

    window.setVisible(true);
    window.requestFocus();
  }

  public static int getWindowHeight() {
    return window.getHeight();
  }

  public static int getWindowWidth() {
    return window.getWidth();
  }

  public static GLProfile getProfile() {
    return profile;
  }

  public static void render() {
    if (window == null) {
      return;
    }
    window.display();
  }

}
