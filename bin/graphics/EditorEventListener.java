package bin.graphics;

import bin.ClientMain;
import bin.editor.Editor;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

public class EditorEventListener extends EventListener {
  @Override
  public void display(GLAutoDrawable drawable) {
    GL2 gl = ClientMain.gl;
    gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

    Graphics.nextFrame();
    Editor.render();
  }
}
