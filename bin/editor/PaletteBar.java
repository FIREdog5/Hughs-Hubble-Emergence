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
import bin.graphics.ui.UIText;
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

class PaletteBar {
  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;
  private static Wrapper<ImageResource> palettResourceWrapper = new Wrapper<ImageResource>();

  public static void init(ClickHandler passedClickHandler, KeyboardHandler passedKeyboardHandler, UIScreen passedScreen, Wrapper<ImageResource> passedPalettResourceWrapper) {
    clickHandler = passedClickHandler;
    keyboardHandler = passedKeyboardHandler;
    screen = passedScreen;
    palettResourceWrapper = passedPalettResourceWrapper;
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
    Wrapper<UIText> displayTextWrapper = new Wrapper<UIText>();
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

      public float valueToYRelative(float value) {
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
          return 110000;
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
        clickHandler.popMask();
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
        if (displayTextWrapper.get() != null) {
          displayTextWrapper.get().text = Integer.toString(255 - newPosition.position);
        }
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
    // slider.setZ(9);

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
    menuButton.setZ(10);

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
        ColorModal.openColorModal(newPosition.color, () -> {
          GradientGenerator.setColumn(gradientTestBuffer, 0, gradientPositions.toArray(new ColorPosition[]{}), solidPositions.toArray(new ColorPosition[]{}));
          palettResourceWrapper.set(new ImageResource(gradientTestBuffer));
        });
      }
    };
    colorButton.minHeight = .3f;
    colorButton.minWidth = .3f;
    colorButton.color = newPosition.color;
    colorButton.mouseOverColor = newColorLowS;
    colorButton.setZ(10);

    sliderButtons.addChild(colorButton);
    clickHandler.register(colorButton);

    UIBoxLayered textPositioner = new UIBoxLayered(slider){
      @Override
      public float getWidth() {
        return 0;
      }
      @Override
      public float getHeight() {
        return 0;
      }
    };
    textPositioner.noBackground = true;
    textPositioner.outlineWeight = 0f;
    slider.addChild(textPositioner);

    UIText displayText = new UIText(textPositioner, Integer.toString(255 - newPosition.position), 0f);
    displayText.color = new Color("#ffffff");
    displayText.noBackground = true;
    displayText.maxWidth = 10f;
    displayText.x = 4.5f;
    displayText.y = .2f;
    textPositioner.addChild(displayText);
    displayTextWrapper.set(displayText);

  }

  public static void makeSidePaletteBar() {
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
            if (sliderChild instanceof UIBoxLayered) {
              ((UIText)(sliderChild.children.get(0))).size = 0f;
              continue;
            }
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
          if (sliderChild instanceof UIBoxLayered) {
            ((UIText)(sliderChild.children.get(0))).size = .5f;
            continue;
          }
          for (UIElement scc : sliderChild.children) {
            scc.minWidth = 1f;
            scc.minHeight = 1f;
            for (UIElement sccc : scc.children) {
              sccc.minWidth = 1f;
              sccc.minHeight = 1f;
            }
          }
        }
        Comparator<UIElement> comparator = (UIElement o1, UIElement o2) -> ((UIButton)o1).getZ() < 0 ? 1 : ((UIButton)o2).getZ() < 0 ? -1 : ((UIButton)o2).getZ() - ((UIButton)o1).getZ();
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
        clickHandler.popMask();
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
        clickHandler.popMask();
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
        clickHandler.popMask();
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
}
