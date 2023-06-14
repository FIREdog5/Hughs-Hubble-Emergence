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
import bin.graphics.objects.ReRendered;
import bin.graphics.objects.ColorWheel;
import bin.graphics.objects.Pointer;
import bin.graphics.objects.PointerOutline;
import bin.graphics.objects.ScreenArea;
import bin.graphics.objects.Line;
import bin.graphics.Color;
import bin.graphics.ui.UIScreen;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UIText;
import bin.graphics.ui.UITextBlock;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIImage;
import bin.graphics.ui.UISelectable;
import bin.graphics.ui.complex.UITextInput;
import bin.graphics.ui.complex.UINumberInput;
import bin.graphics.ui.complex.UISelectionMenu;
import bin.graphics.ui.complex.UIPopUp;
import bin.graphics.ui.complex.UIToolTip;
import bin.graphics.ui.complex.UITestCircle;
import bin.graphics.ui.complex.UIScrollableBox;
import bin.graphics.Shaders;
import bin.ClientMain;
import bin.graphics.Renderer;
import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.input.Key;
import bin.Wrapper;

import java.awt.MouseInfo;
import java.awt.Point;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.GL2;
import java.nio.FloatBuffer;
import java.util.Arrays;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import com.jogamp.newt.event.KeyEvent;

public class GraphicsTest extends Thread{

    private boolean running;
    private static Camera camera;
    private static UIScreen screen;
    private static boolean mouseIsPressed = false;
    private static Point startLoc;
    private static ClickHandler clickHandler;
    private static KeyboardHandler keyboardHandler;

    private static int blackholeFBO = -1;

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

      clickHandler = new ClickHandler();
      keyboardHandler = new KeyboardHandler();

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

      UIBoxCol containerBox = new UIBoxCol(outerBoxRow);
      containerBox.color = new Color("#00ffff");
      containerBox.outlineWeight = 0f;

      outerBoxRow.addChild(containerBox);


      UIScrollableBox scrollBox = new UIScrollableBox(containerBox);
      scrollBox.maxHeight = 10;
      scrollBox.scrollbarWidth = 1;
      scrollBox.outlineColor = new Color("#444444");
      scrollBox.color = new Color("#ffffff");
      scrollBox.scrollbarColor = new Color("#aaaaaa");
      scrollBox.scrollbarMouseOverColor = new Color("#eeeeee");
      scrollBox.padding = 1;
      scrollBox.outlineWeight = .3f;
      containerBox.addChild(scrollBox);
      clickHandler.register(scrollBox);

      for (int i = 0; i < 10; i++) {
        UIButton scrolledBox = new UIButton(scrollBox);
        scrolledBox.minWidth = 4;
        scrolledBox.minHeight = 4;
        scrolledBox.color = Color.randomColor();
        scrolledBox.outlineColor = new Color("#ffffff");
        scrolledBox.padding = 1;
        scrolledBox.outlineWeight = .1f;
        scrolledBox.mouseOverColor = new Color("#000000");
        scrollBox.addChild(scrolledBox);
        clickHandler.register(scrolledBox);
      }

      UIBoxCol outliner = new UIBoxCol(containerBox);
      outliner.padding = 1f;
      outliner.margin = .3f;
      outliner.outlineColor = new Color("#ff0000");
      outliner.noBackground = true;
      outliner.outlineWeight = .3f;
      containerBox.addChild(outliner);


      UIImage testImageRotator = new UIImage(outliner, ImageResources.error){
        @Override
        public void render() {
          super.render();
          this.rotation += .1f;
          this.rotation %= 360f;
        }
      };
      testImageRotator.minWidth = 6f;
      testImageRotator.minHeight = 3f;
      outliner.addChild(testImageRotator);

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

      UIBoxRow innerBoxRowWrapper = new UIBoxRow(innerCenter);
      innerBoxRowWrapper.outlineWeight = 0f;
      innerBoxRowWrapper.noBackground = true;

      UIBoxCol innerBoxCol1 = new UIBoxCol(innerBoxRowWrapper);
      innerBoxCol1.x = 0;
      innerBoxCol1.y = 0;
      innerBoxCol1.minWidth = 15;
      innerBoxCol1.minHeight = 5;
      innerBoxCol1.outlineColor = new Color("#ff0000");
      innerBoxCol1.margin = 1;
      innerBoxCol1.outlineWeight = .3f;
      innerBoxCol1.radius = 1;

      UIText helloWorldText = new UIText(innerBoxCol1, "Hello World!", .5f);
      helloWorldText.color = new Color("#ffffff");

