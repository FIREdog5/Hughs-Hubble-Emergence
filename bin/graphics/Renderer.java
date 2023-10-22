package bin.graphics;
import com.jogamp.opengl.GL2;

import bin.ClientMain;

import bin.input.MouseInput;
import bin.input.KeyInput;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;

public class Renderer {

  private static GLWindow window = null;
  private static GLProfile profile = null;

  public static int screenWidth = 640;
  public static int screenHeight = 360;

  public static float unitsWide = 100;

  public static FramebufferController framebufferController = new FramebufferController();

  public static void init(String mode, String fullscreen) {
    GLProfile.initSingleton();
    profile = GLProfile.get(GLProfile.GL2);
    GLCapabilities caps = new GLCapabilities(profile);
    caps.setSampleBuffers(true);
    caps.setNumSamples(5);

    window = GLWindow.create(caps);

    switch (fullscreen){
      case "borderless":
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        DisplayMode displayMode = gs[0].getDisplayMode();
        screenWidth = displayMode.getWidth();
        screenHeight = displayMode.getHeight();
        window.setUndecorated(true);
        break;
      case "bordered":
        break;
      case "":
      case "fullscreen":
        window.setFullscreen(true);
        break;
    }
    window.setSize(screenWidth, screenHeight);
    window.setResizable(false);

    switch (mode) {
      case "editor":
        window.addGLEventListener(new EditorEventListener());
        break;
      case "graphicsTest":
        window.addGLEventListener(new GraphicsTestEventListener());
        break;
      case "":
        window.addGLEventListener(new GameEventListener());
        break;
    }
    window.addMouseListener(new MouseInput());
    window.addKeyListener(new KeyInput());

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

  public static GLWindow getWindow() {
    return window;
  }

  public static void render() {
    if (window == null) {
      return;
    }
    window.display();
  }

}
