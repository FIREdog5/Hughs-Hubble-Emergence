package bin.graphics.ui.colorUtils;

import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIElement;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.complex.UIVerticalValueSlider;
import bin.graphics.Color;
import bin.input.ClickHandler;

public class UIColorSlider extends UIBoxRow {

  public UIVerticalValueSlider slider;
  public UIBoxCol displayBar;

  public UIColorSlider(UIElement parent, Color colorData, ClickHandler clickHandler, String val) {
    super(parent);
    this.slider = new UIVerticalValueSlider(this){

      private boolean wasHigh = false;

      @Override
      public float getValue() {
        switch(val) {
          case "red":
            return colorData.getRed();
          case "green":
            return colorData.getGreen();
          case "blue":
            return colorData.getBlue();
          case "hue":
            float[] hsv = colorData.getHSV();
            if (hsv[0] == 0 && wasHigh) {
              return (float) Math.PI * 2;
            }
            return colorData.getHSV()[0];
          case "saturation":
            return colorData.getHSV()[1];
          case "value":
            return colorData.getHSV()[2];
        }
        return 0;
      }

      @Override
      public void setValue(float value) {
        float[] hsv;
        switch(val) {
          case "red":
            colorData.setRed(value);
            return;
          case "green":
            colorData.setGreen(value);
            return;
          case "blue":
            colorData.setBlue(value);
            return;
          case "hue":
            hsv = colorData.getHSV();
            hsv[0] = value;
            wasHigh = value > Math.PI;
            if(hsv[1] == 0)
            {
              hsv[1] = .000001f;
            }
            colorData.setHSV(hsv);
            return;
          case "saturation":
            hsv = colorData.getHSV();
            hsv[1] = value;
            colorData.setHSV(hsv);
            return;
          case "value":
            hsv = colorData.getHSV();
            hsv[2] = value;
            colorData.setHSV(hsv);
            return;
        }
      }

      @Override
      public boolean isMouseOver(float x, float y) {
        return ((x >= this.getX() && x <= this.getX() + this.getWidth() && y <= this.valueToY(this.getValue()) + this.getChildHeight() / 2 && y >= this.valueToY(this.getValue()) - this.getChildHeight() / 2) || (x >= displayBar.getX() && x <= displayBar.getX() + displayBar.getWidth() && y <= displayBar.getY() && y >= displayBar.getY() - displayBar.getHeight())) && (this.parent == null || this.parent.allowChildContent(x, y));
      }

      @Override
      public void mousedDown(float x, float y) {
        super.mousedDown(x, y);
        if ((x >= displayBar.getX() && x <= displayBar.getX() + displayBar.getWidth() && y <= displayBar.getY() && y >= displayBar.getY() - displayBar.getHeight())) {
          this.deltaY = 0;
        }
      }

    };
    if (val.equals("hue")) {
      this.slider.maxValue = (float)Math.PI * 2;
    } else {
      this.slider.maxValue = 1;
    }
    this.slider.minValue = 0;

    this.slider.minHeight = .5f;
    this.slider.maxWidth = .5f;
    this.slider.minWidth = 0;
    this.slider.maxHeight = 5;

    this.addChild(slider);
    clickHandler.register(slider);

    if (val.equals("hue")) {
      this.displayBar = new UIHueColorBar(this);
      this.displayBar.minWidth = 1;
      this.displayBar.minHeight = 5;
      this.addChild(displayBar);
    } else {
      Color topColor = new Color(colorData){
        @Override
        public void transformRef() {
          super.transformRef();
          float[] hsv;
          switch(val) {
            case "red":
            this.setRed(0);
            return;
            case "green":
            this.setGreen(0);
            return;
            case "blue":
            this.setBlue(0);
            return;
            case "hue":
            hsv = colorData.getHSV();
            hsv[0] = 0;
            this.setHSV(hsv);
            return;
            case "saturation":
            hsv = colorData.getHSV();
            hsv[1] = 0;
            this.setHSV(hsv);
            return;
            case "value":
            hsv = colorData.getHSV();
            hsv[2] = 0;
            this.setHSV(hsv);
            return;
          }
        }
      };

      Color bottomColor = new Color(colorData){
        @Override
        public void transformRef() {
          super.transformRef();
          float[] hsv;
          switch(val) {
            case "red":
            this.setRed(1);
            return;
            case "green":
            this.setGreen(1);
            return;
            case "blue":
            this.setBlue(1);
            return;
            case "hue":
            hsv = colorData.getHSV();
            hsv[0] = 1;
            this.setHSV(hsv);
            return;
            case "saturation":
            hsv = colorData.getHSV();
            hsv[1] = 1;
            this.setHSV(hsv);
            return;
            case "value":
            hsv = colorData.getHSV();
            hsv[2] = 1;
            this.setHSV(hsv);
            return;
          }
        }
      };

      this.displayBar = new UIColorBar(this, new Color[] {bottomColor, bottomColor, topColor, topColor});
      this.displayBar.minWidth = 1;
      this.displayBar.minHeight = 5;
      this.addChild(displayBar);
    }

  }

  @Override
  public void render() {
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }

}
