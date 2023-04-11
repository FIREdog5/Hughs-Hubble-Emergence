package bin;
import bin.graphics.Renderer;
import bin.engine.GameLoop;
import bin.editor.Editor;
import bin.test.GraphicsTest;

import com.jogamp.opengl.GL2;
import java.util.Arrays;
import com.jogamp.opengl.util.awt.TextRenderer;

public class ClientMain{
  public static GL2 gl = null;
  public static TextRenderer textRenderer = null;

  public static void main(String[] args) {
    String fullscreen = "";
    if (Arrays.asList(args).contains("borderless")) {
      fullscreen = "borderless";
    } else if (Arrays.asList(args).contains("bordered")) {
      fullscreen = "bordered";
      System.out.println("[WARNING] bordered mode is not fully implemented");
    }
    if (Arrays.asList(args).contains("editor")) {
      Renderer.init("editor", fullscreen);
      Editor.init();
    } else if (Arrays.asList(args).contains("graphicsTest")) {
      Renderer.init("graphicsTest", fullscreen);
      GraphicsTest.init();
    } else {
      Renderer.init("", fullscreen);
      GameLoop.init();
    }
  }
}