      UITextBlock textBlock = new UITextBlock(innerBoxCol1, "\nline2... \n\n A really long line here that stretches multiple lines and has a_way_too_long_word_in_it_to_break_up \n \n line 3!", .5f);
      textBlock.textColor = new Color("#ffffff");
      textBlock.maxWidth = 15f;

      UIBoxCol selectableCol = new UIBoxCol(innerBoxRowWrapper);
      selectableCol.outlineWeight = 0f;
      selectableCol.noBackground = true;

      UISelectable selectable1 = new UISelectable(selectableCol);
      selectable1.x = 0;
      selectable1.y = 0;
      selectable1.minWidth = 3;
      selectable1.minHeight = 2;
      selectable1.color = new Color("#ffffff");
      selectable1.mouseOverColor = new Color("#aaaaaa");
      selectable1.selectedColor = new Color("#666666");
      selectable1.mouseOverOutlineColor = new Color("#000000");
      selectable1.selectedOutlineColor = new Color("#0000ff");
      selectable1.padding = 1;
      selectable1.outlineWeight = .3f;

      Wrapper<String> stringWrapper1 = new Wrapper<String>("");

      UISelectionMenu selectable2 = new UISelectionMenu(selectableCol, screen, clickHandler){
        @Override
        public String getValue() {
          return stringWrapper1.get();
        }

        @Override
        public void setValue(String newValue) {
          stringWrapper1.set(newValue);
        }
      };
      selectable2.x = 0;
      selectable2.y = 0;
      selectable2.minWidth = 3;
      selectable2.minHeight = 2f;
      selectable2.selectionBoxHeight = 6;
      selectable2.color = new Color("#ffffff");
      selectable2.mouseOverColor = new Color("#aaaaaa");
      selectable2.selectedColor = new Color("#666666");
      selectable2.mouseOverOutlineColor = new Color("#000000");
      selectable2.selectedOutlineColor = new Color("#0000ff");
      selectable2.padding = 1;
      selectable2.outlineWeight = .3f;
      selectable2.margin = .3f;

      selectableCol.addChild(selectable1);
      selectableCol.addChild(selectable2);

      for (int i = 0; i < 20; i++) {
        UISelectable entry = new UISelectable(selectable2.scrollableList);
        entry.x = 0;
        entry.y = 0;
        entry.minWidth = 3;
        entry.minHeight = 1.5f;
        entry.color = new Color("#ffffff");
        entry.mouseOverColor = new Color("#5B90C1");
        entry.outlineWeight = 0f;
        entry.setZ(10);

        selectable2.addOption(entry, Integer.toString(i));
        clickHandler.register(entry);

        UICenter entryCenter = new UICenter(entry);
        entryCenter.centerY = true;
        entryCenter.centerX = false;

        entry.addChild(entryCenter);

        UIBoxRow entryContents = new UIBoxRow(entryCenter);
        entryContents.noBackground = true;
        entryContents.outlineWeight = 0f;

        entryCenter.addChild(entryContents);

        UIBoxRow entryPadding = new UIBoxRow(entryContents);
        entryPadding.minWidth = .3f;
        entryPadding.noBackground = true;
        entryPadding.outlineWeight = 0f;

        entryContents.addChild(entryPadding);

        UITextBlock entryText = new UITextBlock(entryContents, Integer.toString(i), .5f);
        entryText.textColor = new Color("#000000");
        entryText.noBackground = true;
        entryText.maxWidth = 2.7f;

        entryContents.addChild(entryText);
      }

      Wrapper<String> stringWrapper2 = new Wrapper<String>("");

      UITextInput selectable3 = new UITextInput(selectableCol, 0.5f){
        @Override
        public String getValue() {
          return stringWrapper2.get();
        }

        @Override
        public boolean setValue(String newValue) {
          stringWrapper2.set(newValue);
          return true;
        }
      };
      selectable3.x = 0;
      selectable3.y = 0;
      selectable3.minWidth = 3;
      selectable3.minHeight = 2;
      selectable3.color = new Color("#ffffff");
      selectable3.mouseOverOutlineColor = new Color("#000000");
      selectable3.selectedOutlineColor = new Color("#0000ff");
      selectable3.padding = 1;
      selectable3.outlineWeight = .3f;

      selectableCol.addChild(selectable3);

      Wrapper<String> stringWrapper3 = new Wrapper<String>("");

