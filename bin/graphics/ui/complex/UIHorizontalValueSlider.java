package bin.graphics.ui.complex;

import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIElement;

import bin.graphics.objects.Global;
import bin.graphics.objects.Pointer;
import bin.graphics.objects.PointerOutline;

import java.lang.UnsupportedOperationException;

public class UIHorizontalValueSlider extends UIButton {

   public float maxValue;
   public float minValue;
   public String facing;

   protected float deltaX;

   public UIHorizontalValueSlider(UIElement parent) {
      super(parent);
      this.maxValue = 0;
      this.minValue = 0;
      this.facing = "up";
      this.deltaX = 0;
   }

   public float getValue() {
     throw new UnsupportedOperationException();
   }

   public void setValue(float value) {
     throw new UnsupportedOperationException();
   }

   public float valueToX(float value) {
     return this.getX() + this.getWidth() * (value - this.minValue) / (this.maxValue - this.minValue);
   }

   public float xToValue(float x) {
     return (x - this.getX()) / this.getWidth() * (this.maxValue - this.minValue) + this.minValue;
   }

   public float valueToXRelative(float value) {
     throw new UnsupportedOperationException();
   }

   @Override
   public float getHeight() {
     return this.maxHeight + this.getChildWidth() + this.getChildHeight();
   }

   @Override
   public float getWidth() {
     return this.maxWidth;
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
       throw new IndexOutOfBoundsException("UIHorizontalValueSlider does not contain " + i + " children.");
     }
     return this.valueToX(this.getValue()) + this.getChildWidth() / 2;
   }

   @Override
   public float getChildY(int i) {
     if (i >= this.children.size() || i < 0) {
       throw new IndexOutOfBoundsException("UIHorizontalValueSlider does not contain " + i + " children.");
     }
     return this.getY();
   }

   @Override
   public void render() {
     if (!this.getNoBackground()) {
       Global.drawColor(this.getColor());
       Pointer.draw(this.valueToX(this.getValue()), this.getY() - this.getHeight() / 2, this.getHeight(), this.getChildWidth(), this.facing);
     }
     if (this.getOutlineWeight() > 0f) {
       Global.drawColor(this.getOutlineColor());
       PointerOutline.draw(this.valueToX(this.getValue()), this.getY() - this.getHeight() / 2, this.getHeight(), this.getChildWidth(), this.facing, this.getOutlineWeight());
     }
     for (int i = 0; i < this.children.size(); i++) {
       this.children.get(i).render();
     }
   }

   @Override
   public boolean isMouseOver(float x, float y) {
     return (x >= this.valueToX(this.getValue()) - this.getChildWidth() / 2 && x <= this.valueToX(this.getValue()) + this.getChildWidth() / 2 && y >= this.getY() - this.getHeight() && y <= this.getY()) && (this.parent == null || this.parent.allowChildContent(x, y));
   }

   @Override
   public void mousedDown(float x, float y) {
     if(!this.getIsMouseDown()) {
       this.setIsMouseDown(true);
       this.deltaX = x - this.valueToX(this.getValue());
     }
   }

   @Override
   public void mouseMoved(float x, float y) {
     if (this.getIsMouseDown()) {
       this.setValue(Math.max(Math.min(this.xToValue(x - this.deltaX), this.maxValue), this.minValue));
     }
   }

}
