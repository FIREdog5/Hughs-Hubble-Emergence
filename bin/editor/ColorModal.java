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
import bin.graphics.ui.complex.UIScrollableBox;
import bin.graphics.ui.complex.UITextInput;
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
import java.util.regex.*;
import java.util.Arrays;

class ColorModal {
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

  public static void openColorModal(Color colorPointer, Runnable afterClose) {

    //set up modal and create a temporary new color
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

    //color wheel and sliders
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

    //a text input for hex codes
    UICenter hexInputCenterer = new UICenter(colorWheelCol);
    hexInputCenterer.centerY = false;
    colorWheelCol.addChild(hexInputCenterer);

    UITextInput hexField = new UITextInput(hexInputCenterer, .5f){

      Wrapper<String> hexStringWrapper = new Wrapper<String>(newColor.getHex());

      @Override
      public String getValue() {
        if(this.isSelected()) {
          return hexStringWrapper.get();
        }
        return newColor.getHex();
      }

      @Override
      public void select() {
        super.select();
        hexStringWrapper.set(newColor.getHex());
      }

      @Override
      public boolean setValue(String newValue) {
        hexStringWrapper.set(newValue.toLowerCase());
        return true;
      }

      @Override
      public boolean setUnfilteredValue(String newValue) {
        if (Pattern.matches("#[0-9A-Fa-f]{0,6}", newValue)) {
          return setValue(newValue);
        }
        return false;
      }

      @Override
      public void onDeselect() {
        if (!this.isSelected()) {
          return;
        }
        String newValue = this.getValue();
        if (newValue.length() != 7) {
          return;
        }
        if (Pattern.matches("#[0-9A-Fa-f]{6}", newValue)) {
          Color tempColor = new Color(newValue);
          float[] hsv = tempColor.getHSV();
          if(hsv[1] == 0)
          {
            hsv[1] = .000001f;
          }
          newColor.setHSV(hsv);
        }
        return;
      }
    };
    hexField.minWidth = 4.3f;
    hexField.minHeight = 1.7f;
    hexField.color = new Color("#000000");
    hexField.textColor = new Color("#ffffff");
    hexField.mouseOverColor = new Color("#444444");
    hexField.selectedColor = new Color("#222222");
    hexField.mouseOverOutlineColor = new Color("#ffffff");
    hexField.selectedOutlineColor = new Color("#ffffff");
    hexField.outlineWeight = .1f;
    hexField.margin = .1f;
    hexField.padding = .5f;

    hexInputCenterer.addChild(hexField);
    clickHandler.register(hexField);
    keyboardHandler.register(hexField);

    //bottom bar with cancel and confirm buttons
    UICenter bottomBarCenterer = new UICenter(colorModal.centerBox);
    bottomBarCenterer.centerY = false;
    colorModal.addChild(bottomBarCenterer);

    UIBoxRow bottomBar = new UIBoxRow(bottomBarCenterer);
    bottomBar.outlineWeight = 0;
    bottomBar.noBackground = true;
    bottomBarCenterer.addChild(bottomBar);

    UIButton confirmButton = new UIButton(bottomBar){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        colorPointer.setRGB(newColor.getRGB());
        clickHandler.popMask();
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

    UIButton cancelButton = new UIButton(bottomBar){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.popMask();
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
}
