package bin.graphics.ui.complex;

import bin.graphics.ui.UIElement;
import bin.resource.ImageResources;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIImage;
import bin.input.ClickHandler;
import bin.graphics.Color;

public abstract class UINumberInput extends UITextInput{

  public float incrementAmount;
  public float maxValue;
  public float minValue;
  public int precision;
  public UIBoxRow outerRow;
  public UIButton incrementButton;
  public UIButton decrementButton;
  public UIImage incrementIcon;
  public UIImage decrementIcon;

  public UINumberInput(UIElement parent, float size, ClickHandler clickHandler) {
    super(null, size);
    this.incrementAmount = 0.1f;
    this.maxValue = 100;
    this.minValue = 0;
    this.precision = 1;

    UINumberInput ref = this;

    outerRow = new UIBoxRow(parent){
      @Override
      public float getPadding() {
        return ref.padding;
      }

      @Override
      public float getMargin() {
        return ref.margin;
      }
    };
    outerRow.outlineWeight = 0;
    outerRow.noBackground = true;

    parent.addChild(outerRow);

    this.parent = outerRow;
    outerRow.addChild(this);

    UIBoxCol buttonCol = new UIBoxCol(outerRow);
    buttonCol.outlineWeight = 0;
    buttonCol.noBackground = true;

    outerRow.addChild(buttonCol);

    incrementButton = new UIButton(buttonCol) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        String content = ref.getValue();
        float val;
        ref.select();
        ref.blockDeselect();
        try {
          val = Float.parseFloat(content) + ref.incrementAmount;
        } catch (Exception e) {
          val = ref.incrementAmount;
        }
        val = Math.round(val * (float) Math.pow(10, ref.precision)) / (float) Math.pow(10, ref.precision);
        if (ref.precision == 0) {
          setUnfilteredValue(Integer.toString((int)val));
        } else {
          setUnfilteredValue(Float.toString(val));
        }
      }

      @Override
      public float getOutlineWeight() {
        return ref.getOutlineWeight();
      }

      @Override
      public boolean getNoBackground() {
        return ref.getNoBackground();
      }

      @Override
      public Color getColor() {
        if (this.getIsMousedOver() && !this.getIsMouseDown()) {
          return super.getColor();
        } else {
          return ref.getColor();
        }
      }

      @Override
      public Color getOutlineColor() {
        if (this.getIsMousedOver() && !this.getIsMouseDown()) {
          return super.getOutlineColor();
        } else {
          return ref.getOutlineColor();
        }
      }

    };

    buttonCol.addChild(incrementButton);
    clickHandler.register(incrementButton);

    incrementIcon = new UIImage(incrementButton, ImageResources.littleArrow);
    incrementButton.addChild(incrementIcon);

    decrementButton = new UIButton(buttonCol) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        String content = ref.getValue();
        float val;
        ref.select();
        ref.blockDeselect();
        try {
          val = Float.parseFloat(content) - ref.incrementAmount;
        } catch (Exception e) {
          val = -1 * ref.incrementAmount;
        }
        val = Math.round(val * (float) Math.pow(10, ref.precision)) / (float) Math.pow(10, ref.precision);
        if (ref.precision == 0) {
          setUnfilteredValue(Integer.toString((int)val));
        } else {
          setUnfilteredValue(Float.toString(val));
        }
      }

      @Override
      public float getOutlineWeight() {
        return ref.getOutlineWeight();
      }

      @Override
      public boolean getNoBackground() {
        return ref.getNoBackground();
      }

      @Override
      public Color getColor() {
        if (this.getIsMousedOver() && !this.getIsMouseDown()) {
          return super.getColor();
        } else {
          return ref.getColor();
        }
      }

      @Override
      public Color getOutlineColor() {
        if (this.getIsMousedOver() && !this.getIsMouseDown()) {
          return super.getOutlineColor();
        } else {
          return ref.getOutlineColor();
        }
      }
    };

    buttonCol.addChild(decrementButton);
    clickHandler.register(decrementButton);

    decrementIcon = new UIImage(decrementButton, ImageResources.littleArrow);
    decrementIcon.rotation = 180f;
    decrementButton.addChild(decrementIcon);

  }

  @Override
  public float getPadding() {
    return 0;
  }

  @Override
  public float getMargin() {
    return 0;
  }

  @Override
  public boolean setUnfilteredValue(String newValue) {
    if (newValue.length() > 0 && (newValue.substring(newValue.length() - 1).equals(" ") || newValue.substring(0, 1).equals(" "))) {
      return false;
    }
    if (newValue.equals("") || newValue.equals("-")) {
      return setValue(newValue);
    }
    if (newValue.contains(".") && newValue.chars().filter(ch -> ch == '.').count() == 1 && newValue.substring(newValue.length() - 1).equals(".")) {
      try {
        float val = Float.parseFloat(newValue.split("\\.")[0]);
      } catch (Exception e) {
        return false;
      }
      return setValue(newValue);
    }
    try {
      float val = Float.parseFloat(newValue);
      String prefix = "";
      if (val >= 0 && newValue.length() >= 1 && newValue.substring(0, 1).equals("-")) {
        prefix = "-";
      }
      if (newValue.contains(".")) {
        int precision = newValue.split("\\.")[1].length();
        if (precision > this.precision) {
          return false;
        }
      }
      if (newValue.contains(".") && newValue.split("\\.").length > 1 && newValue.split("\\.")[1].length() > 0 && this.precision != 0) {
        return setValue(prefix + Float.toString(val));
      } else {
        return setValue(prefix + Integer.toString((int)val));
      }
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void onDeselect() {
    super.onDeselect();
    String content = this.getValue();
    try {
      float val = Float.parseFloat(content);
      val = Math.max(Math.min(this.maxValue, val), this.minValue);
      if (content.contains(".") && content.split("\\.").length > 1 && content.split("\\.")[1].length() > 0 && this.precision != 0) {
        setValue(Float.toString(val));
      } else {
        setValue(Integer.toString((int)val));
      }
    } catch (Exception e) {
      setValue("0");
    }
  }
}
