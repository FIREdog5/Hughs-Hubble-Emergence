package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;
import bin.graphics.Renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.geom.Rectangle2D;

public class Text {
  public static void draw(String text, float x, float y, Color color, float size) {
    GL2 gl = ClientMain.gl;
    gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

    TextRenderer textRenderer = ClientMain.textRenderer;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitsWide = Renderer.unitsWide;
    textRenderer.begin3DRendering();
    Rectangle2D bounds = textRenderer.getBounds(text);
    textRenderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    // 3.5f, and 10f are magic numbers that were found experimentally.
    // so is .05f.
    textRenderer.draw3D(text, x - getWidth(text, size) / 2, y - .5f * size, 0, .05f * size);

    textRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    textRenderer.end3DRendering();
  }

  public static float getHeight(String text, float size) {
    TextRenderer textRenderer = ClientMain.textRenderer;
    Rectangle2D bounds = textRenderer.getBounds(text);
    // .05f is a magic number that was found experimentally.
    return (float) bounds.getHeight() * .07f * size;
  }

  public static float getWidth(String text, float size) {
    TextRenderer textRenderer = ClientMain.textRenderer;
    Rectangle2D bounds = textRenderer.getBounds("-" + text + "-");
    Rectangle2D dashBounds = textRenderer.getBounds("--");
    // .05f is a magic number that was found experimentally.
    return ((float) bounds.getWidth() - (float)dashBounds.getWidth()) * .05f * size;
  }

}