      UINumberInput selectable4 = new UINumberInput(selectableCol, 0.5f, clickHandler){
        @Override
        public String getValue() {
          return stringWrapper3.get();
        }

        @Override
        public boolean setValue(String newValue) {
          stringWrapper3.set(newValue);
          return true;
        }
      };
      selectable4.x = 0;
      selectable4.y = 0;
      selectable4.minWidth = 3;
      selectable4.minHeight = 2;
      selectable4.color = new Color("#ffffff");
      selectable4.mouseOverOutlineColor = new Color("#000000");
      selectable4.selectedOutlineColor = new Color("#0000ff");
      selectable4.padding = 1;
      selectable4.outlineWeight = .3f;

      selectable4.incrementButton.mouseOverOutlineColor = new Color("#000000");
      selectable4.incrementButton.outlineColor = new Color("#ff0000");
      selectable4.incrementButton.color = new Color("#cccccc");
      selectable4.incrementIcon.padding = .3f;
      selectable4.incrementIcon.minWidth = .8f;
      selectable4.incrementIcon.minHeight = .4f;

      selectable4.decrementButton.mouseOverOutlineColor = new Color("#000000");
      selectable4.decrementButton.outlineColor = new Color("#ff0000");
      selectable4.decrementButton.color = new Color("#cccccc");
      selectable4.decrementIcon.padding = .3f;
      selectable4.decrementIcon.minWidth = .8f;
      selectable4.decrementIcon.minHeight = .4f;

      // selectableCol.addChild(selectable4);

      clickHandler.register(selectable1);
      clickHandler.register(selectable2);
      clickHandler.register(selectable3);
      clickHandler.register(selectable4);

      keyboardHandler.register(selectable3);
      keyboardHandler.register(selectable4);

      innerCenter.addChild(innerBoxRowWrapper);
      innerBoxRowWrapper.addChild(innerBoxCol1);
      innerBoxRowWrapper.addChild(selectableCol);
      innerBoxCol1.addChild(helloWorldText);
      innerBoxCol1.addChild(textBlock);

      UIButton innerButton = new UIButton(outerBoxCol);
      innerButton.x = 0;
      innerButton.y = 0;
      innerButton.minWidth = 25;
      innerButton.minHeight = 2;
      innerButton.color = new Color("#ffff00");
      innerButton.mouseOverColor = new Color("#0000ff");
      innerButton.mouseOverOutlineColor = new Color("#000000");
      innerButton.padding = 1;
      innerButton.outlineWeight = .3f;

      clickHandler.register(innerButton);

      UIButton innerButton2 = new UIButton(innerButton);
      innerButton2.minWidth = 2;
      innerButton2.minHeight = 1;
      innerButton2.color = new Color("#ffffff");
      innerButton2.mouseOverColor = new Color("#777777");
      innerButton2.padding = .5f;
      innerButton2.setZ(10);

      clickHandler.register(innerButton2);
      innerButton.addChild(innerButton2);

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
      outerBoxCol.addChild(innerButton);

      innerBoxRow1.addChild(outerBoxCol);
      innerBoxRow1.addChild(innerBoxCol3);
      screen.addChild(outerBoxRow);

      UITestCircle testCircle = new UITestCircle(screen);
      testCircle.circleRadius = 2;
      testCircle.color = new Color("#0000ff");
      testCircle.mouseOverColor = new Color("#2288ff");
      testCircle.x = -47;

      screen.addChild(testCircle);
      clickHandler.register(testCircle);

      UIToolTip popUp = new UIToolTip(screen);
      popUp.anchor = testCircle;
      popUp.position = "top";
      popUp.offset = .3f;
      popUp.margin = .6f;
      popUp.color = new Color("#0000ff");
      popUp.outlineColor = new Color("#AAAAAA");
      popUp.outlineWeight = .3f;

      UITextBlock toolTipTextBlock = new UITextBlock(popUp, "this is a tool-tip!", .5f);
      toolTipTextBlock.textColor = new Color("#ffffff");
      toolTipTextBlock.maxWidth = 4f;

      popUp.addChild(toolTipTextBlock);

      screen.addChild(popUp);

