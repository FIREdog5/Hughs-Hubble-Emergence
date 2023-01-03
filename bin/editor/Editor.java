package bin.editor;

import bin.world.Planet;
import bin.graphics.Renderer;
import bin.resource.palette.EditorPalette;
import bin.resource.palette.Palette;
import bin.resource.ImageResources;
import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.input.Key;
import bin.graphics.Color;
import bin.graphics.Shaders;
import bin.ClientMain;
import bin.Wrapper;
import bin.resource.ImageResource;
import bin.resource.GradientGenerator;
import bin.resource.ImageGenerator;
import bin.resource.ColorPosition;
import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.modifiers.ImageCreateModifier;

import bin.graphics.objects.Image;
import bin.graphics.objects.Global;
import bin.graphics.objects.Pointer;
import bin.graphics.objects.PointerOutline;

import bin.graphics.ui.UIScreen;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIImage;
import bin.graphics.ui.UITextBlock;
import bin.graphics.ui.UIElement;
import bin.graphics.ui.UIRightPositioner;
import bin.graphics.ui.UIBoxLayered;
import bin.graphics.ui.UISelectable;

import bin.graphics.ui.complex.UIToolTip;
import bin.graphics.ui.complex.UIModal;
import bin.graphics.ui.complex.UIVerticalValueSlider;
import bin.graphics.ui.complex.UISelectionMenu;
import bin.graphics.ui.complex.UINumberInput;
import bin.graphics.ui.complex.UIScrollableBox;
import bin.graphics.ui.colorUtils.UIColorWheel;
import bin.graphics.ui.colorUtils.UIColorSlider;
import bin.graphics.ui.colorUtils.UIGlobeDisplay;

import java.awt.image.BufferedImage;
import com.jogamp.opengl.GL2;
import java.awt.MouseInfo;
import java.awt.Point;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.jogamp.newt.event.KeyEvent;
import java.util.function.Consumer;

public class Editor extends Thread{

  private boolean running;
  private static EditorWorld world;
  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;
  private static String view = "globe";
  private static Wrapper<ImageResource> palettResourceWrapper = new Wrapper<ImageResource>();
  private static Wrapper<ImageResource> heightMapWrapper = new Wrapper<ImageResource>();
  private static Palette editorPalette;

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
    keyboardHandler = new KeyboardHandler();
    screen = new UIScreen();
    makeUI();

    world = new EditorWorld();
    editorPalette = new EditorPalette(palettResourceWrapper,
                                      new Color("#03fcf8"), //atmosphere lower
                                      new Color("#0000ff", .3f) //atmosphere upper
                                      );
    world.addWorldObject(new Planet(0, 0, editorPalette, heightMapWrapper));
    world.addWorldObject(new Planet(20, 20, editorPalette, heightMapWrapper));
    world.addWorldObject(new Planet(-20, 20, editorPalette, heightMapWrapper));
    world.addWorldObject(new Planet(-20, -20, editorPalette, heightMapWrapper));
    world.addWorldObject(new Planet(20, -20, editorPalette, heightMapWrapper));

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
      gl.glBindTexture(GL2.GL_TEXTURE_2D, palettResourceWrapper.get().getTexture().getTextureObject());
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glActiveTexture(GL2.GL_TEXTURE0+2);
      gl.glBindTexture(GL2.GL_TEXTURE_2D, ImageResources.generationTest2.getTexture().getTextureObject());
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
      gl.glActiveTexture(GL2.GL_TEXTURE0);

      Global.drawColor(new Color("#ffffff"));
      Image.draw(ImageResources.generationTest, 0, 0, world.camera.scaleToZoom(64), world.camera.scaleToZoom(32));

