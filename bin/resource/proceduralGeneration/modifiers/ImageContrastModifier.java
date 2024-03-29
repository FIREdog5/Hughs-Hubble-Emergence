package bin.resource.proceduralGeneration.modifiers;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.graphics.ui.UIBoxCol;
import bin.resource.ImageResource;

import java.awt.image.BufferedImage;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImagePreprocessor;

import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.complex.UINumberInput;

import bin.editor.EditorUtils;

public class ImageContrastModifier implements IImageModifier {
  private float scale;
  private float offset;
  public ImageContrastModifier(double scale, double offset) {
    this.scale = (float)scale;
    this.offset = (float)offset;
  }

  @Override
  public IImageModifier copy() {
    return new ImageContrastModifier(this.scale, this.offset);
  }

  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImagePreprocessor.adjustContrast(image, this.scale, this.offset);
  }

  // @Override
  // public Parameter[] getParameters() {
  //   Parameter[] parameters = {new Parameter("Scale (Contrast)", 0d, 10d), new Parameter("Offset (Brightness)", -255d, 255d)};
  //   return parameters;
  // }

  @Override
  public boolean hasParameter() {
    return true;
  }

  @Override
  public Runnable constructParameterFields(UIBoxCol container, ClickHandler clickHandler, KeyboardHandler keyboardHandler, BufferedImage imageIn, Wrapper<ImageResource> imageWrapper) {
    Wrapper<String> scaleStringWrapper = new Wrapper<String>(Float.toString(scale));
    UIBoxRow scaleFieldRow = new UIBoxRow(null);
    UINumberInput scaleField = new UINumberInput(scaleFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = scale;
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          scale = Float.parseFloat(val);
          imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return scaleStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        scaleStringWrapper.set(val);
        return true;
      }
    };
    scaleField.minValue = 0f;
    scaleField.maxValue = 10f;
    scaleField.incrementAmount = .01f;
    scaleField.precision = 2;

    EditorUtils.constructNumField(container, scaleFieldRow, scaleField, "Contrast [0 - 10]");

    Wrapper<String> offsetStringWrapper = new Wrapper<String>(Float.toString(offset));
    UIBoxRow offsetFieldRow = new UIBoxRow(null);
    UINumberInput offsetField = new UINumberInput(offsetFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = offset;
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          offset = Float.parseFloat(val);
          imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return offsetStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        offsetStringWrapper.set(val);
        return true;
      }
    };
    offsetField.minValue = -255f;
    offsetField.maxValue = 255f;
    offsetField.incrementAmount = 1f;
    offsetField.precision = 0;

    EditorUtils.constructNumField(container, offsetFieldRow, offsetField, "Brightness [-255 - 255]");

    return null;
  }
}
