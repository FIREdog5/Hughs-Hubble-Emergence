package bin.graphics.ui.complex;

import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIElement;

import bin.graphics.objects.Global;
import bin.graphics.objects.Pointer;
import bin.graphics.objects.PointerOutline;

import java.lang.UnsupportedOperationException;

public class UIVerticalValueSlider extends UIButton {

   public float maxValue;
   public float minValue;
   public String facing;

   protected float deltaY;

   public UIVerticalValueSlider(UIElement parent) {
      super(parent);
      this.maxValue = 0;
      this.minValue = 0;
      this.facing = "right";
      this.deltaY = 0;
   }

   public float getValue() {
     throw new UnsupportedOperationException();
   }

   public void setValue(float value) {
     throw new UnsupportedOperationException();
   }

   protected float valueToY(float value) {
     return this.getY() - this.getHeight() * (value - this.minValue) / (this.maxValue - this.minValue);
   }

   protected float yToValue(float y) {
     return (this.getY() - y) / this.getHeight() * (this.maxValue - this.minValue) + this.minValue;
   }

   @Override
   public float getHeight() {
     return this.maxHeight;
   }

   @Override
   public float getWidth() {
     return this.maxWidth + this.getChildWidth();
   }

   protected float getChildHeight() {
     return super.getHeight();
   }

   protected float getChildWidth() {
     return super.getWidth();
   }

   @Override
   public float getChildX(int i) {
     if (i >= this.children.size() || i < 0) {
       throw new IndexOutOfBoundsException("UIVerticalValueSlider does not contain " + i + " children.");
     }
     return this.getX();
   }

   @Override
   public float getChildY(int i) {
     if (i >= this.children.size() || i < 0) {
       throw new IndexOutOfBoundsException("UIVerticalValueSlider does not contain " + i + " children.");
     }
     return this.valueToY(this.getValue()) + this.getChildHeight() / 2;
   }

   @Override
   public void render() {
     if (!this.noBackground) {
       Global.drawColor(this.getColor());
       Pointer.draw(this.getX() + this.getWidth() - this.maxWidth / 2, this.valueToY(this.getValue()), this.maxWidth, this.getChildHeight(), this.facing);
     }
     if (this.outlineWeight > 0f) {
       Global.drawColor(this.getOutlineColor());
       PointerOutline.draw(this.getX() + this.getWidth() - this.maxWidth / 2, this.valueToY(this.getValue()), this.maxWidth, this.getChildHeight(), this.facing, this.outlineWeight);
     }
     for (int i = 0; i < this.children.size(); i++) {
       this.children.get(i).render();
     }
   }

   @Override
   public boolean isMouseOver(float x, float y) {
     return x >= this.getX() + (this.getWidth() - this.getChildWidth() - this.maxWidth) && x <= this.getX() + this.getWidth() && y <= this.valueToY(this.getValue()) + this.getChildHeight() / 2 && y >= this.valueToY(this.getValue()) - this.getChildHeight() / 2;
   }

   @Override
   public void mousedDown(float x, float y) {
     if(!this.getIsMouseDown()) {
       this.setIsMouseDown(true);
       this.deltaY = y - this.valueToY(this.getValue());
     }
   }

   @Override
   public void mouseMoved(float x, float y) {
     if (this.getIsMouseDown()) {
       this.setValue(Math.max(Math.min(this.yToValue(y - this.deltaY), this.maxValue), this.minValue));
     }
   }

}
