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
import bin.resource.proceduralGeneration.modifiers.*;
import bin.resource.proceduralGeneration.ImageCombineModifier;
import bin.resource.proceduralGeneration.IImageModifier;

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

class NoiseBar {
  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;
  private static Palette editorPalette;
  private static Wrapper<ImageResource> heightMapWrapper;

  public static void init(ClickHandler passedClickHandler, KeyboardHandler passedKeyboardHandler, UIScreen passedScreen, Wrapper<ImageResource> palettResourceWrapper, Wrapper<ImageResource> passedHeightMapWrapper) {
    clickHandler = passedClickHandler;
    keyboardHandler = passedKeyboardHandler;
    screen = passedScreen;
    editorPalette = new EditorPalette(palettResourceWrapper,
                                      new Color("#03fcf8"), //atmosphere lower
                                      new Color("#0000ff", .3f) //atmosphere upper
                                      );
    heightMapWrapper = passedHeightMapWrapper;
  }

  private static void recalculateNoise(ArrayList<NoiseStepData> noiseStepList, NoiseStepData changedNoiseStep) {
    int index = noiseStepList.indexOf(changedNoiseStep);
    if (index >= noiseStepList.size() || index < 0) {
      index = 0;
    }
    for (int i = index; i < noiseStepList.size(); i++) {
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
    noiseStepData.fnl.SetSeed(1);
    noiseStepData.modifier = new ImageCreateModifier(noiseStepData.fnl);

    noiseStepList.add(noiseStepData);

    recalculateNoise(noiseStepList, noiseStepData);

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
                                                                                                   recalculateNoise(noiseStepList, noiseStepData);
                                                                                                  });
      }
    };
    noisePreviewButton.mouseOverOutlineColor = new Color("#ffffff");
    noisePreviewButton.outlineColor = new Color("#ffffff");
    noisePreviewButton.noBackground = true;
    noisePreviewButton.outlineWeight = .1f;
    noisePreviewButton.minWidth = 6f;
    noisePreviewButton.minHeight = 6f;
    noisePreviewButton.radius = 3f;
    stepRow.addChild(noisePreviewButton);
    clickHandler.register(noisePreviewButton);

    UIGlobeDisplay grayGlobe = new UIGlobeDisplay(noisePreviewButton, noiseStepData.noisePreviewWrapper, null) {
      @Override
      public Color getColor() {
        if (noisePreviewButton.getIsMousedOver()) {
          return new Color("#bbbbbb");
        }
        else {
          return super.getColor();
        }
      }
    };
    grayGlobe.setRadius(5.8f);
    grayGlobe.padding = .1f;
    noisePreviewButton.addChild(grayGlobe);

    scrollBox.addChild(stepRow);
  }

  private static void addNoiseStep(UIScrollableBox scrollBox, ArrayList<NoiseStepData> noiseStepList) {
    NoiseStepData noiseStepData = new NoiseStepData();
    addNoiseStep(scrollBox, noiseStepList, noiseStepData, -1);
  }

  private static void addNoiseStep(UIScrollableBox scrollBox, ArrayList<NoiseStepData> noiseStepList, NoiseStepData noiseStepData, int index) {
    if(noiseStepData.init == false) {
      noiseStepData.fnl = new FastNoiseLite();
      noiseStepData.fnl.SetSeed(1);
      noiseStepData.modifier = new ImageAddModifier(noiseStepData.fnl);
      noiseStepData.init = true;
    }

    if (index >= 0 && index < noiseStepList.size()) {
      noiseStepList.add(index, noiseStepData);
    } else {
      noiseStepList.add(noiseStepData);
    }

    recalculateNoise(noiseStepList, noiseStepData);

    UIBoxRow stepRow = new UIBoxRow(scrollBox);
    stepRow.minWidth = 18f;
    stepRow.minHeight = 6f;
    stepRow.padding = .5f;
    stepRow.color = new Color("#000000");
    stepRow.outlineColor = new Color("#ffffff");
    stepRow.outlineWeight = .1f;

    UIBoxCol orderCol = new UIBoxCol(stepRow);
    orderCol.margin = .2f;
    orderCol.outlineWeight = .1f;
    orderCol.color = new Color("#000000");
    orderCol.outlineColor = new Color("#ffffff");
    orderCol.minWidth = 2f;
    orderCol.minHeight = 6f;
    stepRow.addChild(orderCol);

    UIButton upButton = new UIButton(orderCol){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x,y);
        int index = noiseStepList.indexOf(noiseStepData);
        if (index > 1) {
          Collections.swap(noiseStepList, index, index - 1);
          index = scrollBox.children.indexOf(stepRow);
          Collections.swap(scrollBox.children, index, index - 1);
          recalculateNoise(noiseStepList, noiseStepData);
        }
      }
    };
    upButton.outlineWeight = .1f;
    upButton.color = new Color("#000000");
    upButton.outlineColor = new Color("#ffffff");
    upButton.minWidth = 1.6f;
    upButton.minHeight = 1.6f;
    upButton.mouseOverColor = new Color("#444444");
    orderCol.addChild(upButton);
    clickHandler.register(upButton);

    UIImage upIcon = new UIImage(upButton, ImageResources.littleArrow);
    upIcon.color = new Color("#ffffff");
    upIcon.minWidth = 1f;
    upIcon.minHeight = 1f;
    upIcon.padding = .3f;
    upButton.addChild(upIcon);

    UIBoxCol padding1 = new UIButton(orderCol);
    padding1.noBackground = true;
    padding1.outlineWeight = 0f;
    padding1.minHeight = .4f;
    orderCol.addChild(padding1);

    UIButton menuButton = new UIButton(orderCol){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        openNoiseStepOptionsModal(scrollBox, noiseStepList, noiseStepData, stepRow, () -> {
          recalculateNoise(noiseStepList, noiseStepData);
        });
      }
    };
    menuButton.outlineWeight = .1f;
    menuButton.color = new Color("#000000");
    menuButton.outlineColor = new Color("#ffffff");
    menuButton.minWidth = 1.6f;
    menuButton.minHeight = 1.6f;
    menuButton.mouseOverColor = new Color("#444444");
    orderCol.addChild(menuButton);
    clickHandler.register(menuButton);

    UIImage menuIcon = new UIImage(menuButton, ImageResources.menuIcon);
    menuIcon.color = new Color("#ffffff");
    menuIcon.minWidth = 1.4f;
    menuIcon.minHeight = 1.4f;
    menuIcon.padding = .1f;
    menuButton.addChild(menuIcon);

    UIBoxCol padding2 = new UIButton(orderCol);
    padding2.noBackground = true;
    padding2.outlineWeight = 0f;
    padding2.minHeight = .4f;
    orderCol.addChild(padding2);

    UIButton downButton = new UIButton(orderCol){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x,y);
        int index = noiseStepList.indexOf(noiseStepData);
        if (index < noiseStepList.size() - 1) {
          Collections.swap(noiseStepList, index, index + 1);
          index = scrollBox.children.indexOf(stepRow);
          Collections.swap(scrollBox.children, index, index + 1);
          recalculateNoise(noiseStepList, noiseStepList.get(index));
        }
      }
    };
    downButton.outlineWeight = .1f;
    downButton.color = new Color("#000000");
    downButton.outlineColor = new Color("#ffffff");
    downButton.minWidth = 1.6f;
    downButton.minHeight = 1.6f;
    downButton.mouseOverColor = new Color("#444444");
    orderCol.addChild(downButton);
    clickHandler.register(downButton);

    UIImage downIcon = new UIImage(downButton, ImageResources.littleArrow);
    downIcon.color = new Color("#ffffff");
    downIcon.minWidth = 1f;
    downIcon.minHeight = 1f;
    downIcon.padding = .3f;
    downIcon.rotation = 180f;
    downButton.addChild(downIcon);

    UIBoxCol infoCol = new UIBoxCol(stepRow);
    infoCol.outlineWeight = 0;
    infoCol.noBackground = true;
    stepRow.addChild(infoCol);

    UIBoxCol infoColPadding = new UIBoxCol(infoCol);
    infoColPadding.outlineWeight = 0;
    infoColPadding.noBackground = true;
    infoColPadding.minHeight = .1f;
    infoCol.addChild(infoColPadding);

    UISelectionMenu modifierTypeField = new UISelectionMenu(null, screen, clickHandler){
      @Override
      public String getValue() {
        switch (noiseStepData.modifier.getClass().getSimpleName()) {
          case "ImageAddModifier":
            return "Add Noise";
          case "ImageContrastModifier":
            return "Contrast";
          case "ImageInvertModifier":
            return "Invert";
          case "ImageMultModifier":
            return "Multiply Noise";
          case "ImageSubtractModifier":
            return "Subtract Noise";
          case "ImageScaleModifier":
            return "Scale";
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
          case "Add Noise":
            noiseStepData.modifier = new ImageAddModifier(noiseStepData.fnl);
            break;
          case "Contrast":
          noiseStepData.modifier = new ImageContrastModifier(1d, 0d);
            break;
          case "Invert":
          noiseStepData.modifier = new ImageInvertModifier();
            break;
          case "Multiply Noise":
          noiseStepData.modifier = new ImageMultModifier(noiseStepData.fnl);
            break;
          case "Subtract Noise":
          noiseStepData.modifier = new ImageSubtractModifier(noiseStepData.fnl);
            break;
          case "Scale":
          noiseStepData.modifier = new ImageScaleModifier(0d, 255d);
            break;
        }
        recalculateNoise(noiseStepList, noiseStepData);
      }
    };

    String[] modifierTypeFieldOptions = {
      "Add Noise",
      "Contrast",
      "Invert",
      "Multiply Noise",
      "Subtract Noise",
      "Scale"
    };

    modifierTypeField.padding = .2f;
    modifierTypeField.minWidth = 9.8f;

    EditorUtils.constructDropdown(infoCol, modifierTypeField, modifierTypeFieldOptions, null);

    UIButton settingsButton = new UIButton(infoCol){
      @Override
      public void mousedUp(float x, float y) {
        if(!noiseStepData.modifier.hasParameter()) {
          return;
        }
        super.mousedUp(x, y);
        openNoiseStepSettingsModal(scrollBox, noiseStepList, noiseStepData, stepRow, (IImageModifier newModifier) -> {
                                                                                                                  noiseStepData.modifier = newModifier;
                                                                                                                  recalculateNoise(noiseStepList, noiseStepData);
                                                                                                                });
      }
      @Override
      public void render() {
        if(noiseStepData.modifier.hasParameter()) {
          super.render();
        }
      }
    };
    settingsButton.minWidth = 2f;
    settingsButton.minHeight = 2f;
    settingsButton.color = new Color("000000");
    settingsButton.padding = .5f;
    settingsButton.outlineColor = new Color("#ffffff");
    settingsButton.outlineWeight = .1f;
    settingsButton.mouseOverColor = new Color("#444444");

    infoCol.addChild(settingsButton);
    clickHandler.register(settingsButton);

    UIImage settingsIcon = new UIImage(settingsButton, ImageResources.settingsIcon);
    settingsIcon.color = new Color("#ffffff");
    settingsIcon.minWidth = 1.5f;
    settingsIcon.minHeight = 1.5f;
    settingsIcon.padding = .25f;
    settingsButton.addChild(settingsIcon);

    UIButton noisePreviewButton = new UIButton(stepRow) {
      @Override
      public boolean isMouseOver(float x, float y) {
        if (!(noiseStepData.modifier instanceof ImageCombineModifier)) {
          return false;
        }
        return super.isMouseOver(x, y);
      }
      @Override
      public void mousedUp(float x, float y) {
        if (!(noiseStepData.modifier instanceof ImageCombineModifier)) {
          return;
        }
        super.mousedUp(x, y);
        NoiseModal.openNoiseModal(new FastNoiseLite(noiseStepData.fnl), (FastNoiseLite newFnl) -> {
                                                                                                   noiseStepData.fnl.Set(newFnl);
                                                                                                   recalculateNoise(noiseStepList, noiseStepData);
                                                                                                  });
      }
    };
    noisePreviewButton.mouseOverOutlineColor = new Color("#ffffff");
    noisePreviewButton.outlineColor = new Color("#ffffff");
    noisePreviewButton.noBackground = true;
    noisePreviewButton.outlineWeight = .1f;
    noisePreviewButton.minWidth = 6f;
    noisePreviewButton.minHeight = 6f;
    noisePreviewButton.radius = 3f;
    stepRow.addChild(noisePreviewButton);
    clickHandler.register(noisePreviewButton);

    UIGlobeDisplay grayGlobe = new UIGlobeDisplay(noisePreviewButton, noiseStepData.noisePreviewWrapper, null) {
      @Override
      public Color getColor() {
        if (noisePreviewButton.getIsMousedOver()) {
          return new Color("#bbbbbb");
        }
        else {
          return super.getColor();
        }
      }
    };
    grayGlobe.setRadius(5.8f);
    grayGlobe.padding = .1f;
    noisePreviewButton.addChild(grayGlobe);

    if (index >= 0 && index < scrollBox.children.size()) {
      scrollBox.addChild(stepRow, index);
    } else {
      scrollBox.addChild(stepRow);
    }

    recalculateNoise(noiseStepList, noiseStepData);
  }

  private static void openNoiseStepSettingsModal(UIScrollableBox scrollBox, ArrayList<NoiseStepData> noiseStepList, NoiseStepData noiseStepData, UIBoxRow stepRow, Consumer<IImageModifier> afterClose) {
    if(!noiseStepData.modifier.hasParameter()) {
      return;
    }

    UIModal noiseStepSettingsModal = new UIModal(screen);
    noiseStepSettingsModal.centerBox.color = new Color("#000000");
    noiseStepSettingsModal.centerBox.outlineColor = new Color("#ffffff");
    noiseStepSettingsModal.centerBox.outlineWeight = .1f;
    noiseStepSettingsModal.centerBox.radius = .5f;
    screen.addChild(noiseStepSettingsModal);
    clickHandler.setMask(noiseStepSettingsModal.centerBox);

    UIBoxCol contentBox = new UIBoxCol(noiseStepSettingsModal.centerBox);
    contentBox.noBackground = true;
    contentBox.outlineWeight = 0f;
    contentBox.minWidth = 5f;
    contentBox.minHeight = 5f;
    noiseStepSettingsModal.centerBox.addChild(contentBox);

    int index = noiseStepList.indexOf(noiseStepData);

    Wrapper<ImageResource> settingsPreviewWrapper = new Wrapper<ImageResource>();
    settingsPreviewWrapper.set(new ImageResource(noiseStepData.modifier.resolve(noiseStepList.get(index - 1).noisePreviewWrapper.get().getBufferedImage()), 1, 1000));

    UIBoxRow globeRow = new UIBoxRow(contentBox);
    globeRow.outlineWeight = 0;
    globeRow.noBackground = true;
    contentBox.addChild(globeRow);

    UIGlobeDisplay grayGlobe = new UIGlobeDisplay(globeRow, settingsPreviewWrapper, null);
    grayGlobe.setRadius(12);
    grayGlobe.padding = 1f;
    globeRow.addChild(grayGlobe);

    UIGlobeDisplay shadedGlobe = new UIGlobeDisplay(globeRow, settingsPreviewWrapper, editorPalette);
    shadedGlobe.setRadius(12);
    shadedGlobe.padding = 1f;
    globeRow.addChild(shadedGlobe);

    IImageModifier newNoiseStepModifier = noiseStepData.modifier.copy();
    newNoiseStepModifier.constructParameterFields(contentBox, clickHandler, keyboardHandler, noiseStepList.get(index - 1).noisePreviewWrapper.get().getBufferedImage(), settingsPreviewWrapper);

    UICenter modalButtonRowCenterer = new UICenter(contentBox);
    modalButtonRowCenterer.centerY = false;
    contentBox.addChild(modalButtonRowCenterer);

    UIBoxRow modalButtonRow = new UIBoxRow(modalButtonRowCenterer);
    modalButtonRow.outlineWeight = 0;
    modalButtonRow.noBackground = true;
    modalButtonRowCenterer.addChild(modalButtonRow);

    UIButton confirmButton = new UIButton(modalButtonRow){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        noiseStepSettingsModal.close();
        afterClose.accept(newNoiseStepModifier);
      }
    };
    confirmButton.color = new Color("#000000");
    confirmButton.outlineColor = new Color("#ffffff");
    confirmButton.outlineWeight = .1f;
    confirmButton.noBackground = false;
    confirmButton.padding = .5f;
    confirmButton.margin = .3f;
    confirmButton.mouseOverColor = new Color("00aa00");
    modalButtonRow.addChild(confirmButton);
    clickHandler.register(confirmButton);
    UITextBlock confirmButtonText = new UITextBlock(confirmButton, "Confirm", .5f);
    confirmButtonText.textColor = new Color("#ffffff");
    confirmButtonText.noBackground = false;
    confirmButton.addChild(confirmButtonText);

    UIButton cancelButton = new UIButton(modalButtonRow){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        noiseStepSettingsModal.close();
      }
    };
    cancelButton.color = new Color("#000000");
    cancelButton.outlineColor = new Color("#ffffff");
    cancelButton.outlineWeight = .1f;
    cancelButton.noBackground = false;
    cancelButton.padding = .5f;
    cancelButton.margin = .3f;
    cancelButton.mouseOverColor = new Color("aa0000");
    modalButtonRow.addChild(cancelButton);
    clickHandler.register(cancelButton);
    UITextBlock cancelButtonText = new UITextBlock(cancelButton, "Cancel", .5f);
    cancelButtonText.textColor = new Color("#ffffff");
    cancelButtonText.noBackground = false;
    cancelButton.addChild(cancelButtonText);

  }

  private static void openNoiseStepOptionsModal(UIScrollableBox scrollBox, ArrayList<NoiseStepData> noiseStepList, NoiseStepData noiseStepData, UIBoxRow stepRow, Runnable afterClose) {
    UIModal noiseStepOptionsModal = new UIModal(screen);
    noiseStepOptionsModal.centerBox.color = new Color("#000000");
    noiseStepOptionsModal.centerBox.outlineColor = new Color("#ffffff");
    noiseStepOptionsModal.centerBox.outlineWeight = .1f;
    noiseStepOptionsModal.centerBox.radius = .5f;
    screen.addChild(noiseStepOptionsModal);
    clickHandler.setMask(noiseStepOptionsModal.centerBox);

    UICenter duplicateButtonCenterer = new UICenter(noiseStepOptionsModal.centerBox);
    duplicateButtonCenterer.centerY = false;
    duplicateButtonCenterer.padding = .3f;
    noiseStepOptionsModal.addChild(duplicateButtonCenterer);

    UIButton duplicateButton = new UIButton(duplicateButtonCenterer){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        noiseStepOptionsModal.close();
        NoiseStepData newNoiseStepData = new NoiseStepData(noiseStepData);
        int index = noiseStepList.indexOf(noiseStepData);
        addNoiseStep(scrollBox, noiseStepList, newNoiseStepData, index + 1);
        afterClose.run();
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

    UICenter removeButtonCenterer = new UICenter(noiseStepOptionsModal.centerBox);
    removeButtonCenterer.centerY = false;
    removeButtonCenterer.padding = .3f;
    noiseStepOptionsModal.addChild(removeButtonCenterer);

    UIButton removeButton = new UIButton(removeButtonCenterer){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        noiseStepOptionsModal.close();
        int index = noiseStepList.indexOf(noiseStepData);
        noiseStepList.remove(index);
        index = scrollBox.children.indexOf(stepRow);
        scrollBox.children.remove(index);
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

    UICenter closeButtonCenterer = new UICenter(noiseStepOptionsModal.centerBox);
    closeButtonCenterer.centerY = false;
    closeButtonCenterer.padding = .3f;
    noiseStepOptionsModal.addChild(closeButtonCenterer);

    UIButton closeButton = new UIButton(closeButtonCenterer){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.clearMask();
        noiseStepOptionsModal.close();
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
        addNoiseStep(scrollableContent, noiseStepList);
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

}