      Shaders.terrainShader.stopShader(gl);

    }
    screen.render();
  }

  private static void makeUI() {
    PaletteBar.init(clickHandler, keyboardHandler, screen, palettResourceWrapper);
    ColorModal.init(clickHandler, keyboardHandler, screen, palettResourceWrapper);
    NoiseModal.init(clickHandler, keyboardHandler, screen, palettResourceWrapper);
    makeNavButtons();
    PaletteBar.makeSidePaletteBar();
    makeSideNoiseBar();

  }

  private static void recalculateNoise(ArrayList<NoiseStepData> noiseStepList) {
    for (int i = 0; i < noiseStepList.size(); i++) {
      if (i == 0) {
        BufferedImage newImage = new BufferedImage(2400, 800, BufferedImage.TYPE_INT_RGB);
        noiseStepList.get(i).noisePreviewWrapper.set(new ImageResource(noiseStepList.get(i).modifier.resolve(newImage), 1, 1000));
      } else {
        noiseStepList.get(i).noisePreviewWrapper.set(new ImageResource(noiseStepList.get(i).modifier.resolve(noiseStepList.get(i-1).noisePreviewWrapper.get().getBufferedImage()), 1, 1000));
      }
    }
    heightMapWrapper.set(noiseStepList.get(noiseStepList.size() - 1).noisePreviewWrapper.get());
  }

  private static void addFixedNoiseStep(UIScrollableBox scrollBox, ArrayList<NoiseStepData> noiseStepList) {

    NoiseStepData noiseStepData = new NoiseStepData();
    noiseStepData.fnl = new FastNoiseLite();
    noiseStepData.modifier = new ImageCreateModifier(1L ,noiseStepData.fnl);

    noiseStepList.add(noiseStepData);

    recalculateNoise(noiseStepList);

    UIBoxRow stepRow = new UIBoxRow(scrollBox);
    stepRow.minWidth = 18f;
    stepRow.minHeight = 6f;
    stepRow.padding = .5f;
    stepRow.color = new Color("#000000");
    stepRow.outlineColor = new Color("#ffffff");
    stepRow.outlineWeight = .1f;

    UIBoxCol orderCol = new UIBoxCol(stepRow);
    orderCol.outlineWeight = .1f;
    orderCol.color = new Color("#000000");
    orderCol.outlineColor = new Color("#ffffff");
    orderCol.minWidth = 2f;
    orderCol.minHeight = 6f;
    stepRow.addChild(orderCol);

    UIBoxCol infoCol = new UIBoxCol(stepRow);
    infoCol.outlineWeight = 0;
    infoCol.noBackground = true;
    stepRow.addChild(infoCol);

    UIBoxCol infoColPadding = new UIBoxCol(infoCol);
    infoColPadding.outlineWeight = 0;
    infoColPadding.noBackground = true;
    infoColPadding.minHeight = .1f;
    infoCol.addChild(infoColPadding);

    UIBoxCol nameBox = new UIBoxCol(infoCol);
    nameBox.outlineWeight = .1f;
    nameBox.padding = .5f;
    nameBox.minWidth = 10;
    nameBox.minHeight = 1.5f;
    nameBox.margin = .1f;
    infoCol.addChild(nameBox);

    UICenter nameCenter = new UICenter(nameBox);
    nameCenter.centerY = true;
    nameCenter.centerX = false;
    nameBox.addChild(nameCenter);

    UIBoxRow nameContents = new UIBoxRow(nameCenter);
    nameContents.noBackground = true;
    nameContents.outlineWeight = 0f;
    nameCenter.addChild(nameContents);

    UIBoxRow namePadding = new UIBoxRow(nameContents);
    namePadding.minWidth = .1f;
    namePadding.noBackground = true;
    namePadding.outlineWeight = 0f;
    nameContents.addChild(namePadding);

    UITextBlock name = new UITextBlock(nameContents, "Base Noise", .5f);
    name.textColor = new Color("#ffffff");
    name.noBackground = false;
    nameContents.addChild(name);

    UIButton noisePreviewButton = new UIButton(stepRow) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        NoiseModal.openNoiseModal(new FastNoiseLite(noiseStepData.fnl), (FastNoiseLite newFnl) -> {
                                                                                                   noiseStepData.fnl.Set(newFnl);
                                                                                                   recalculateNoise(noiseStepList);
                                                                                                  });
      }
    };
    noisePreviewButton.mouseOverColor = new Color("#ffffff");
    noisePreviewButton.color = new Color("#ffffff");
    noisePreviewButton.minWidth = 6f;
    noisePreviewButton.minHeight = 6f;
    noisePreviewButton.radius = 3f;
    stepRow.addChild(noisePreviewButton);
    clickHandler.register(noisePreviewButton);

    UIGlobeDisplay grayGlobe = new UIGlobeDisplay(noisePreviewButton, noiseStepData.noisePreviewWrapper, null);
    grayGlobe.setRadius(5.8f);
    grayGlobe.padding = .1f;
    noisePreviewButton.addChild(grayGlobe);

    scrollBox.addChild(stepRow);
  }

  private static void addNoiseStep(UIScrollableBox scrollBox, ArrayList<NoiseStepData> noiseStepList) {

    NoiseStepData noiseStepData = new NoiseStepData();
    noiseStepData.fnl = new FastNoiseLite();
    noiseStepData.modifier = new ImageCreateModifier(1L ,noiseStepData.fnl);

    noiseStepList.add(noiseStepData);

    recalculateNoise(noiseStepList);

    UIBoxRow stepRow = new UIBoxRow(scrollBox);
    stepRow.minWidth = 18f;
    stepRow.minHeight = 6f;
    stepRow.padding = .5f;
    stepRow.color = new Color("#000000");
    stepRow.outlineColor = new Color("#ffffff");
    stepRow.outlineWeight = .1f;

    UIBoxCol orderCol = new UIBoxCol(stepRow);
    orderCol.outlineWeight = .1f;
    orderCol.color = new Color("#000000");
    orderCol.outlineColor = new Color("#ffffff");
    orderCol.minWidth = 2f;
    orderCol.minHeight = 6f;
    stepRow.addChild(orderCol);

    UIBoxCol infoCol = new UIBoxCol(stepRow);
    infoCol.outlineWeight = 0;
    infoCol.noBackground = true;
    stepRow.addChild(infoCol);

    UIBoxCol infoColPadding = new UIBoxCol(infoCol);
    infoColPadding.outlineWeight = 0;
    infoColPadding.noBackground = true;
    infoColPadding.minHeight = .1f;
    infoCol.addChild(infoColPadding);

    UIBoxCol nameBox = new UIBoxCol(infoCol);
    nameBox.outlineWeight = .1f;
    nameBox.padding = .5f;
    nameBox.minWidth = 10;
    nameBox.minHeight = 1.5f;
    nameBox.margin = .1f;
    infoCol.addChild(nameBox);

    UICenter nameCenter = new UICenter(nameBox);
    nameCenter.centerY = true;
    nameCenter.centerX = false;
    nameBox.addChild(nameCenter);

    UIBoxRow nameContents = new UIBoxRow(nameCenter);
    nameContents.noBackground = true;
    nameContents.outlineWeight = 0f;
    nameCenter.addChild(nameContents);

    UIBoxRow namePadding = new UIBoxRow(nameContents);
    namePadding.minWidth = .1f;
    namePadding.noBackground = true;
    namePadding.outlineWeight = 0f;
    nameContents.addChild(namePadding);

    UITextBlock name = new UITextBlock(nameContents, "Base Noise", .5f);
    name.textColor = new Color("#ffffff");
    name.noBackground = false;
    nameContents.addChild(name);

    UIButton noisePreviewButton = new UIButton(stepRow) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        NoiseModal.openNoiseModal(new FastNoiseLite(noiseStepData.fnl), (FastNoiseLite newFnl) -> {
                                                                                                   noiseStepData.fnl.Set(newFnl);
                                                                                                   recalculateNoise(noiseStepList);
                                                                                                  });
      }
    };
    noisePreviewButton.mouseOverColor = new Color("#ffffff");
    noisePreviewButton.color = new Color("#ffffff");
    noisePreviewButton.minWidth = 6f;
    noisePreviewButton.minHeight = 6f;
    noisePreviewButton.radius = 3f;
    stepRow.addChild(noisePreviewButton);
    clickHandler.register(noisePreviewButton);

    UIGlobeDisplay grayGlobe = new UIGlobeDisplay(noisePreviewButton, noiseStepData.noisePreviewWrapper, null);
    grayGlobe.setRadius(5.8f);
    grayGlobe.padding = .1f;
    noisePreviewButton.addChild(grayGlobe);

    scrollBox.addChild(stepRow);
  }

  public static void makeSideNoiseBar() {

    ArrayList<NoiseStepData> noiseStepList = new ArrayList<NoiseStepData>();

    UICenter leftCenterer = new UICenter(screen);
    leftCenterer.centerX = false;
    leftCenterer.centerY = true;
    screen.addChild(leftCenterer);

    UIBoxCol leftContainer = new UIBoxCol(leftCenterer);
    leftContainer.color = new Color("#000000");
    leftContainer.outlineColor = new Color("#ffffff");
    leftContainer.outlineWeight = .1f;
    leftContainer.radius = .5f;
    leftCenterer.addChild(leftContainer);

    UIBoxRow buttonsRow = new UIBoxRow(leftContainer);
    buttonsRow.outlineWeight = 0;
    buttonsRow.noBackground = true;
    buttonsRow.padding = .5f;
    leftContainer.addChild(buttonsRow);

    UIScrollableBox scrollableContent = new UIScrollableBox(leftContainer);
    scrollableContent.color = new Color("#000000");
    scrollableContent.outlineColor = new Color("#ffffff");
    scrollableContent.outlineWeight = .1f;
    scrollableContent.scrollbarColor = new Color("#ffffff");
    scrollableContent.scrollbarMouseOverColor = new Color("#dddddd");
    scrollableContent.scrollbarWidth = .5f;
    scrollableContent.padding = .5f;

    //TODO remove these eventually
    scrollableContent.maxHeight = 38f;

    leftContainer.addChild(scrollableContent);
    clickHandler.register(scrollableContent);

    UIButton addNewNoiseStepButton = new UIButton(buttonsRow) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
      }
    };
    addNewNoiseStepButton.mouseOverColor = new Color("#00aa00");
    addNewNoiseStepButton.outlineWeight = .1f;
    buttonsRow.addChild(addNewNoiseStepButton);
    clickHandler.register(addNewNoiseStepButton);

    UIImage addIcon = new UIImage(addNewNoiseStepButton, ImageResources.addIcon);
    addIcon.minWidth = 1f;
    addIcon.minHeight = 1f;
    addIcon.padding = .25f;
    addNewNoiseStepButton.addChild(addIcon);


    addFixedNoiseStep(scrollableContent, noiseStepList);

  }

  private static void makeNavButtons() {
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
    if (clickHandler == null || !lockContext()) {
      return;
    }
    if (!clickHandler.hasMask()) {
      zoomWorld(location, amount);
    }
    float unitX = (location.x / (float) Renderer.getWindowWidth() - .5f) * (float) Renderer.unitsWide;
    float unitsTall = Renderer.getWindowHeight() / (Renderer.getWindowWidth() / Renderer.unitsWide);
    float unitY = (location.y / (float) Renderer.getWindowHeight() - .5f) * -unitsTall;
    clickHandler.processScrollOver(unitX, unitY, amount);
    releaseContext();
  }

  private static void zoomWorld(Point location, float deltaZoom) {
    if (world.camera == null) {
      return;
    }
    world. camera.adjustZoom(deltaZoom);
  }

  public static void mousePressed(Point location) {
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
