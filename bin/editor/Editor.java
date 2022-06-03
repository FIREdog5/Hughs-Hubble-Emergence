package bin.editor;

import bin.world.Planet;
import bin.graphics.Renderer;
import bin.resource.palette.EditorPalette;
import bin.resource.palette.Palette;
import bin.resource.ImageResources;
import bin.input.ClickHandler;
import bin.graphics.Color;
import bin.graphics.Shaders;
import bin.ClientMain;
import bin.Wrapper;
import bin.resource.ImageResource;
import bin.resource.GradientGenerator;
import bin.resource.ColorPosition;

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

import bin.graphics.ui.complex.UIToolTip;
import bin.graphics.ui.complex.UIModal;
import bin.graphics.ui.complex.UIVerticalValueSlider;
import bin.graphics.ui.colorUtils.UIColorWheel;
import bin.graphics.ui.colorUtils.UIColorSlider;

import java.awt.image.BufferedImage;
import com.jogamp.opengl.GL2;
import java.awt.MouseInfo;
import java.awt.Point;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Editor extends Thread{

  private boolean running;
  private static EditorWorld world;
  private static ClickHandler clickHandler;
  private static UIScreen screen;
  private static String view = "globe";
  private static Wrapper<ImageResource> palettResourceWrapper = new Wrapper<ImageResource>();

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
    makeUI();

    world = new EditorWorld();
    Palette editorPalette = new EditorPalette(palettResourceWrapper,
                                               new Color("#03fcf8"), //atmosphere lower
                                               new Color("#0000ff", .3f) //atmosphere upper
                                               );
    world.addWorldObject(new Planet(0, 0, editorPalette));
    world.addWorldObject(new Planet(20, 20, editorPalette));
    world.addWorldObject(new Planet(-20, 20, editorPalette));
    world.addWorldObject(new Planet(-20, -20, editorPalette));
    world.addWorldObject(new Planet(20, -20, editorPalette));

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
      Image.draw(ImageResources.generationTest, 0, 0, world.camera.scaleToZoom(96), world.camera.scaleToZoom(32));

      Shaders.terrainShader.stopShader(gl);

    }
    screen.render();
  }

  private static void makeUI() {

    makeNavButtons();
    makeSidePaletteBar();

    // color modal test button

    Color pickedColor = new Color("#ffffff");

    Color pickedColorLowS = new Color(pickedColor) {
      @Override
      public void transformRef() {
        super.transformRef();
        float[] hsv = this.refColor.getHSV();
        hsv[1] = Math.max(hsv[1] - .5f, 0f);
        this.setHSV(hsv);
      }
    };

    UIButton colorModalTestButton = new UIButton(screen) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openColorModal(pickedColor);
      }
    };
    colorModalTestButton.minHeight = 3f;
    colorModalTestButton.minWidth = 3f;
    colorModalTestButton.color = pickedColor;
    colorModalTestButton.mouseOverColor = pickedColorLowS;

    screen.addChild(colorModalTestButton);
    clickHandler.register(colorModalTestButton);
  }

  private static void addDefaultGradientSliders(UIElement parent, ArrayList<ColorPosition> gradientPositions, ArrayList<ColorPosition> solidPositions, BufferedImage gradientTestBuffer) {
    ColorPosition newPosition;

    newPosition = new ColorPosition(new Color("#004CFF"), 1, 255);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    newPosition = new ColorPosition(new Color("#00FFFF"), 1, 213);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    newPosition = new ColorPosition(new Color("#FFAF00"), 1, 212);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    newPosition = new ColorPosition(new Color("#31A500"), 1, 16);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    newPosition = new ColorPosition(new Color("#FFFFFF"), 1, 15);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    newPosition = new ColorPosition(new Color("#86FFFF"), 1, 1);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    newPosition = new ColorPosition(new Color("#FFFFFF"), 1, 0);
    gradientPositions.add(newPosition);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    addGradientSlider(parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

    GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
  }

  private static void addGradientSlider(UIElement parent, ArrayList<ColorPosition> gradientPositions, ArrayList<ColorPosition> solidPositions, ColorPosition newPosition, BufferedImage gradientTestBuffer) {
    UIVerticalValueSlider slider = new UIVerticalValueSlider(parent){

      private int oldPos = 0;

      @Override
      public float getValue() {
        this.oldPos = newPosition.position;
        return newPosition.position;
      }

      @Override
      public float getChildY(int i) {
        if (i >= this.children.size() || i < 0) {
          throw new IndexOutOfBoundsException("UIVerticalValueSlider does not contain " + i + " children.");
        }
        return this.valueToY(this.getValue()) + this.getChildHeight() / 2 - this.valueToYRelative(.5f);
      }

      protected float valueToYRelative(float value) {
        return this.getHeight() * (value - this.minValue) / (this.maxValue - this.minValue);
      }

      @Override
      public void render() {
        if (!this.noBackground) {
          Global.drawColor(this.getColor());
          Pointer.draw(this.getX() + this.getWidth() - this.maxWidth / 2, this.valueToY(this.getValue()) - this.valueToYRelative(.5f), this.maxWidth, this.getChildHeight(), this.facing);
        }
        if (this.outlineWeight > 0f) {
          Global.drawColor(this.getOutlineColor());
          PointerOutline.draw(this.getX() + this.getWidth() - this.maxWidth / 2, this.valueToY(this.getValue()) - this.valueToYRelative(.5f), this.maxWidth, this.getChildHeight(), this.facing, this.outlineWeight);
        }
        for (int i = 0; i < this.children.size(); i++) {
          this.children.get(i).render();
        }
      }

      @Override
      public void setValue(float value) {
        boolean taken = false;
        int correctedValue = (int)(Math.min(value, 255));
        for (ColorPosition colorPos : gradientPositions) {
          if (correctedValue == colorPos.position) {
            taken = true;
          }
        }
        if (taken) {
          if (correctedValue == 0) {
            Comparator<ColorPosition> comparator = (ColorPosition o1, ColorPosition o2) -> o1.position - o2.position;
            Collections.sort(gradientPositions, comparator);
            int pos = 0;
            for (ColorPosition colorPos : gradientPositions) {
              if (colorPos.equals(newPosition)) {
                break;
              }
              if (colorPos.position != pos) {
                break;
              }
              pos++;
            }
            newPosition.position = pos;
          } else if (correctedValue == 255) {
            Comparator<ColorPosition> comparator = (ColorPosition o1, ColorPosition o2) -> o2.position - o1.position;
            Collections.sort(gradientPositions, comparator);
            int pos = 255;
            for (ColorPosition colorPos : gradientPositions) {
              if (colorPos.equals(newPosition)) {
                break;
              }
              if (colorPos.position != pos) {
                break;
              }
              pos--;
            }
            newPosition.position = pos;
          } else {
            newPosition.position = (int) this.oldPos;
          }
        } else {
          newPosition.position = correctedValue;
        }
        GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
        palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
      }
    };

    slider.minValue = 0;
    slider.maxValue = 256;
    slider.minHeight = .5f;
    slider.maxWidth = 1;
    slider.minWidth = 0;
    slider.maxHeight = 50;
    slider.color = new Color("#000000");
    slider.mouseOverColor = new Color("#777777");
    slider.outlineColor = new Color("#ffffff");
    slider.outlineWeight = .1f;

    parent.addChild(slider);
    clickHandler.register(slider);

    Color newColorLowS = new Color(newPosition.color) {
      @Override
      public void transformRef() {
        super.transformRef();
        float[] hsv = this.refColor.getHSV();
        hsv[1] = Math.max(hsv[1] - .5f, 0f);
        this.setHSV(hsv);
      }
    };

    UIButton colorButton = new UIButton(slider) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openColorModal(newPosition.color, () -> {
          GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
          palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
        });
      }
    };
    colorButton.minHeight = .5f;
    colorButton.minWidth = .5f;
    colorButton.color = newPosition.color;
    colorButton.mouseOverColor = newColorLowS;

    slider.addChild(colorButton);
    clickHandler.register(colorButton);
  }

  private static void makeSidePaletteBar() {
    BufferedImage gradientTestBuffer = GradientGenerator.newGradient(1);
    palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
    ArrayList<ColorPosition> gradientPositions = new ArrayList<ColorPosition>();
    ArrayList<ColorPosition> solidPositions = new ArrayList<ColorPosition>();

    UICenter rightCenterer = new UICenter(screen);
    screen.addChild(rightCenterer);

    UIRightPositioner rightPositioner = new UIRightPositioner(rightCenterer);
    rightPositioner.x = .5f;
    rightCenterer.addChild(rightPositioner);

    UIBoxCol rightContainer = new UIBoxCol(rightPositioner);
    rightContainer.color = new Color("#000000");
    rightContainer.outlineColor = new Color("#ffffff");
    rightContainer.outlineWeight = .1f;
    rightContainer.radius = .5f;
    rightPositioner.addChild(rightContainer);

    //TODO remove these eventually
    rightContainer.minWidth = 15f;
    rightContainer.minHeight = 40f;

    UIBoxRow buttonsRow = new UIBoxRow(rightContainer);
    buttonsRow.outlineWeight = 0;
    buttonsRow.noBackground = true;
    rightContainer.addChild(buttonsRow);

    UIBoxRow contentRow = new UIBoxRow(rightContainer);
    contentRow.outlineWeight = 0;
    contentRow.noBackground = true;
    contentRow.padding = .5f;
    rightContainer.addChild(contentRow);

    UIBoxLayered sliderContainer = new UIBoxLayered(contentRow);
    sliderContainer.outlineWeight = 0;
    sliderContainer.noBackground = true;
    sliderContainer.minWidth = 1f;
    contentRow.addChild(sliderContainer);

    UIButton addNewSliderButton = new UIButton(buttonsRow) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        if (gradientPositions.size() == 256) {
          return;
        }
        Comparator<ColorPosition> comparator = (ColorPosition o1, ColorPosition o2) -> o1.position - o2.position;
        Collections.sort(gradientPositions, comparator);
        int pos = 0;
        for (ColorPosition colorPos : gradientPositions) {
          if (colorPos.position != pos) {
            break;
          }
          pos++;
        }
        ColorPosition newPosition = new ColorPosition(Color.randomColor(), 1, pos);
        gradientPositions.add(newPosition);
        GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
        palettResourceWrapper.set(new ImageResource(gradientTestBuffer));

        addGradientSlider(sliderContainer, gradientPositions, solidPositions, newPosition, gradientTestBuffer);

      }
    };
    addNewSliderButton.mouseOverColor = new Color("#00aa00");
    addNewSliderButton.outlineWeight = .2f;
    addNewSliderButton.padding = .5f;
    addNewSliderButton.y = .8f;
    buttonsRow.addChild(addNewSliderButton);
    clickHandler.register(addNewSliderButton);

    addDefaultGradientSliders(sliderContainer, gradientPositions, solidPositions, gradientTestBuffer);

    UIImage addIcon = new UIImage(addNewSliderButton, ImageResources.addIcon);
    addIcon.minWidth = 1f;
    addIcon.minHeight = 1f;
    addIcon.padding = .25f;
    addNewSliderButton.addChild(addIcon);

    UIImage gradientPreview = new UIImage(contentRow, palettResourceWrapper);
    gradientPreview.minWidth = 1f;
    gradientPreview.minHeight = 50f;
    gradientPreview.useAA = false;
    contentRow.addChild(gradientPreview);

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

  private static void openColorModal(Color colorPointer, Runnable afterClose) {
    Color newColor = new Color("#ffffff");
    float[] hsv = colorPointer.getHSV();
    newColor.setHSV(hsv);
    UIModal colorModal = new UIModal(screen);
    colorModal.centerBox.color = new Color("#000000");
    colorModal.centerBox.outlineColor = new Color("#ffffff");
    colorModal.centerBox.outlineWeight = .1f;
    colorModal.centerBox.radius = .5f;
    screen.addChild(colorModal);
    clickHandler.setMask(colorModal.centerBox);

    //color wheel

    UICenter colorSettersCenterer = new UICenter(colorModal.centerBox);
    colorSettersCenterer.centerY = false;
    colorModal.addChild(colorSettersCenterer);

    UIBoxRow colorSettersRow = new UIBoxRow(colorSettersCenterer);
    colorSettersRow.outlineWeight = 0;
    colorSettersRow.noBackground = true;
    colorSettersCenterer.addChild(colorSettersRow);

    UICenter colorWheelCenterer = new UICenter(colorSettersRow);
    colorWheelCenterer.centerX = false;
    colorWheelCenterer.padding = .5f;
    colorSettersRow.addChild(colorWheelCenterer);

    UIBoxCol colorWheelCol = new UIBoxCol(colorWheelCenterer);
    colorWheelCol.outlineWeight = 0;
    colorWheelCol.noBackground = true;
    colorWheelCenterer.addChild(colorWheelCol);

    UIColorWheel colorWheel = new UIColorWheel(colorWheelCol, newColor);
    colorWheel.circleRadius = 5f;
    colorWheelCol.addChild(colorWheel);
    clickHandler.register(colorWheel);

    UIBoxCol sliderCol = new UIBoxCol(colorSettersRow);
    sliderCol.noBackground = true;
    sliderCol.outlineWeight = 0;
    sliderCol.padding = .5f;
    colorSettersRow.addChild(sliderCol);

    UIBoxRow rgbSliderRow = new UIBoxRow(sliderCol);
    rgbSliderRow.noBackground = true;
    rgbSliderRow.outlineWeight = 0;
    sliderCol.addChild(rgbSliderRow);

    addColorSlider(rgbSliderRow, newColor, "red", "R");
    addColorSlider(rgbSliderRow, newColor, "green", "G");
    addColorSlider(rgbSliderRow, newColor, "blue", "B");

    UIBoxRow hsvSliderPadding = new UIBoxRow(sliderCol);
    hsvSliderPadding.noBackground = true;
    hsvSliderPadding.outlineWeight = 0;
    hsvSliderPadding.minHeight = .5f;
    sliderCol.addChild(hsvSliderPadding);

    UIBoxRow hsvSliderRow = new UIBoxRow(sliderCol);
    hsvSliderRow.noBackground = true;
    hsvSliderRow.outlineWeight = 0;
    sliderCol.addChild(hsvSliderRow);

    addColorSlider(hsvSliderRow, newColor, "hue", "H");
    addColorSlider(hsvSliderRow, newColor, "saturation", "S");
    addColorSlider(hsvSliderRow, newColor, "value", "V");

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
        afterClose.run();
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

  private static void openColorModal(Color colorPointer) {
    openColorModal(colorPointer, () -> {});
  }

  private static void addColorSlider(UIElement parent, Color newColor, String val, String name) {

    UIBoxCol sliderCol = new UIBoxCol(parent);
    sliderCol.outlineWeight = 0;
    sliderCol.noBackground = true;
    parent.addChild(sliderCol);

    UICenter textCenterer = new UICenter(sliderCol);
    textCenterer.centerY = false;
    sliderCol.addChild(textCenterer);

    UIBoxRow namePlateRow = new UIBoxRow(textCenterer);
    namePlateRow.outlineWeight = 0;
    namePlateRow.noBackground = true;
    textCenterer.addChild(namePlateRow);

    UIBoxRow manualPadding = new UIBoxRow(namePlateRow);
    manualPadding.outlineWeight = 0;
    manualPadding.noBackground = true;
    manualPadding.minWidth = 1;
    namePlateRow.addChild(manualPadding);

    UITextBlock silderText = new UITextBlock(namePlateRow, name, .5f);
    silderText.textColor = new Color("#ffffff");
    namePlateRow.addChild(silderText);

    UIColorSlider newSlider = new UIColorSlider(sliderCol, newColor, clickHandler, val);
    newSlider.slider.color = new Color("#000000");
    newSlider.slider.mouseOverColor = new Color("#777777");
    newSlider.slider.outlineColor = new Color("#ffffff");
    newSlider.slider.outlineWeight = .1f;
    newSlider.padding = .3f;
    sliderCol.addChild(newSlider);
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
    zoomWorld(location, amount);
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

}
