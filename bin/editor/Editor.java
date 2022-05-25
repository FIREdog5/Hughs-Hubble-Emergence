package bin.editor;

import bin.world.Planet;
import bin.graphics.Renderer;
import bin.resource.Palettes;
import bin.resource.ImageResources;
import bin.input.ClickHandler;
import bin.graphics.Color;
import bin.graphics.Shaders;
import bin.ClientMain;

import bin.graphics.objects.Image;
import bin.graphics.objects.Global;

import bin.graphics.ui.UIScreen;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIImage;
import bin.graphics.ui.UITextBlock;

import bin.graphics.ui.complex.UIToolTip;
import bin.graphics.ui.complex.UIModal;
import bin.graphics.ui.complex.UIColorWheel;

import com.jogamp.opengl.GL2;
import java.awt.MouseInfo;
import java.awt.Point;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;

public class Editor extends Thread{

  private boolean running;
  private static EditorWorld world;
  private static ClickHandler clickHandler;
  private static UIScreen screen;
  private static String view = "globe";

  public static void init() {
    (new Editor()).start();
  }

  public Editor () {
    this.setName("Editor");
    this.running = true;
  }

  public void run() {
    this.running = true;

    clickHandler = new ClickHandler();
    screen = new UIScreen();

    // top bar menu in UI
    // move to its own function..

    UICenter topBarCenterer = new UICenter(screen);
    topBarCenterer.centerY = false;
    screen.addChild(topBarCenterer);
    topBarCenterer.y = -.5f;

    UIBoxRow topBarContainer = new UIBoxRow(topBarCenterer);
    topBarContainer.color = new Color("#000000");
    topBarContainer.outlineColor = new Color("#ffffff");
    topBarContainer.outlineWeight = .1f;
    topBarContainer.radius = .5f;

    topBarCenterer.addChild(topBarContainer);

    UIButton globeButton = new UIButton(topBarContainer) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        view = "globe";
      }

