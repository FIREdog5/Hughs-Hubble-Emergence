package bin;
import bin.graphics.Renderer;
import bin.engine.GameLoop;
import bin.editor.Editor;

import com.jogamp.opengl.GL2;
import java.util.Arrays;

public class ClientMain{
  public static GL2 gl = null;
  public static void main(String[] args) {
    if (Arrays.asList(args).contains("--editor")) {
      Renderer.init(true);
      Editor.init();
    } else {
      Renderer.init(false);
      GameLoop.init();
    }
  }
}
