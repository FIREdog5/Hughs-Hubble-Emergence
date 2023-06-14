package bin.resource.proceduralGeneration.modifiers;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.Wrapper;
import bin.Pair;
import bin.resource.ImageResource;
import bin.resource.ImageResources;
import bin.graphics.Color;
import bin.editor.Editor;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.proceduralGeneration.Parameter;
import bin.resource.ImagePreprocessor;

import bin.graphics.objects.Global;
import bin.graphics.objects.Pointer;
import bin.graphics.objects.PointerOutline;
import bin.graphics.objects.Line;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIElement;
import bin.graphics.ui.UIBoxLayered;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIImage;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.complex.UINumberInput;
import bin.graphics.ui.complex.UIHorizontalValueSlider;

import bin.editor.EditorUtils;

public class ImageRemapModifier implements IImageModifier {
  private ArrayList<Pair<Integer, Integer>> mappings = new ArrayList<Pair<Integer, Integer>>();
  public ImageRemapModifier() {
    mappings.add(new Pair<Integer, Integer>(0,0));
    mappings.add(new Pair<Integer, Integer>(255,255));
  }

  public ImageRemapModifier(ArrayList<Pair<Integer, Integer>> mappings) {
    for (int i = 0; i < mappings.size(); i++) {
      Pair<Integer, Integer> original = mappings.get(i);
      Pair<Integer, Integer> copy = new Pair<Integer, Integer>(original.getKey(), original.getValue());
      this.mappings.add(copy);
    }
  }

  @Override
  public IImageModifier copy() {
    return new ImageRemapModifier(mappings);
  }

  @Override
  public BufferedImage resolve(BufferedImage image) {
    return ImagePreprocessor.remap(image, mappings);
  }

  @Override
  public boolean hasParameter() {
    return true;
  }

  @Override
  public Runnable constructParameterFields(UIBoxCol container, ClickHandler clickHandler, KeyboardHandler keyboardHandler, BufferedImage imageIn, Wrapper<ImageResource> imageWrapper) {
    UIBoxCol canvas = new UIBoxCol(container);
    canvas.color = new Color("#000000");
    canvas.outlineColor = new Color("#ffffff");
    canvas.outlineWeight = .1f;
    canvas.padding = .5f;
    canvas.margin = .5f;
    container.addChild(canvas);

    UIBoxLayered topSliderContainer = new UIBoxLayered(canvas);
    topSliderContainer.outlineWeight = 0;
    topSliderContainer.noBackground = true;
    topSliderContainer.minWidth = 3.4f;

    UIBoxLayered bottomSliderContainer = new UIBoxLayered(canvas);
    bottomSliderContainer.outlineWeight = 0;
    bottomSliderContainer.noBackground = true;
    bottomSliderContainer.minWidth = 3.4f;
    bottomSliderContainer.y = 10f;

    ImageRemapModifier ref = this;
    UIButton addNewSliderButton = new UIButton(canvas) {
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        if (ref.mappings.size() == 256) {
          return;
        }

        for (int i = 0; i < ref.mappings.size() - 1; i++) {
          Pair<Integer, Integer> lower = ref.mappings.get(i);
          Pair<Integer, Integer> upper = ref.mappings.get(i + 1);
          if (upper.getKey() - lower.getKey() > 1 && upper.getValue() - lower.getValue() > 1) {
            Pair<Integer, Integer> newMapping = new Pair<Integer, Integer>(lower.getKey() + 1, lower.getValue() + 1);
            ref.mappings.add(i + 1, newMapping);
            ref.addMappingSliders(topSliderContainer, bottomSliderContainer, clickHandler, newMapping, imageIn, imageWrapper);
            imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
            break;
          }
        }
      }
    };
    addNewSliderButton.mouseOverColor = new Color("#00aa00");
    addNewSliderButton.outlineWeight = .1f;
    addNewSliderButton.padding = .5f;
    canvas.addChild(addNewSliderButton);
    clickHandler.register(addNewSliderButton);

    UIImage addIcon = new UIImage(addNewSliderButton, ImageResources.addIcon);
    addIcon.minWidth = 1f;
    addIcon.minHeight = 1f;
    addIcon.padding = .25f;
    addNewSliderButton.addChild(addIcon);

    UIImage blankGradientPreview = new UIImage(canvas, ImageResources.blankGradient);
    blankGradientPreview.minWidth = 1f;
    blankGradientPreview.minHeight = 50f;
    blankGradientPreview.rotation = 270f;
    blankGradientPreview.useAA = false;
    canvas.addChild(blankGradientPreview);

