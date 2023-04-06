package bin.resource.proceduralGeneration.modifiers;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.graphics.ui.UIBoxCol;

import java.awt.image.BufferedImage;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImagePreprocessor;
import bin.resource.ImageResource;

import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.complex.UINumberInput;

import bin.editor.EditorUtils;

public class ImageScaleModifier implements IImageModifier {
  private float minScaleValue;
  private float maxScaleValue;
  public ImageScaleModifier(double minScaleValue, double maxScaleValue) {
    this.minScaleValue = (float)minScaleValue;
    this.maxScaleValue = (float)maxScaleValue;
  }

  @Override
  public IImageModifier copy() {
    return new ImageScaleModifier(this.minScaleValue, this.maxScaleValue);
  }

  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImagePreprocessor.normalizeWithBounds(image, this.minScaleValue, this.maxScaleValue);

  }

  // @Override
  // public Parameter[] getParameters() {
  //   Parameter[] parameters = {new Parameter("Min value", 0d, 255d), new Parameter("Max value", 0d, 255d)};
  //   return parameters;
  // }

  @Override
  public boolean hasParameter() {
    return true;
  }

  @Override
  public Runnable constructParameterFields(UIBoxCol container, ClickHandler clickHandler, KeyboardHandler keyboardHandler, BufferedImage imageIn, Wrapper<ImageResource> imageWrapper) {
    Wrapper<String> minScaleValueStringWrapper = new Wrapper<String>(Float.toString(minScaleValue));
    UIBoxRow minScaleValueFieldRow = new UIBoxRow(null);
    UINumberInput minScaleValueField = new UINumberInput(minScaleValueFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = minScaleValue;
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          minScaleValue = Float.parseFloat(val);
          if (minScaleValue > maxScaleValue) {
            minScaleValue = maxScaleValue;
          }
          imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return minScaleValueStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        minScaleValueStringWrapper.set(val);
        return true;
      }
    };
    minScaleValueField.minValue = 0f;
    minScaleValueField.maxValue = 255f;
    minScaleValueField.incrementAmount = 1f;
    minScaleValueField.precision = 0;

    EditorUtils.constructNumField(container, minScaleValueFieldRow, minScaleValueField, "Lower Bound");

    Wrapper<String> maxScaleValueStringWrapper = new Wrapper<String>(Float.toString(maxScaleValue));
    UIBoxRow maxScaleValueFieldRow = new UIBoxRow(null);
    UINumberInput maxScaleValueField = new UINumberInput(maxScaleValueFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = maxScaleValue;
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          maxScaleValue = Float.parseFloat(val);
          if (maxScaleValue < minScaleValue) {
            maxScaleValue = minScaleValue;
          }
          imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return maxScaleValueStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        maxScaleValueStringWrapper.set(val);
        return true;
      }
    };
    maxScaleValueField.minValue = 0f;
    maxScaleValueField.maxValue = 255f;
    maxScaleValueField.incrementAmount = 1f;
    maxScaleValueField.precision = 0;

    EditorUtils.constructNumField(container, maxScaleValueFieldRow, maxScaleValueField, "Upper Bound");

    return null;
  }
}