      while (this.running) {
        Point currentLoc = MouseInfo.getPointerInfo().getLocation();
        if (mouseIsPressed && !clickHandler.isMouseOnElement()) {
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

      if (blackholeFBO == -1) {
        blackholeFBO = Renderer.framebufferController.createNewFrame();
      }

      Renderer.framebufferController.switchToFrame(blackholeFBO, false);

      GL2 gl = ClientMain.gl;

      //draw stary test field
      Global.drawColor(new Color("#ffffff"));
      Image.draw(ImageResources.primordialGalaxy, camera.convertXToCamera(110), camera.convertYToCamera(50), camera.scaleToZoom(162), camera.scaleToZoom(108));

      //draw test images
      Global.drawColor(new Color("#ffffff"));
      Image.draw(ImageResources.sphericalTest, camera.convertXToCamera(110), camera.convertYToCamera(-19), camera.scaleToZoom(93), camera.scaleToZoom(30));
      Globe.draw(ImageResources.sphericalTest, camera.scaleToZoom(15), camera.convertXToCamera(110), camera.convertYToCamera(-49));
      Globe.draw(ImageResources.earthTest, camera.scaleToZoom(15), camera.convertXToCamera(140), camera.convertYToCamera(-49));
      Image.drawNoAA(ImageResources.gradientTest, camera.convertXToCamera(192), camera.convertYToCamera(0), camera.scaleToZoom(1), camera.scaleToZoom(130), 0);

      //draw gas giant test
      Global.drawColor(new Color("#ffffff"));
      Globe.draw(ImageResources.stripeTest, camera.scaleToZoom(15), camera.convertXToCamera(110), camera.convertYToCamera(-79));
      Globe.draw(ImageResources.domainWarpRBTest, camera.scaleToZoom(15), camera.convertXToCamera(140), camera.convertYToCamera(-79));

      Shaders.turbulenceShader.startShader(gl);
      float frame = Global.getFrame();
      float offset = (frame % ImageResources.domainWarpRBTest.getFrameDelay()) / (float)ImageResources.domainWarpRBTest.getFrameDelay();
      gl.glUniform1f(gl.glGetUniformLocation(Shaders.turbulenceShader.getProgramId(), "offset"), offset);
      gl.glActiveTexture(GL2.GL_TEXTURE0+1);
      gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.domainWarpRBTest.getTexture().getTextureObject());
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
      gl.glActiveTexture(GL2.GL_TEXTURE0);
      Global.drawColor(new Color("#ffffff"));
      Globe.draw(ImageResources.stripeTest, camera.scaleToZoom(15), camera.convertXToCamera(170), camera.convertYToCamera(-79));
      Shaders.turbulenceShader.stopShader(gl);

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
      Rect.draw(camera.convertXToCamera(-23), camera.convertYToCamera(97), Text.getWidth(text, size), Text.getHeight(text, size));
      Text.draw(text, camera.convertXToCamera(-23), camera.convertYToCamera(97), new Color("#ffffff"), size);
      size = camera.scaleToZoom(3f);
      Global.drawColor(new Color("#00ff00"));
      Rect.draw(camera.convertXToCamera(-23), camera.convertYToCamera(77), Text.getWidth(text, size), Text.getHeight(text, size));
      Text.draw(text, camera.convertXToCamera(-23), camera.convertYToCamera(77), new Color("#ffffff"), size);
      size = camera.scaleToZoom(30f);
      Global.drawColor(new Color("#00ff00"));
      Rect.draw(camera.convertXToCamera(-23), camera.convertYToCamera(147), Text.getWidth(text, size), Text.getHeight(text, size));
      Text.draw(text, camera.convertXToCamera(-23), camera.convertYToCamera(147), new Color("#ffffff"), size);

      //draw colorwheel
      ColorWheel.draw(camera.scaleToZoom(10f), camera.convertXToCamera(-85), camera.convertYToCamera(75));

      //pointers
      Global.drawColor(new Color("#0000ff"));
      Pointer.draw(camera.convertXToCamera(-34f), camera.convertYToCamera(70f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "right");
      Pointer.draw(camera.convertXToCamera(-34f), camera.convertYToCamera(65f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "left");
      Pointer.draw(camera.convertXToCamera(-25.5f), camera.convertYToCamera(67.5f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "up");
      Pointer.draw(camera.convertXToCamera(-20.5f), camera.convertYToCamera(67.5f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "down");
      Pointer.draw(camera.convertXToCamera(-13f), camera.convertYToCamera(67.5f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), 45f);

      Global.drawColor(new Color("#ffffff"));
      PointerOutline.draw(camera.convertXToCamera(-34f), camera.convertYToCamera(70f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "right", camera.scaleToZoom(.3f));
      PointerOutline.draw(camera.convertXToCamera(-34f), camera.convertYToCamera(65f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "left", camera.scaleToZoom(.3f));
      PointerOutline.draw(camera.convertXToCamera(-25.5f), camera.convertYToCamera(67.5f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "up", camera.scaleToZoom(.3f));
      PointerOutline.draw(camera.convertXToCamera(-20.5f), camera.convertYToCamera(67.5f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), "down", camera.scaleToZoom(.3f));
      PointerOutline.draw(camera.convertXToCamera(-13f), camera.convertYToCamera(67.5f), camera.scaleToZoom(9f), camera.scaleToZoom(3f), 45f, camera.scaleToZoom(.3f));

      //line
      Global.drawColor(new Color("#00ff00"));
      int lineEnd = 0;
      for (lineEnd = 0; lineEnd < 7; lineEnd++){
        Line.draw(camera.convertXToCamera(-23f), camera.convertYToCamera(57f), camera.convertXToCamera(-38f + lineEnd * 5f), camera.convertYToCamera(61f), camera.scaleToZoom(.3f));
      }
      for (lineEnd = 1; lineEnd < 4; lineEnd++){
        Line.draw(camera.convertXToCamera(-23f), camera.convertYToCamera(57f), camera.convertXToCamera(-8f), camera.convertYToCamera(61f - lineEnd * 2f), camera.scaleToZoom(.3f));
      }
      for (lineEnd = 0; lineEnd < 7; lineEnd++){
        Line.draw(camera.convertXToCamera(-23f), camera.convertYToCamera(57f), camera.convertXToCamera(-38f + lineEnd * 5f), camera.convertYToCamera(53f), camera.scaleToZoom(.3f));
      }
      for (lineEnd = 1; lineEnd < 4; lineEnd++){
        Line.draw(camera.convertXToCamera(-23f), camera.convertYToCamera(57f), camera.convertXToCamera(-38f), camera.convertYToCamera(61f - lineEnd * 2f), camera.scaleToZoom(.3f));
      }

      //draw blackhole
      float bhX = 0f;
      float bhY = 0f;
      float bhSize = 10f;

      Renderer.framebufferController.popFrameAndBind();
      float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
      Global.drawColor(new Color("#ffffff"));
      ScreenArea.draw(0, 0, Renderer.unitsWide, unitsTall);

      Shaders.blackHoleShader.startShader(gl);
      gl.glUniform4f(gl.glGetUniformLocation(Shaders.blackHoleShader.getProgramId(), "params"), bhX, bhY, bhSize, Renderer.unitsWide);
      ScreenArea.draw(bhX, bhY, bhSize, bhSize);
      Shaders.blackHoleShader.stopShader(gl);

      //draw UI
      screen.render();
    }

    private static boolean lockContext() {
      if (Renderer.getWindow().getContext() == null) {
        return false;
      }
      GLContext context = Renderer.getWindow().getContext();
      if (!context.isCurrent()) {
        context.makeCurrent();
      }
      return true;
    }

    private static void releaseContext() {
      if (Renderer.getWindow().getContext() == null) {
        return;
      }
      GLContext context = Renderer.getWindow().getContext();
      if (context.isCurrent()) {
        context.release();
      }
    }

    public static void mouseScrolled(Point location, float amount) {
      zoomWorld(location, amount);
      if (clickHandler == null || !lockContext()) {
        return;
      }
      float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
      float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
      float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
      clickHandler.processScrollOver(unitX, unitY, amount);
      releaseContext();
    }

    private static void zoomWorld(Point location, float deltaZoom) {
      if (camera == null || clickHandler.isMouseOnElement()) {
        return;
      }
      camera.adjustZoom(deltaZoom);
    }

    public static void mousePressed(Point location) {
      startLoc = MouseInfo.getPointerInfo().getLocation();
      mouseIsPressed = true;
      if (clickHandler == null || !lockContext()) {
        return;
      }
      float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
      float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
      float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
      clickHandler.processMouseDown(unitX, unitY, false);
      releaseContext();
    }

    public static void mouseReleased(Point location) {
      mouseIsPressed = false;
      if (clickHandler == null || !lockContext()) {
        return;
      }
      float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
      float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
      float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
      clickHandler.processClick(unitX, unitY);
      clickHandler.processMouseDown(unitX, unitY, true);
      releaseContext();
    }

    public static void mouseMoved(Point location) {
      if (clickHandler == null || !lockContext()) {
        return;
      }
      float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
      float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
      float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
      clickHandler.processMouseMove(unitX, unitY);
      releaseContext();
    }

    public static void mouseDragged(Point location) {
      if (clickHandler == null || !lockContext()) {
        return;
      }
      float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
      float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
      float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
      clickHandler.processMouseDragged(unitX, unitY);
      releaseContext();
    }

    public static void keyPressed(Key key) {
      if (keyboardHandler == null || !lockContext()) {
        return;
      }
      keyboardHandler.keyPressed(key);
      releaseContext();
    }

    public static void keyReleased(Key key) {
      if (keyboardHandler == null || !lockContext()) {
        return;
      }
      keyboardHandler.keyReleased(key);
      releaseContext();
    }
}
