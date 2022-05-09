package bin.test;

import bin.graphics.Renderer;
import bin.world.Camera;

import bin.resource.ImageResources;
import bin.graphics.objects.Circle;
import bin.graphics.objects.Global;
import bin.graphics.objects.Globe;
import bin.graphics.objects.GradientHalo;
import bin.graphics.objects.Image;
import bin.graphics.objects.Rect;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.Text;
import bin.graphics.Color;
import bin.graphics.ui.UIScreen;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UIText;
import bin.graphics.ui.UITextBlock;
import bin.graphics.Shaders;
import bin.ClientMain;

import bin.graphics.Renderer;

import java.awt.MouseInfo;
import java.awt.Point;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.GL2;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class GraphicsTest extends Thread{

    private boolean running;
    private static Camera camera;
    private static UIScreen screen;
    private static boolean mouseIsPressed = false;
    private static Point startLoc;

    public static void init() {
      (new GraphicsTest()).start();
    }

    public GraphicsTest () {
      this.setName("graphicsTest");
      this.running = true;
    }

    public void run() {
      this.running = true;

      camera = new Camera(0, 0, 40, 1, 40);

      screen = new UIScreen();


      UIBoxRow outerBoxRow = new UIBoxRow(screen);
      outerBoxRow.x = 0;
      outerBoxRow.minWidth = 0;
      outerBoxRow.minHeight = 0;
      outerBoxRow.y = (float)((float)Renderer.screenHeight / (float)Renderer.screenWidth) * (float)Renderer.unitsWide - 23;
      outerBoxRow.color = new Color("#00ff00");
      outerBoxRow.margin = 1;
      outerBoxRow.outlineWeight = .3f;

      UIBoxRow innerBoxRow1 = new UIBoxRow(outerBoxRow);
      innerBoxRow1.x = 0;
      innerBoxRow1.y = 0;
      innerBoxRow1.minWidth = 0;
      innerBoxRow1.minHeight = 0;
      innerBoxRow1.outlineColor = new Color("#ff0000");
      innerBoxRow1.padding = 1;
      innerBoxRow1.outlineWeight = .3f;
      innerBoxRow1.radius = 1;

      UIBoxRow innerBoxRow2 = new UIBoxRow(outerBoxRow);
      innerBoxRow2.x = 0;
      innerBoxRow2.y = 0;
      innerBoxRow2.minWidth = 2;
      innerBoxRow2.minHeight = 14;
      innerBoxRow2.color = new Color("#0000ff");
      innerBoxRow2.padding = 1;
      innerBoxRow2.outlineWeight = .3f;

      outerBoxRow.addChild(innerBoxRow1);
      outerBoxRow.addChild(innerBoxRow2);

      UIBoxCol outerBoxCol = new UIBoxCol(innerBoxRow1);
      outerBoxCol.x = 0;
      outerBoxCol.y = 0;
      outerBoxCol.minWidth = 0;
      outerBoxCol.minHeight = 0;
      outerBoxCol.color = new Color("#00ff00");
      outerBoxCol.margin = 1;
      outerBoxCol.padding = 1;
      outerBoxCol.outlineWeight = .3f;

      UICenter innerCenter = new UICenter(outerBoxCol);
      innerCenter.centerY = false;

      UIBoxCol innerBoxCol1 = new UIBoxCol(innerCenter);
      innerBoxCol1.x = 0;
      innerBoxCol1.y = 0;
      innerBoxCol1.minWidth = 15;
      innerBoxCol1.minHeight = 5;
      innerBoxCol1.outlineColor = new Color("#ff0000");
      innerBoxCol1.padding = 1;
      innerBoxCol1.margin = 1;
      innerBoxCol1.outlineWeight = .3f;
      innerBoxCol1.radius = 1;

      UIText helloWorldText = new UIText(innerBoxCol1, "Hello World!", .5f);
      helloWorldText.color = new Color("#ffffff");

      UITextBlock textBlock = new UITextBlock(innerBoxCol1, "\nline2... \n\n A really long line here that stretches multiple lines and has a_way_too_long_word_in_it_to_break_up \n \n line 3!", .5f);
      textBlock.textColor = new Color("#ffffff");
      textBlock.maxWidth = 15f;

      innerCenter.addChild(innerBoxCol1);
      innerBoxCol1.addChild(helloWorldText);
      innerBoxCol1.addChild(textBlock);

      UIBoxCol innerBoxCol2 = new UIBoxCol(outerBoxCol);
      innerBoxCol2.x = 0;
      innerBoxCol2.y = 0;
      innerBoxCol2.minWidth = 25;
      innerBoxCol2.minHeight = 2;
      innerBoxCol2.color = new Color("#0000ff");
      innerBoxCol2.padding = 1;
      innerBoxCol2.outlineWeight = .3f;

      UIBoxCol innerBoxCol3 = new UIBoxCol(innerBoxRow1);
      innerBoxCol3.x = 0;
      innerBoxCol3.y = 0;
      innerBoxCol3.minWidth = 2;
      innerBoxCol3.minHeight = 12;
      innerBoxCol3.color = new Color("#ffffff");
      innerBoxCol3.outlineColor = new Color("#000000");
      innerBoxCol3.padding = 1;
      innerBoxCol3.outlineWeight = .3f;

      outerBoxCol.addChild(innerCenter);
      outerBoxCol.addChild(innerBoxCol2);

      innerBoxRow1.addChild(outerBoxCol);
      innerBoxRow1.addChild(innerBoxCol3);
      screen.addChild(outerBoxRow);

      while (this.running) {
        if (mouseIsPressed) {
          Point currentLoc = MouseInfo.getPointerInfo().getLocation();
          float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
          camera.adjustLocation(camera.zoomToScale((float)(startLoc.x - currentLoc.x) / Renderer.getWindowWidth() * Renderer.unitsWide), camera.zoomToScale((float)(currentLoc.y - startLoc.y) / Renderer.getWindowHeight() * unitsTall));
          startLoc = currentLoc;
        }
        Renderer.render();
      }
    }

    public static void render() {
      if (camera == null) {
        return;
      }

      GL2 gl = ClientMain.gl;

      //draw stary test field
      Image.draw(ImageResources.primordialGalaxy, camera.convertXToCamera(110), camera.convertYToCamera(50), camera.scaleToZoom(162), camera.scaleToZoom(108));


      //draw circles
      Global.drawColor(new Color("#ffffff"));
      float[] circles = {.1f, .5f, 1f, 2f, 5f, 10f, 20f, 50f};
      int circleX = -82;
      int circleY = 58;
      float circleSum = -circles[0];
      for (int i = 0; i < circles.length; i++) {
        Circle.draw(camera.scaleToZoom(circles[i]), camera.convertXToCamera(circleX - circleSum - i - circles[i]), camera.convertYToCamera(circleY));
        circleSum += 2 * circles[i];
        }

      //draw rotated rectangles
      Global.drawColor(new Color("#ff0000"));
      int numRects = 12;
      int rectX = -85;
      int rectY = 53;
      for (int i = 0; i < numRects; i++) {
        Rect.draw(camera.convertXToCamera(rectX + (i * 4)), camera.convertYToCamera(rectY), camera.scaleToZoom(3), camera.scaleToZoom(2), 360f / numRects * i);
      }

      //draw rectangles
      Global.drawColor(new Color("#ff0000"));
      float[] rects = {.1f, .5f, 1f, 2f, 5f, 10f, 20f};
      rectY += 3;
      float rectSum = -rects[0] / 2;
      for (int i = 0; i < rects.length; i++) {
        Rect.draw(camera.convertXToCamera(rectX + rectSum + i + rects[i] / 2), camera.convertYToCamera(rectY + rects[i] / 2), camera.scaleToZoom(rects[i]), camera.scaleToZoom(rects[i]));
        rectSum += rects[i];
      }

      //draw text
      float size = camera.scaleToZoom(10f);
      String text = "Hello World!";
      Global.drawColor(new Color("#00ff00"));
      Rect.draw(camera.convertXToCamera(-23), camera.convertYToCamera(87), Text.getWidth(text, size), Text.getHeight(text, size));
      Text.draw(text, camera.convertXToCamera(-23), camera.convertYToCamera(87), new Color("#ffffff"), size);
      size = camera.scaleToZoom(3f);
      Global.drawColor(new Color("#00ff00"));
      Rect.draw(camera.convertXToCamera(-23), camera.convertYToCamera(67), Text.getWidth(text, size), Text.getHeight(text, size));
      Text.draw(text, camera.convertXToCamera(-23), camera.convertYToCamera(67), new Color("#ffffff"), size);
      size = camera.scaleToZoom(30f);
      Global.drawColor(new Color("#00ff00"));
      Rect.draw(camera.convertXToCamera(-23), camera.convertYToCamera(127), Text.getWidth(text, size), Text.getHeight(text, size));
      Text.draw(text, camera.convertXToCamera(-23), camera.convertYToCamera(127), new Color("#ffffff"), size);

      //draw UI
      screen.render();

      //render blackhole
      Shaders.blackHoleShader.startShader(gl);
      int[] textures = new int[1];
      gl.glGenTextures(1, textures, 0);
      gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      int bhSize = 10 * Renderer.getWindowWidth() / 100;
      bhSize = bhSize % 2 == 0 ? bhSize : bhSize + 1;
      FloatBuffer buffer = FloatBuffer.allocate(4 * bhSize * bhSize);
      // System.out.println(Arrays.toString(buffer.array()));
      gl.glReadBuffer(GL2.GL_BACK);
      gl.glReadPixels(Renderer.getWindowWidth() / 2 - bhSize / 2, Renderer.getWindowHeight() / 2 - bhSize / 2, bhSize, bhSize, GL2.GL_RGBA, GL2.GL_FLOAT, buffer);
      gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, bhSize, bhSize, 0, GL2.GL_RGBA, GL2.GL_FLOAT, buffer);
      // System.out.println(Arrays.toString(buffer.array()));
      // System.out.println(gl.glGetError());

      // Texture tex = new Texture(gl, texD);
      // gl.glCopyTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, Renderer.screenWidth / 2 - 5, Renderer.screenHeight / 2 - 5, 10, 10, 0);
      // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
      // gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      Image.draw((Texture)null, 0, 0, 10, 10);
      Shaders.blackHoleShader.stopShader(gl);
      // Global.drawColor(new Color("#000000"));
      // Circle.draw(10f/6f + .05f, 0, 0);
      // Global.drawColor(new Color("#ffffff"));
    }

    public static void zoomWorld(float deltaZoom) {
      if (camera == null) {
        return;
      }
      camera.adjustZoom(deltaZoom);
    }

    public static void mousePressed() {
      startLoc = MouseInfo.getPointerInfo().getLocation();
      mouseIsPressed = true;
    }

    public static void mouseReleased() {
      mouseIsPressed = false;
    }

}