    canvas.addChild(topSliderContainer);
    canvas.addChild(bottomSliderContainer);

    UIImage gradientPreview = new UIImage(canvas, Editor.getPalettResourceWrapper());
    gradientPreview.minWidth = 1f;
    gradientPreview.minHeight = 50f;
    gradientPreview.rotation = 270f;
    gradientPreview.useAA = false;
    canvas.addChild(gradientPreview);

    for (int i = 0; i < mappings.size(); i++) {
      Pair<Integer, Integer> mapping = mappings.get(i);
      addMappingSliders(topSliderContainer, bottomSliderContainer, clickHandler, mapping, imageIn, imageWrapper);
    }

    return null;
  }

  public void addMappingSliders(UIElement topParent, UIElement bottomParent, ClickHandler clickHandler, Pair<Integer, Integer> newMapping, BufferedImage imageIn, Wrapper<ImageResource> imageWrapper) {
    ArrayList<Pair<Integer, Integer>> mappings = this.mappings;
    UIHorizontalValueSlider topSlider = new UIHorizontalValueSlider(topParent){
      private int oldValue = 0;

      @Override
      public float getValue() {
        this.oldValue = newMapping.getKey();
        return newMapping.getKey();
      }

      @Override
      public float getChildX(int i) {
        if (i >= this.children.size() || i < 0) {
          throw new IndexOutOfBoundsException("UIHorizontalValueSlider does not contain " + i + " children.");
        }
        return this.valueToX(this.getValue()) + this.getChildWidth() / 2 + this.valueToXRelative(.5f);
      }

      public float valueToXRelative(float value) {
        return this.getWidth() * (value - this.minValue) / (this.maxValue - this.minValue);
      }

      @Override
      public void render() {
        if (!this.noBackground) {
          Global.drawColor(this.getColor());
          Pointer.draw(this.valueToX(this.getValue()) + this.valueToXRelative(.5f), this.getY() - this.getHeight() / 2, this.getHeight(), this.getChildWidth(), this.facing);
        }
        if (this.outlineWeight > 0f) {
          Global.drawColor(this.getOutlineColor());
          PointerOutline.draw(this.valueToX(this.getValue()) + this.valueToXRelative(.5f), this.getY() - this.getHeight() / 2, this.getHeight(), this.getChildWidth(), this.facing, this.outlineWeight);
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
      public void mouseMoved(float x, float y) {
        if (this.getIsMouseDown()) {
          this.setValue(this.xToValue(x - this.deltaX));
        }
      }

      @Override
      public void setValue(float value) {
        boolean taken = false;
        int correctedValue = (int) value;
        int index = mappings.indexOf(newMapping);
        if (index - 1 >= 0) {
          Pair<Integer, Integer> position = mappings.get(index - 1);
          if (correctedValue == (Integer)position.getKey()) {
              taken = true;
            }
          if (correctedValue < (Integer)position.getKey() && this.oldValue > (Integer)position.getKey()) {
            this.oldValue = (Integer)position.getKey() + 1;
            taken = true;
          } else if (correctedValue > (Integer)position.getKey() && this.oldValue < (Integer)position.getKey()) {
            this.oldValue = (Integer)position.getKey() - 1;
            taken = true;
          }
        }
        if (index + 1 < mappings.size() && !taken) {
          Pair<Integer, Integer> position = mappings.get(index + 1);
          if (correctedValue == (Integer)position.getKey()) {
              taken = true;
            }
          if (correctedValue < (Integer)position.getKey() && this.oldValue > (Integer)position.getKey()) {
            this.oldValue = (Integer)position.getKey() + 1;
            taken = true;
          } else if (correctedValue > (Integer)position.getKey() && this.oldValue < (Integer)position.getKey()) {
            this.oldValue = (Integer)position.getKey() - 1;
            taken = true;
          }
        }
        correctedValue = (int)Math.max((Math.min(correctedValue, 255)), 0);
        if (taken) {
          newMapping.set((int) this.oldValue, newMapping.getValue());
        } else {
          newMapping.set(correctedValue, newMapping.getValue());
        }
        imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
      }
    };
    topSlider.y = 0;
    topSlider.minValue = 0;
    topSlider.maxValue = 256;
    topSlider.minWidth = .5f;
    topSlider.maxHeight = .3f;
    topSlider.maxHeight = .3f;
    topSlider.maxWidth = 50f;
    topSlider.color = new Color("#000000");
    topSlider.mouseOverColor = new Color("#777777");
    topSlider.outlineColor = new Color("#ffffff");
    topSlider.outlineWeight = .1f;

    topParent.addChild(topSlider);
    clickHandler.register(topSlider);

    UIHorizontalValueSlider bottomSlider = new UIHorizontalValueSlider(bottomParent){
      private int oldValue = 0;

      @Override
      public float getValue() {
        this.oldValue = newMapping.getValue();
        return newMapping.getValue();
      }

      @Override
      public float getChildX(int i) {
        if (i >= this.children.size() || i < 0) {
          throw new IndexOutOfBoundsException("UIHorizontalValueSlider does not contain " + i + " children.");
        }
        return this.valueToX(this.getValue()) + this.getChildWidth() / 2 + this.valueToXRelative(.5f);
      }

      public float valueToXRelative(float value) {
        return this.getWidth() * (value - this.minValue) / (this.maxValue - this.minValue);
      }

      @Override
      public void render() {
        if (!this.noBackground) {
          Global.drawColor(this.getColor());
          Pointer.draw(this.valueToX(this.getValue()) + this.valueToXRelative(.5f), this.getY() - this.getHeight() / 2, this.getHeight(), this.getChildWidth(), this.facing);
        }
        if (this.outlineWeight > 0f) {
          Global.drawColor(this.getOutlineColor());
          PointerOutline.draw(this.valueToX(this.getValue()) + this.valueToXRelative(.5f), this.getY() - this.getHeight() / 2, this.getHeight(), this.getChildWidth(), this.facing, this.outlineWeight);
          Line.draw(this.valueToX(this.getValue()) + this.valueToXRelative(.5f), this.getY(), topSlider.valueToX(topSlider.getValue()) + topSlider.valueToXRelative(.5f), topSlider.getY() - topSlider.getHeight(), this.outlineWeight);
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
      public void mouseMoved(float x, float y) {
        if (this.getIsMouseDown()) {
          this.setValue(this.xToValue(x - this.deltaX));
        }
      }

      @Override
      public void setValue(float value) {
        boolean taken = false;
        int correctedValue = (int) value;
        int index = mappings.indexOf(newMapping);
        if (index - 1 >= 0) {
          Pair<Integer, Integer> position = mappings.get(index - 1);
          if (correctedValue == (Integer)position.getValue()) {
              taken = true;
            }
          if (correctedValue < (Integer)position.getValue() && this.oldValue > (Integer)position.getValue()) {
            this.oldValue = (Integer)position.getValue() + 1;
            taken = true;
          } else if (correctedValue > (Integer)position.getValue() && this.oldValue < (Integer)position.getValue()) {
            this.oldValue = (Integer)position.getValue() - 1;
            taken = true;
          }
        }
        if (index + 1 < mappings.size() && !taken) {
          Pair<Integer, Integer> position = mappings.get(index + 1);
          if (correctedValue == (Integer)position.getValue()) {
              taken = true;
            }
          if (correctedValue < (Integer)position.getValue() && this.oldValue > (Integer)position.getValue()) {
            this.oldValue = (Integer)position.getValue() + 1;
            taken = true;
          } else if (correctedValue > (Integer)position.getValue() && this.oldValue < (Integer)position.getValue()) {
            this.oldValue = (Integer)position.getValue() - 1;
            taken = true;
          }
        }
        correctedValue = (int)Math.max((Math.min(correctedValue, 255)), 0);
        if (taken) {
          newMapping.set(newMapping.getKey(), (int) this.oldValue);
        } else {
          newMapping.set(newMapping.getKey(), correctedValue);
        }
        imageWrapper.set(new ImageResource(resolve(imageIn), 1, 1000));
      }

    };
    bottomSlider.y = 0;
    bottomSlider.facing = "down";
    bottomSlider.minValue = 0;
    bottomSlider.maxValue = 256;
    bottomSlider.minWidth = .5f;
    bottomSlider.maxHeight = .3f;
    bottomSlider.maxHeight = .3f;
    bottomSlider.maxWidth = 50f;
    bottomSlider.color = new Color("#000000");
    bottomSlider.mouseOverColor = new Color("#777777");
    bottomSlider.outlineColor = new Color("#ffffff");
    bottomSlider.outlineWeight = .1f;

    bottomParent.addChild(bottomSlider);
    clickHandler.register(bottomSlider);

  }

}