      @Override
      public void render() {
        this.outlineWeight = view.equals("globe") ? .2f : .1f;
        super.render();
      }
    };
    globeButton.mouseOverColor = new Color("#777777");
    globeButton.outlineWeight = .2f;
    globeButton.padding = .5f;
    globeButton.y = .8f;
    topBarContainer.addChild(globeButton);
    clickHandler.register(globeButton);

    UIImage globeIcon = new UIImage(globeButton, ImageResources.globeIcon);
    globeIcon.minWidth = 2.5f;
    globeIcon.minHeight = 2.5f;
    globeIcon.padding = .25f;
    globeButton.addChild(globeIcon);

    UIButton mapButton = new UIButton(topBarContainer) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        view = "map";
      }

      @Override
      public void render() {
        this.outlineWeight = view.equals("map") ? .2f : .1f;
        super.render();
      }
    };
    mapButton.mouseOverColor = new Color("#777777");
    mapButton.outlineWeight = .1f;
    mapButton.padding = .5f;
    mapButton.y = .8f;
    topBarContainer.addChild(mapButton);
    clickHandler.register(mapButton);

    UIImage mapIcon = new UIImage(mapButton, ImageResources.mapIcon);
    mapIcon.minWidth = 2.5f;
    mapIcon.minHeight = 2.5f;
    mapIcon.padding = .25f;
    mapButton.addChild(mapIcon);

    UIToolTip globeToolTip = new UIToolTip(screen);
    globeToolTip.outlineWeight = .1f;
    globeToolTip.color = new Color("#000000");
    globeToolTip.outlineColor = new Color("#ffffff");
    globeToolTip.offset = .1f;
    globeToolTip.margin = .3f;
    globeToolTip.anchor = globeButton;

    screen.addChild(globeToolTip);

    UITextBlock globeToolTipText = new UITextBlock(globeToolTip, "globe view", .5f);
    globeToolTipText.textColor = new Color("#ffffff");

    globeToolTip.addChild(globeToolTipText);

    UIToolTip mapToolTip = new UIToolTip(screen);
    mapToolTip.outlineWeight = .1f;
    mapToolTip.color = new Color("#000000");
    mapToolTip.outlineColor = new Color("#ffffff");
    mapToolTip.offset = .1f;
    mapToolTip.margin = .3f;
    mapToolTip.anchor = mapButton;

    screen.addChild(mapToolTip);

    UITextBlock mapToolTipText = new UITextBlock(mapToolTip, "map view", .5f);
    mapToolTipText.textColor = new Color("#ffffff");

    mapToolTip.addChild(mapToolTipText);

    // color modal test button

    Color pickedColor = new Color("#ffffff");

    UIBoxCol colorModalTestButtonBackground = new UIBoxCol(screen);
    colorModalTestButtonBackground.minHeight = 3f;
    colorModalTestButtonBackground.minWidth = 3f;
    colorModalTestButtonBackground.color = pickedColor;

    screen.addChild(colorModalTestButtonBackground);

    UIButton colorModalTestButton = new UIButton(screen) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openColorModal(pickedColor);
      }
    };
    colorModalTestButton.minHeight = 3f;
    colorModalTestButton.minWidth = 3f;
    colorModalTestButton.color = new Color("#000000", 0);
    colorModalTestButton.mouseOverColor = new Color("#000000", .3f);

    screen.addChild(colorModalTestButton);
    clickHandler.register(colorModalTestButton);


    world = new EditorWorld();
    world.addWorldObject(new Planet(0, 0, Palettes.earth1));
    world.addWorldObject(new Planet(20, 20, Palettes.earth1));
    world.addWorldObject(new Planet(-20, 20, Palettes.earth1));
    world.addWorldObject(new Planet(-20, -20, Palettes.earth1));
    world.addWorldObject(new Planet(20, -20, Palettes.earth1));

    System.out.println(EditorUtils.openResource());

    while (this.running) {
      Renderer.render();
    }
  }

  public static void render() {
    if (world == null) {
      return;
    }
    if (view.equals("globe")) {
      world.renderWorld();
    } else if (view.equals("map")) {
      GL2 gl = ClientMain.gl;

      Shaders.terrainShader.startShader(gl);

      gl.glActiveTexture(GL2.GL_TEXTURE0+1);
      gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.biomeShaderTest.getTexture().getTextureObject());
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glActiveTexture(GL2.GL_TEXTURE0+2);
      gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.generationTest2.getTexture().getTextureObject());
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glActiveTexture(GL2.GL_TEXTURE0);

      Global.drawColor(new Color("#ffffff"));
      Image.draw(ImageResources.generationTest, 0, 0, world.camera.scaleToZoom(96), world.camera.scaleToZoom(32));

      Shaders.terrainShader.stopShader(gl);

    }
    screen.render();
  }

  private static void openColorModal(Color colorPointer) {
    Color newColor = new Color("#ffffff");
    newColor.setRGB(colorPointer.getRGB());
    UIModal colorModal = new UIModal(screen);
    colorModal.centerBox.color = new Color("#000000");
    colorModal.centerBox.outlineColor = new Color("#ffffff");
    colorModal.centerBox.outlineWeight = .1f;
    colorModal.centerBox.radius = .5f;
    screen.addChild(colorModal);
    clickHandler.setMask(colorModal.centerBox);

    //placeholder for content

    UICenter colorWheelCenterer = new UICenter(colorModal.centerBox);
    colorWheelCenterer.centerY = false;
    colorModal.addChild(colorWheelCenterer);

    UIBoxRow colorWheelRow = new UIBoxRow(colorWheelCenterer);
    colorWheelRow.outlineWeight = 0;
    colorWheelRow.noBackground = true;
    colorWheelCenterer.addChild(colorWheelRow);

    UIColorWheel colorWheel = new UIColorWheel(colorWheelRow, newColor);
    colorWheel.circleRadius = 2.5f;
    colorWheel.padding = .5f;
    colorWheelRow.addChild(colorWheel);
    clickHandler.register(colorWheel);

    UIBoxCol placeholder = new UIBoxCol(colorModal.centerBox);
    placeholder.minWidth = 10f;
    placeholder.minHeight = 10f;
    placeholder.color = newColor;
    placeholder.padding = .5f;
    colorModal.addChild(placeholder);

    //bottom bar with cancel and confirm buttons

    UICenter bottomBarCenterer = new UICenter(colorModal.centerBox);
    bottomBarCenterer.centerY = false;
    colorModal.addChild(bottomBarCenterer);

    UIBoxRow bottomBar = new UIBoxRow(bottomBarCenterer);
    bottomBar.outlineWeight = 0;
    bottomBar.noBackground = true;
    bottomBarCenterer.addChild(bottomBar);

    UIButton cancelButton = new UIButton(bottomBar){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        colorModal.close();
      }
    };
    cancelButton.color = new Color("#000000");
    cancelButton.outlineColor = new Color("#ffffff");
    cancelButton.outlineWeight = .1f;
    cancelButton.noBackground = false;
    cancelButton.padding = .5f;
    cancelButton.margin = .3f;
    cancelButton.mouseOverColor = new Color("aa0000");
    bottomBar.addChild(cancelButton);
    clickHandler.register(cancelButton);
    UITextBlock cancelButtonText = new UITextBlock(cancelButton, "Cancel", .5f);
    cancelButtonText.textColor = new Color("#ffffff");
    cancelButtonText.noBackground = false;
    cancelButton.addChild(cancelButtonText);

    UIButton confirmButton = new UIButton(bottomBar){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        colorPointer.setRGB(newColor.getRGB());
        clickHandler.clearMask();
        colorModal.close();
      }
    };
    confirmButton.color = new Color("#000000");
    confirmButton.outlineColor = new Color("#ffffff");
    confirmButton.outlineWeight = .1f;
    confirmButton.noBackground = false;
    confirmButton.padding = .5f;
    confirmButton.margin = .3f;
    confirmButton.mouseOverColor = new Color("00aa00");
    bottomBar.addChild(confirmButton);
    clickHandler.register(confirmButton);
    UITextBlock confirmButtonText = new UITextBlock(confirmButton, "Confirm", .5f);
    confirmButtonText.textColor = new Color("#ffffff");
    confirmButtonText.noBackground = false;
    confirmButton.addChild(confirmButtonText);
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

  public static void mouseScrolled(float amount) {
    if (clickHandler == null || !lockContext()) {
      return;
    }
    zoomWorld(amount);
    Point location = MouseInfo.getPointerInfo().getLocation();
    float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
    clickHandler.processScrollOver(unitX, unitY, amount);
    releaseContext();
  }

  private static void zoomWorld(float deltaZoom) {
    if (world.camera == null) {
      return;
    }
    world. camera.adjustZoom(deltaZoom);
  }

  public static void mousePressed() {
    if (clickHandler == null || !lockContext()) {
      return;
    }
    Point location = MouseInfo.getPointerInfo().getLocation();
    float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
    clickHandler.processMouseDown(unitX, unitY, false);
    releaseContext();
  }

  public static void mouseReleased() {
    if (clickHandler == null || !lockContext()) {
      return;
    }
    Point location = MouseInfo.getPointerInfo().getLocation();
    float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
    clickHandler.processClick(unitX, unitY);
    clickHandler.processMouseDown(unitX, unitY, true);
    releaseContext();
  }

  public static void mouseMoved() {
    if (clickHandler == null || !lockContext()) {
      return;
    }
    Point location = MouseInfo.getPointerInfo().getLocation();
    float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
    clickHandler.processMouseMove(unitX, unitY);
    releaseContext();
  }

  public static void mouseDragged() {
    if (clickHandler == null || !lockContext()) {
      return;
    }
    Point location = MouseInfo.getPointerInfo().getLocation();
    float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
    clickHandler.processMouseDragged(unitX, unitY);
    releaseContext();
  }

}
