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
    TextRenderer textRenderer = ClientMain.textRenderer;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitsWide = Renderer.unitsWide;
    textRenderer.begin3DRendering();
    Rectangle2D bounds = textRenderer.getBounds(text);
    textRenderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    // 3.5f, and 3f are magic numbers that were found experimentally.
    // so is .05f.
    textRenderer.draw3D(text, x - ((float) bounds.getWidth() + 3.5f) * .05f * size / 2, y - ((float) bounds.getHeight() - 3f) * .05f * size / 2, 0, .05f * size);
    textRenderer.end3DRendering();
  }

  public static float getHeight(String text, float size) {
    TextRenderer textRenderer = ClientMain.textRenderer;
    Rectangle2D bounds = textRenderer.getBounds(text);
    // .05f is a magic number that was found experimentally.
    return (float) bounds.getHeight() * .05f * size;
  }

  public static float getWidth(String text, float size) {
    TextRenderer textRenderer = ClientMain.textRenderer;
    Rectangle2D bounds = textRenderer.getBounds(text);
    // .05f is a magic number that was found experimentally.
    return ((float) bounds.getWidth()) * .05f * size;
  }

}
