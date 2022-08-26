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
import com.jogamp.newt.event.KeyEvent;

public class Editor extends Thread{

  private boolean running;
  private static EditorWorld world;
  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;
  private static String view = "globe";
  private static Wrapper<ImageResource> palettResourceWrapper = new Wrapper<ImageResource>();
  private static Wrapper<ImageResource> noisePreviewWrapper = new Wrapper<ImageResource>();

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

    UIButton openNoiseModalButton = new UIButton(screen) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openNoiseModal(() -> {});
      }
    };
    openNoiseModalButton.minWidth = 5;
    openNoiseModalButton.minHeight = 5;
    openNoiseModalButton.color = new Color("#ffffff");

    screen.addChild(openNoiseModalButton);
    clickHandler.register(openNoiseModalButton);

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

    newPosition = new ColorPosition(new Color("#31A500"), 1, 110);
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
          Pointer.draw(this.getX() + this.getWidth() / 2, this.valueToY(this.getValue()) - this.valueToYRelative(.5f), this.getWidth(), this.getChildHeight(), this.facing);
        }
        if (this.outlineWeight > 0f) {
          Global.drawColor(this.getOutlineColor());
          PointerOutline.draw(this.getX() + this.getWidth() / 2, this.valueToY(this.getValue()) - this.valueToYRelative(.5f), this.getWidth(), this.getChildHeight(), this.facing, this.outlineWeight);
        }
        for (int i = 0; i < this.children.size(); i++) {
          this.children.get(i).render();
        }
      }

      @Override
      public int getZ(){
        if (this.onTop) {
          return 100000;
        } else {
          return super.getZ();
        }
      }

      @Override
      public void mousedDown(float x, float y) {
        super.mousedDown(x, y);
        clickHandler.setMask(this);
      }

      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
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

    slider.x = 2.114f;
    slider.minValue = 0;
    slider.maxValue = 256;
    slider.minHeight = .5f;
    slider.maxWidth = .3f;
    slider.minWidth = 0;
    slider.maxHeight = 50;
    slider.color = new Color("#000000");
    slider.mouseOverColor = new Color("#777777");
    slider.outlineColor = new Color("#ffffff");
    slider.outlineWeight = .1f;

    parent.addChild(slider);
    clickHandler.register(slider);

    UIBoxRow sliderButtons = new UIBoxRow(slider);
    slider.outlineColor = new Color("#ffffff");
    sliderButtons.outlineWeight = .1f;
    sliderButtons.noBackground = true;
    sliderButtons.margin = .1f;
    slider.addChild(sliderButtons);


    Color newColorLowS = new Color(newPosition.color) {
      @Override
      public void transformRef() {
        super.transformRef();
        float[] hsv = this.refColor.getHSV();
        hsv[1] = Math.max(hsv[1] - .5f, 0f);
        this.setHSV(hsv);
      }
    };

    UIButton menuButton = new UIButton(sliderButtons) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openSliderOptionsModal(slider, newPosition, gradientPositions, solidPositions, gradientTestBuffer, () -> {
          GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
          palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
        });
      }
    };
    menuButton.color = new Color("#000000");
    menuButton.mouseOverColor = new Color("#777777");
    menuButton.setZ(1000000);

    sliderButtons.addChild(menuButton);
    clickHandler.register(menuButton);

    UIImage menuIcon = new UIImage(menuButton, ImageResources.menuIcon);
    menuIcon.minWidth = 0f;
    menuIcon.minHeight = 0f;
    menuButton.addChild(menuIcon);

    UIButton colorButton = new UIButton(sliderButtons) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openColorModal(newPosition.color, () -> {
          GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
          palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
        });
      }
    };
    colorButton.minHeight = .3f;
    colorButton.minWidth = .3f;
    colorButton.color = newPosition.color;
    colorButton.mouseOverColor = newColorLowS;
    colorButton.setZ(1000000);

    sliderButtons.addChild(colorButton);
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
    sliderContainer.minWidth = 3.4f;
    contentRow.addChild(sliderContainer);

    UIButton sliderControlArea = new UIButton(sliderContainer) {
      @Override
      public void mousedDown(float x, float y) {

      }

      @Override
      public void mouseMoved(float x, float y) {
        if (!this.getIsMousedOver()) {
          return;
        }
        float minDist = -1;
        UIVerticalValueSlider closest = null;
        for (UIElement child : sliderContainer.children) {
          if (!(child instanceof UIVerticalValueSlider)) {
            continue;
          }
          UIVerticalValueSlider slider = (UIVerticalValueSlider) child;
          float dist = Math.abs(y - slider.valueToY(slider.getValue()));
          if (minDist == -1 || dist < minDist) {
            minDist = dist;
            closest = slider;
          }
        }
        for (UIElement child : sliderContainer.children) {
          if (!(child instanceof UIVerticalValueSlider)) {
            continue;
          }
          UIVerticalValueSlider slider = (UIVerticalValueSlider) child;
          if (closest.equals(slider)) {
            continue;
          }
          slider.x = 2.114f;
          slider.maxWidth = .3f;
          slider.setOnTop(false);
          for (UIElement sliderChild : slider.children) {
            for (int i = 0; i < sliderChild.children.size(); i++) {
              UIElement scc = sliderChild.children.get(i);
              if (i == 0) {
                scc.minWidth = 0;
                scc.minHeight = 0;
                for (UIElement sccc : scc.children) {
                  sccc.minWidth = 0;
                  sccc.minHeight = 0;
                }
              }
              if (i == 1) {
                scc.minWidth = .3f;
                scc.minHeight = .3f;
                for (UIElement sccc : scc.children) {
                  sccc.minWidth = .3f;
                  sccc.minHeight = .3f;
                }
              }
            }
          }
        }
        closest.maxWidth = 1f;
        closest.x = 0f;
        closest.setOnTop(true);
        for (UIElement sliderChild : closest.children) {
          for (UIElement scc : sliderChild.children) {
            scc.minWidth = 1f;
            scc.minHeight = 1f;
            for (UIElement sccc : scc.children) {
              sccc.minWidth = 1f;
              sccc.minHeight = 1f;
            }
          }
        }
        Comparator<UIElement> comparator = (UIElement o1, UIElement o2) -> ((UIButton)o1).getZ() < 0 ? 1 : ((UIButton)o2).getZ() < 0 ? -1 : ((UIButton)o1).getZ() - ((UIButton)o2).getZ();
        Collections.sort(sliderContainer.children, comparator);
      }
    };
    sliderControlArea.outlineWeight = 0f;
    sliderControlArea.noBackground = true;
    sliderControlArea.minHeight = 50f;
    sliderControlArea.minWidth = 3.4f;
    sliderControlArea.setZ(-1);
    sliderContainer.addChild(sliderControlArea);
    clickHandler.register(sliderControlArea);

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
    addNewSliderButton.outlineWeight = .1f;
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

  private static void openSliderOptionsModal(UIVerticalValueSlider slider, ColorPosition colorPosition, ArrayList<ColorPosition> gradientPositions, ArrayList<ColorPosition> solidPositions, BufferedImage gradientTestBuffer, Runnable afterClose) {
    UIModal sliderOptionsModal = new UIModal(screen);
    sliderOptionsModal.centerBox.color = new Color("#000000");
    sliderOptionsModal.centerBox.outlineColor = new Color("#ffffff");
    sliderOptionsModal.centerBox.outlineWeight = .1f;
    sliderOptionsModal.centerBox.radius = .5f;
    screen.addChild(sliderOptionsModal);
    clickHandler.setMask(sliderOptionsModal.centerBox);

    UICenter duplicateButtonCenterer = new UICenter(sliderOptionsModal.centerBox);
    duplicateButtonCenterer.centerY = false;
    duplicateButtonCenterer.padding = .3f;
    sliderOptionsModal.addChild(duplicateButtonCenterer);

    UIButton duplicateButton = new UIButton(duplicateButtonCenterer){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        sliderOptionsModal.close();
        if (gradientPositions.size() < 256) {
          Comparator<ColorPosition> comparator = (ColorPosition o1, ColorPosition o2) -> o1.position - o2.position;
          Collections.sort(gradientPositions, comparator);
          int pos = colorPosition.position;
          for (ColorPosition colorPos : gradientPositions) {
            if (colorPos.position < pos) {
              continue;
            }
            if (colorPos.position != pos) {
              break;
            }
            pos++;
          }
          if (pos > 255) {
            comparator = (ColorPosition o1, ColorPosition o2) -> o2.position - o1.position;
            Collections.sort(gradientPositions, comparator);
            pos = colorPosition.position;
            for (ColorPosition colorPos : gradientPositions) {
              if (colorPos.position > pos) {
                continue;
              }
              if (colorPos.position != pos) {
                break;
              }
              pos--;
            }
          }
          ColorPosition newPosition = new ColorPosition(colorPosition.color.clone(), 1, pos);
          gradientPositions.add(newPosition);

          addGradientSlider(slider.parent, gradientPositions, solidPositions, newPosition, gradientTestBuffer);
          afterClose.run();
        }
      }
    };
    duplicateButton.color = new Color("#000000");
    duplicateButton.outlineColor = new Color("#ffffff");
    duplicateButton.outlineWeight = .1f;
    duplicateButton.noBackground = false;
    duplicateButton.padding = .5f;
    duplicateButton.margin = .3f;
    duplicateButton.mouseOverColor = new Color("#777777");
    duplicateButtonCenterer.addChild(duplicateButton);
    clickHandler.register(duplicateButton);
    UITextBlock duplicateButtonText = new UITextBlock(duplicateButton, "Duplicate", .5f);
    duplicateButtonText.textColor = new Color("#ffffff");
    duplicateButtonText.noBackground = false;
    duplicateButton.addChild(duplicateButtonText);

    UICenter removeButtonCenterer = new UICenter(sliderOptionsModal.centerBox);
    removeButtonCenterer.centerY = false;
    removeButtonCenterer.padding = .3f;
    sliderOptionsModal.addChild(removeButtonCenterer);

    UIButton removeButton = new UIButton(removeButtonCenterer){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        sliderOptionsModal.close();
        slider.removeFromParent();
        if(gradientPositions.contains(colorPosition)) {
          gradientPositions.remove(colorPosition);
        }
        if(solidPositions.contains(colorPosition)) {
          solidPositions.remove(colorPosition);
        }
        afterClose.run();
      }
    };
    removeButton.color = new Color("#000000");
    removeButton.outlineColor = new Color("#ffffff");
    removeButton.outlineWeight = .1f;
    removeButton.noBackground = false;
    removeButton.padding = .5f;
    removeButton.margin = .3f;
    removeButton.mouseOverColor = new Color("#777777");
    removeButtonCenterer.addChild(removeButton);
    clickHandler.register(removeButton);
    UITextBlock removeButtonText = new UITextBlock(removeButton, "Remove", .5f);
    removeButtonText.textColor = new Color("#ffffff");
    removeButtonText.noBackground = false;
    removeButton.addChild(removeButtonText);

    UICenter closeButtonCenterer = new UICenter(sliderOptionsModal.centerBox);
    closeButtonCenterer.centerY = false;
    closeButtonCenterer.padding = .3f;
    sliderOptionsModal.addChild(closeButtonCenterer);

    UIButton closeButton = new UIButton(closeButtonCenterer){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        sliderOptionsModal.close();
        afterClose.run();
      }
    };
    closeButton.color = new Color("#000000");
    closeButton.outlineColor = new Color("#ffffff");
    closeButton.outlineWeight = .1f;
    closeButton.noBackground = false;
    closeButton.padding = .5f;
    closeButton.margin = .3f;
    closeButton.mouseOverColor = new Color("#aa0000");
    closeButtonCenterer.addChild(closeButton);
    clickHandler.register(closeButton);
    UITextBlock closeButtonText = new UITextBlock(closeButton, "Close", .5f);
    closeButtonText.textColor = new Color("#ffffff");
    closeButtonText.noBackground = false;
    closeButton.addChild(closeButtonText);
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

  private static void openNoiseModal() {
    openNoiseModal(() -> {});
  }

  private static void constructDropdown(UIBoxCol fieldBox, UISelectionMenu field, String[] dropdownOptions, String title) {

    UIBoxRow fieldRow = new UIBoxRow(fieldBox);
    fieldRow.outlineWeight = 0;
    fieldRow.noBackground = true;
    fieldRow.padding = .3f;
    fieldBox.addChild(fieldRow);

    UIBoxRow fieldLabelRow = new UIBoxRow(fieldRow);
    fieldLabelRow.minWidth = 10;
    fieldLabelRow.minHeight = 2f;
    fieldRow.outlineWeight = 0;
    fieldRow.noBackground = true;
    fieldRow.addChild(fieldLabelRow);

    UIRightPositioner fieldLabelPositioner = new UIRightPositioner(fieldLabelRow);
    fieldLabelRow.addChild(fieldLabelPositioner);

    UITextBlock titleText = new UITextBlock(fieldLabelPositioner, title, .5f);
    titleText.textColor = new Color("#ffffff");
    titleText.noBackground = true;
    titleText.maxWidth = 10f;
    titleText.y = .2f;
    fieldLabelPositioner.addChild(titleText);

    UIBoxRow fieldRowPadding = new UIBoxRow(fieldRow);
    fieldRowPadding.minWidth = .3f;
    fieldRowPadding.outlineWeight = 0;
    fieldRowPadding.noBackground = true
    ;
    fieldRow.addChild(fieldRowPadding);

    field.parent = fieldRow;
    fieldRow.addChild(field);
    clickHandler.register(field);

    field.minWidth = 10;
    field.minHeight = 2f;
    field.selectionBoxHeight = 6;
    field.color = new Color("#000000");
    field.mouseOverColor = new Color("#444444");
    field.selectedColor = new Color("#222222");
    field.mouseOverOutlineColor = new Color("#ffffff");
    field.selectedOutlineColor = new Color("#ffffff");
    field.outlineWeight = .1f;
    field.margin = .1f;

    for (String option : dropdownOptions) {
      UISelectable entry = new UISelectable(field.scrollableList);
      entry.x = 0;
      entry.y = 0;
      entry.minWidth = 10f;
      entry.minHeight = 1.5f;
      entry.color = new Color("#222222");
      entry.mouseOverColor = new Color("#888888");
      entry.outlineWeight = 0f;
      entry.setZ(10);
      field.addOption(entry, option);
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
      entryPadding.minWidth = .1f;
      entryPadding.noBackground = true;
      entryPadding.outlineWeight = 0f;
      entryContents.addChild(entryPadding);

      UITextBlock entryText = new UITextBlock(entryContents, option, .5f);
      entryText.textColor = new Color("#ffffff");
      entryText.noBackground = true;
      entryText.maxWidth = 10f;
      entryContents.addChild(entryText);
    }
  }

  private static void constructNumField(UIBoxCol fieldBox, UIBoxRow fieldRow, UINumberInput field, String title) {

    fieldRow.parent = fieldBox;
    fieldRow.outlineWeight = 0;
    fieldRow.noBackground = true;
    fieldRow.padding = .3f;
    fieldBox.addChild(fieldRow);

    UIBoxRow fieldLabelRow = new UIBoxRow(fieldRow);
    fieldLabelRow.minWidth = 10;
    fieldLabelRow.minHeight = 2f;
    fieldRow.outlineWeight = 0;
    fieldRow.noBackground = true;
    fieldRow.children.add(0, fieldLabelRow);

    UIRightPositioner fieldLabelPositioner = new UIRightPositioner(fieldLabelRow);
    fieldLabelRow.addChild(fieldLabelPositioner);

    UITextBlock titleText = new UITextBlock(fieldLabelPositioner, title, .5f);
    titleText.textColor = new Color("#ffffff");
    titleText.noBackground = true;
    titleText.maxWidth = 10f;
    titleText.y = .2f;
    fieldLabelPositioner.addChild(titleText);

    UIBoxRow fieldRowPadding = new UIBoxRow(fieldRow);
    fieldRowPadding.minWidth = .3f;
    fieldRowPadding.outlineWeight = 0;
    fieldRowPadding.noBackground = true;
    fieldRow.children.add(1, fieldRowPadding);

    clickHandler.register(field);
    keyboardHandler.register(field);

    field.minWidth = 10f - .4f - (1.7f / 2f - .6f) * 2f;;
    field.minHeight = 1.7f;
    field.color = new Color("#000000");
    field.textColor = new Color("#ffffff");
    field.mouseOverColor = new Color("#444444");
    field.selectedColor = new Color("#222222");
    field.mouseOverOutlineColor = new Color("#ffffff");
    field.selectedOutlineColor = new Color("#ffffff");
    field.outlineWeight = .1f;
    field.margin = .1f;

    field.incrementButton.mouseOverOutlineColor = new Color("#ffffff");
    field.incrementButton.outlineColor = new Color("#ffffff");
    field.incrementButton.color = new Color("#000000");
    field.incrementButton.mouseOverColor = new Color("#444444");
    field.incrementIcon.padding = .3f;
    field.incrementIcon.minWidth = (1.7f / 2f - .6f) * 2f;
    field.incrementIcon.minHeight = 1.7f / 2f - .6f;

    field.decrementButton.mouseOverOutlineColor = new Color("#ffffff");
    field.decrementButton.outlineColor = new Color("#ffffff");
    field.decrementButton.color = new Color("#000000");
    field.decrementButton.mouseOverColor = new Color("#444444");
    field.decrementIcon.padding = .3f;
    field.decrementIcon.minWidth = (1.7f / 2f - .6f) * 2f;
    field.decrementIcon.minHeight = 1.7f / 2f - .6f;
  }

  private static void openNoiseModal(Runnable afterClose) {

    FastNoiseLite fnl = new FastNoiseLite();

    noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));

    UIModal noiseModal = new UIModal(screen);
    // noiseModal.centerBox.minWidth = 20f;
    // noiseModal.centerBox.minHeight = 20f;
    noiseModal.centerBox.color = new Color("#000000");
    noiseModal.centerBox.outlineColor = new Color("#ffffff");
    noiseModal.centerBox.outlineWeight = .1f;
    noiseModal.centerBox.radius = .5f;
    noiseModal.centerBox.margin = .5f;
    screen.addChild(noiseModal);
    clickHandler.setMask(noiseModal.centerBox);

    UIBoxRow modalContentRow = new UIBoxRow(noiseModal.centerBox);
    modalContentRow.outlineWeight = 0;
    modalContentRow.noBackground = true;
    noiseModal.centerBox.addChild(modalContentRow);

    UIBoxCol controls = new UIBoxCol(modalContentRow);
    controls.outlineWeight = 0;
    controls.noBackground = true;
    modalContentRow.addChild(controls);

    UITextBlock generalText = new UITextBlock(controls, "General", .5f);
    generalText.textColor = new Color("#ffffff");
    generalText.noBackground = true;
    generalText.maxWidth = 10f;
    controls.addChild(generalText);

    UISelectionMenu noiseTypeField = new UISelectionMenu(null, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetNoiseType()) {
          case OpenSimplex2:
            return "OpenSimplex2";
          case OpenSimplex2S:
            return "OpenSimplex2S";
          case Cellular:
            return "Cellular";
          case Perlin:
            return "Perlin";
          case ValueCubic:
            return "ValueCubic";
          case Value:
            return "Value";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "OpenSimplex2":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
            break;
          case "OpenSimplex2S":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
            break;
          case "Cellular":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
            break;
          case "Perlin":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            break;
          case "ValueCubic":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            break;
          case "Value":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.Value);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
      }
    };

    String[] noiseTypeFieldOptions = {
      "OpenSimplex2",
      "OpenSimplex2S",
      "Cellular",
      "Perlin",
      "ValueCubic",
      "Value"
    };

    constructDropdown(controls, noiseTypeField, noiseTypeFieldOptions, "Noise Type");

    UISelectionMenu rotationType3DField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetRotationType3D()) {
          case None:
            return "None";
          case ImproveXYPlanes:
            return "ImproveXYPlanes";
          case ImproveXZPlanes:
            return "ImproveXZPlanes";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            fnl.SetRotationType3D(FastNoiseLite.RotationType3D.None);
            break;
          case "ImproveXYPlanes":
            fnl.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXYPlanes);
            break;
          case "ImproveXZPlanes":
            fnl.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
      }
    };

    String[] rotationType3DFieldOptions = {
      "None",
      "ImproveXYPlanes",
      "ImproveXZPlanes"
    };

    constructDropdown(controls, rotationType3DField, rotationType3DFieldOptions, "Rotation Type 3D");

    Wrapper<String> frequencyStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFrequency()));
    UIBoxRow frequencyFieldRow = new UIBoxRow(null);
    UINumberInput frequencyField = new UINumberInput(frequencyFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFrequency();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFrequency(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return frequencyStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        frequencyStringWrapper.set(val);
        return true;
      }
    };
    frequencyField.minValue = -1000;
    frequencyField.maxValue = 1000;
    frequencyField.incrementAmount = .005f;
    frequencyField.precision = 3;

    constructNumField(controls, frequencyFieldRow, frequencyField, "Frequency");

    UITextBlock fractalText = new UITextBlock(controls, "Fractal", .5f);
    fractalText.textColor = new Color("#ffffff");
    fractalText.noBackground = true;
    fractalText.maxWidth = 10f;
    controls.addChild(fractalText);

    UISelectionMenu fractalTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetFractalType()) {
          case None:
            return "None";
          case FBm:
            return "FBm";
          case Ridged:
            return "Ridged";
          case PingPong:
            return "PingPong";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            fnl.SetFractalType(FastNoiseLite.FractalType.None);
            break;
          case "FBm":
            fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
            break;
          case "Ridged":
            fnl.SetFractalType(FastNoiseLite.FractalType.Ridged);
            break;
          case "PingPong":
            fnl.SetFractalType(FastNoiseLite.FractalType.PingPong);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
      }
    };

    String[] fractalTypeFieldOptions = {
      "None",
      "FBm",
      "Ridged",
      "PingPong"
    };

    constructDropdown(controls, fractalTypeField, fractalTypeFieldOptions, "Type");

    Wrapper<String> fractalOctavesStringWrapper = new Wrapper<String>(Integer.toString(fnl.GetFractalOctaves()));
    UIBoxRow fractalOctavesFieldRow = new UIBoxRow(null);
    UINumberInput fractalOctavesField = new UINumberInput(fractalOctavesFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          int oldVal = fnl.GetFractalOctaves();
          if (oldVal == Integer.parseInt(val)) {
            return;
          }
          fnl.SetFractalOctaves(Integer.parseInt(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalOctavesStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalOctavesStringWrapper.set(val);
        return true;
      }
    };
    fractalOctavesField.minValue = 0;
    fractalOctavesField.maxValue = 10;
    fractalOctavesField.incrementAmount = 1f;
    fractalOctavesField.precision = 0;

    constructNumField(controls, fractalOctavesFieldRow, fractalOctavesField, "Octaves");

    Wrapper<String> fractalLacunarityStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalLacunarity()));
    UIBoxRow fractalLacunarityFieldRow = new UIBoxRow(null);
    UINumberInput fractalLacunarityField = new UINumberInput(fractalLacunarityFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalLacunarity();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalLacunarity(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalLacunarityStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalLacunarityStringWrapper.set(val);
        return true;
      }
    };
    fractalLacunarityField.minValue = -10;
    fractalLacunarityField.maxValue = 10;
    fractalLacunarityField.incrementAmount = .1f;
    fractalLacunarityField.precision = 2;

    constructNumField(controls, fractalLacunarityFieldRow, fractalLacunarityField, "Lacunarity");

    Wrapper<String> fractalGainStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalGain()));
    UIBoxRow fractalGainFieldRow = new UIBoxRow(null);
    UINumberInput fractalGainField = new UINumberInput(fractalGainFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalGain();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalGain(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalGainStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalGainStringWrapper.set(val);
        return true;
      }
    };
    fractalGainField.minValue = -10;
    fractalGainField.maxValue = 10;
    fractalGainField.incrementAmount = .1f;
    fractalGainField.precision = 2;

    constructNumField(controls, fractalGainFieldRow, fractalGainField, "Gain");

    Wrapper<String> fractalWeightedStrengthStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalWeightedStrength()));
    UIBoxRow fractalWeightedStrengthFieldRow = new UIBoxRow(null);
    UINumberInput fractalWeightedStrengthField = new UINumberInput(fractalWeightedStrengthFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalWeightedStrength();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalWeightedStrength(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalWeightedStrengthStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalWeightedStrengthStringWrapper.set(val);
        return true;
      }
    };
    fractalWeightedStrengthField.minValue = -1000;
    fractalWeightedStrengthField.maxValue = 1000;
    fractalWeightedStrengthField.incrementAmount = .1f;
    fractalWeightedStrengthField.precision = 2;

    constructNumField(controls, fractalWeightedStrengthFieldRow, fractalWeightedStrengthField, "Weighted Strength");

    Wrapper<String> fractalPingPongStrengthStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalPingPongStrength()));
    UIBoxRow fractalPingPongStrengthFieldRow = new UIBoxRow(null);
    UINumberInput fractalPingPongStrengthField = new UINumberInput(fractalPingPongStrengthFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalPingPongStrength();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalPingPongStrength(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalPingPongStrengthStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalPingPongStrengthStringWrapper.set(val);
        return true;
      }
    };
    fractalPingPongStrengthField.minValue = -100;
    fractalPingPongStrengthField.maxValue = 100;
    fractalPingPongStrengthField.incrementAmount = .1f;
    fractalPingPongStrengthField.precision = 2;

    constructNumField(controls, fractalPingPongStrengthFieldRow, fractalPingPongStrengthField, "Ping Pong Strength");

    UITextBlock cellularText = new UITextBlock(controls, "Cellular", .5f);
    cellularText.textColor = new Color("#ffffff");
    cellularText.noBackground = true;
    cellularText.maxWidth = 10f;
    controls.addChild(cellularText);

    UISelectionMenu cellularDistanceFunctionField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetCellularDistanceFunction()) {
          case Euclidean:
            return "Euclidean";
          case EuclideanSq:
            return "EuclideanSq";
          case Manhattan:
            return "Manhattan";
          case Hybrid:
            return "Hybrid";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "Euclidean":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
            break;
          case "EuclideanSq":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
            break;
          case "Manhattan":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Manhattan);
            break;
          case "Hybrid":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Hybrid);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
      }
    };

    String[] cellularDistanceFunctionFieldOptions = {
      "Euclidean",
      "EuclideanSq",
      "Manhattan",
      "Hybrid"
    };

    constructDropdown(controls, cellularDistanceFunctionField, cellularDistanceFunctionFieldOptions, "Distance Function");

    UISelectionMenu cellularReturnTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetCellularReturnType()) {
          case CellValue:
            return "CellValue";
          case Distance:
            return "Distance";
          case Distance2:
            return "Distance2";
          case Distance2Add:
            return "Distance2Add";
          case Distance2Sub:
            return "Distance2Sub";
          case Distance2Mul:
            return "Distance2Mul";
          case Distance2Div:
            return "Distance2Div";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "CellValue":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);
            break;
          case "Distance":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
            break;
          case "Distance2":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2);
            break;
          case "Distance2Add":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Add);
            break;
          case "Distance2Sub":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Sub);
            break;
          case "Distance2Mul":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Mul);
            break;
          case "Distance2Div":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Div);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
      }
    };

    String[] cellularReturnTypeFieldOptions = {
      "CellValue",
      "Distance",
      "Distance2",
      "Distance2Add",
      "Distance2Sub",
      "Distance2Mul",
      "Distance2Div"
    };

    constructDropdown(controls, cellularReturnTypeField, cellularReturnTypeFieldOptions, "Return Type");

    Wrapper<String> cellularJitterStringWrapper = new Wrapper<String>(Float.toString(fnl.GetCellularJitter()));
    UIBoxRow cellularJitterFieldRow = new UIBoxRow(null);
    UINumberInput cellularJitterField = new UINumberInput(cellularJitterFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetCellularJitter();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetCellularJitter(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return cellularJitterStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        cellularJitterStringWrapper.set(val);
        return true;
      }
    };
    cellularJitterField.minValue = -10;
    cellularJitterField.maxValue = 10;
    cellularJitterField.incrementAmount = .1f;
    cellularJitterField.precision = 2;

    constructNumField(controls, cellularJitterFieldRow, cellularJitterField, "Jitter");

    UITextBlock domainWarpText = new UITextBlock(controls, "Domain Warp", .5f);
    domainWarpText.textColor = new Color("#ffffff");
    domainWarpText.noBackground = true;
    domainWarpText.maxWidth = 10f;
    controls.addChild(domainWarpText);

    UISelectionMenu domainWarpTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetDomainWarpType()) {
          case OpenSimplex2:
            return "OpenSimplex2";
          case OpenSimplex2Reduced:
            return "OpenSimplex2Reduced";
          case BasicGrid:
            return "BasicGrid";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "OpenSimplex2":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
            break;
          case "OpenSimplex2Reduced":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
            break;
          case "BasicGrid":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 1L, 800)));
      }
    };

    String[] domainWarpTypeFieldOptions = {
      "OpenSimplex2",
      "OpenSimplex2Reduced",
      "BasicGrid"
    };

    constructDropdown(controls, domainWarpTypeField, domainWarpTypeFieldOptions, "Type");

    UIImage noisePreview = new UIImage(modalContentRow, noisePreviewWrapper);
    noisePreview.minWidth = 31.4f;
    noisePreview.minHeight = 15.7f;
    // noisePreview.useAA = false;
    modalContentRow.addChild(noisePreview);

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
