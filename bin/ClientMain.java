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
    if (Arrays.asList(args).contains("--editor")) {
      Renderer.init("editor");
      Editor.init();
    } else if (Arrays.asList(args).contains("--graphicsTest")) {
      Renderer.init("graphicsTest");
      GraphicsTest.init();
    } else {
      Renderer.init("");
      GameLoop.init();
    }
  }
}
