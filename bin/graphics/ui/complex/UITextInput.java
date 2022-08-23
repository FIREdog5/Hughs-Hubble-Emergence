package bin.graphics.ui.complex;

import bin.graphics.ui.UISelectable;
import bin.graphics.ui.UIElement;
import bin.graphics.objects.Global;
import bin.graphics.objects.RoundedBox;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.Text;
import bin.graphics.objects.Rect;
import bin.graphics.objects.ScreenArea;
import bin.input.Key;
import bin.graphics.Color;
import bin.input.KeyboardHandler;
import bin.input.IKeyConsumer;
import bin.graphics.Renderer;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import com.jogamp.newt.event.KeyEvent;

public abstract class UITextInput extends UISelectable implements IKeyConsumer{
  public float size;
  public Color textColor;
  public Color selectionColor;
  public Color selectedTextColor;
  private boolean shiftHeld;
  private boolean controlHeld;
  private int selectionLow;
  private int selectionHigh;
  private boolean leftHanded;
  private KeyboardHandler keyboardHandler;
  private int fbo;
  private int lastFrame;
  private float scrollAmount;
  private int startSelection;
  private float currX;
  private boolean selecting;

  public UITextInput(UIElement parent, float size) {
    super(parent);
    this.size = size;
    this.textColor = new Color("#000000");
    this.selectionColor = new Color("#4F69FF");
    this.selectedTextColor = new Color("#ffffff");
    this.shiftHeld = false;
    this.controlHeld = false;
    this.selectionHigh = 0;
    this.selectionLow = 0;
    this.leftHanded = true;
    this.keyboardHandler = null;
    this.fbo = -1;
    this.lastFrame = 0;
    this.scrollAmount = 0;
    this.startSelection = 0;
    this.currX = 0;
    this.selecting = false;
  }

  public abstract String getValue();
  public abstract boolean setValue(String newValue);

  public float getSize(){
    return this.size;
  }

  public boolean setUnfilteredValue(String newValue) {
    return setValue(newValue);
  }

  @Override
  public void setKeyboardHandler(KeyboardHandler keyboardHandler) {
    this.keyboardHandler = keyboardHandler;
  }

  @Override
  public KeyboardHandler getKeyboardHandler() {
    return this.keyboardHandler;
  }

  private float minTextHeight() {
    return 2.3f * this.getSize();
  }

  @Override
  public float getHeight() {
    return Math.max(super.getHeight(), this.minTextHeight());
  }

  private void adjustScroll() {
    String content = this.getValue();
    if (selecting) {
      int currSelection = this.getClosestIndex(this.currX);
      float textOffset = Text.getWidth(content.substring(0, currSelection), this.getSize());
      if (textOffset + 0.1f < this.scrollAmount) {
        this.scrollAmount -= .2f;
      } else if (textOffset + 0.1f > this.getWidth() - this.getOutlineWeight() * 2 + this.scrollAmount) {
        this.scrollAmount += .2f;
      }
      return;
    }
    float textLowOffset = Text.getWidth(content.substring(0, this.selectionLow), this.getSize());
    float textHighOffset = Text.getWidth(content.substring(0, this.selectionHigh), this.getSize());
    if (this.selectionLow == this.selectionHigh) {
      if (textLowOffset + 0.1f < this.scrollAmount) {
        this.scrollAmount = textLowOffset;
      } else if (textLowOffset + 0.1f > this.getWidth() - this.getOutlineWeight() * 2 + this.scrollAmount) {
        this.scrollAmount = textLowOffset + .2f - this.getWidth() + this.getOutlineWeight() * 2;
      }
    } else {
      if (this.leftHanded) {
        if (textHighOffset + 0.1f < this.scrollAmount) {
          this.scrollAmount = textHighOffset;
        } else if (textHighOffset + 0.1f > this.getWidth() - this.getOutlineWeight() * 2 + this.scrollAmount) {
          this.scrollAmount = textHighOffset + .2f - this.getWidth() + this.getOutlineWeight() * 2;
        }
      } else {
        if (textLowOffset + 0.1f < this.scrollAmount) {
          this.scrollAmount = textLowOffset;
        } else if (textLowOffset + 0.1f > this.getWidth() - this.getOutlineWeight() * 2 + this.scrollAmount) {
          this.scrollAmount = textLowOffset + .2f - this.getWidth() + this.getOutlineWeight() * 2;
        }
      }
    }
  }

  @Override
  public void render() {
    if (this.fbo == -1) {
      this.fbo = Renderer.framebufferController.createNewFrame();
    }
    this.deselectBlocked = false;
    if (!this.noBackground) {
      Global.drawColor(this.getColor());
      RoundedBox.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.getIsMouseDown() ? .2f : 0)), (this.getHeight() + (this.getIsMouseDown() ? .2f : 0)), this.getRadius());
    }
    if (this.outlineWeight > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, (this.getWidth() + (this.getIsMouseDown() ? .2f : 0)), (this.getHeight() + (this.getIsMouseDown() ? .2f : 0)), this.getRadius(), this.getOutlineWeight());
    }
    //switch to fbo
    Renderer.framebufferController.switchToFrame(this.fbo);
    //draw text
    this.selectionHigh = Math.min(selectionHigh, this.getValue().length());
    this.selectionLow = Math.min(selectionLow, this.getValue().length());
    String content = this.getValue();
    float textLowOffset = Text.getWidth(content.substring(0, this.selectionLow), this.getSize());
    float textHighOffset = Text.getWidth(content.substring(0, this.selectionHigh), this.getSize());
    float textWidth = Text.getWidth(content, this.getSize());
    float textSelectionWidth = textHighOffset - textLowOffset;
    int frame = Global.getFrame();
    this.adjustScroll();
    if (this.selecting && this.isSelected()) {
      int currSelection = this.getClosestIndex(this.currX);
      int low = Math.min(this.startSelection, currSelection);
      int high = Math.max(this.startSelection, currSelection);
      textLowOffset = Text.getWidth(content.substring(0, low), this.getSize());
      textHighOffset = Text.getWidth(content.substring(0, high), this.getSize());
      textSelectionWidth = textHighOffset - textLowOffset;
      if (this.startSelection != currSelection) {
        Global.drawColor(this.selectionColor);
        Rect.draw(this.getX() - this.scrollAmount + textLowOffset + textSelectionWidth / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, textSelectionWidth , this.minTextHeight());
        Text.draw(content.substring(0, low), this.getX() - this.scrollAmount + textLowOffset / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.textColor, this.getSize());
        Text.draw(content.substring(low, high), this.getX() - this.scrollAmount + textLowOffset + textSelectionWidth / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.selectedTextColor, this.getSize());
        Text.draw(content.substring(high), this.getX() - this.scrollAmount + textLowOffset + textSelectionWidth + (textWidth - textHighOffset) / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.textColor, this.getSize());
      } else {
        Text.draw(content, this.getX() - this.scrollAmount + textWidth / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.textColor, this.getSize());
      }
    } else if (this.selectionLow != this.selectionHigh) {
      Global.drawColor(this.selectionColor);
      Rect.draw(this.getX() - this.scrollAmount + textLowOffset + textSelectionWidth / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, textSelectionWidth , this.minTextHeight());
      if ((int) ((frame - this.lastFrame) / 4) % 10 < 5) {
        Global.drawColor(this.textColor);
        if (this.leftHanded) {
          Rect.draw(this.getX() - this.scrollAmount + textHighOffset + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, .1f , this.minTextHeight());
        } else {
          Rect.draw(this.getX() - this.scrollAmount + textLowOffset + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, .1f , this.minTextHeight());
        }
      }
      Text.draw(content.substring(0, this.selectionLow), this.getX() - this.scrollAmount + textLowOffset / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.textColor, this.getSize());
      Text.draw(content.substring(this.selectionLow, this.selectionHigh), this.getX() - this.scrollAmount + textLowOffset + textSelectionWidth / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.selectedTextColor, this.getSize());
      Text.draw(content.substring(this.selectionHigh), this.getX() - this.scrollAmount + textLowOffset + textSelectionWidth + (textWidth - textHighOffset) / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.textColor, this.getSize());
    } else {
      if (this.isSelected()) {
        if ((int) ((frame - this.lastFrame) / 4) % 10 < 5) {
          Global.drawColor(this.textColor);
          Rect.draw(this.getX() - this.scrollAmount + textHighOffset + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, .1f , this.minTextHeight());
        }
      }
      Text.draw(content, this.getX() - this.scrollAmount + textWidth / 2 + this.getOutlineWeight() + .1f, this.getY() - this.getHeight() / 2, this.textColor, this.getSize());
    }
    //switch back to previous (or default) fbo
    Renderer.framebufferController.popFrameAndBind();
    //draw fbo content
    ScreenArea.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth() - 2 * this.getOutlineWeight(), this.getHeight() - 2 * this.getOutlineWeight());
  }

  @Override
  public void keyDown(Key key) {
    if (key.equals(KeyEvent.VK_SHIFT)) {
      this.shiftHeld = true;
    } else if (key.equals(KeyEvent.VK_CONTROL)) {
      this.controlHeld = true;
    }
    if (this.isDead || !this.isSelected() || this.selecting) {
      return;
    }
    this.lastFrame = Global.getFrame();
    if (key.isChar()) {
      if (this.controlHeld) {
        StringSelection selection;
        Clipboard clipboard;
        String existing = this.getValue();
        switch (key.getVK()) {
          case KeyEvent.VK_A:
            this.selectionHigh = this.getValue().length();
            this.selectionLow = 0;
            this.leftHanded = true;
            break;
          case KeyEvent.VK_C:
            selection = new StringSelection(this.getValue().substring(this.selectionLow, this.selectionHigh));
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            break;
          case KeyEvent.VK_X:
            selection = new StringSelection(this.getValue().substring(this.selectionLow, this.selectionHigh));
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            this.setUnfilteredValue(existing.substring(0, this.selectionLow) + existing.substring(this.selectionHigh));
            this.selectionHigh = this.selectionLow;
            this.leftHanded = true;
            break;
          case KeyEvent.VK_V:
            try {
              String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
              if (this.setUnfilteredValue(existing.substring(0, this.selectionLow) + data + existing.substring(this.selectionHigh))) {
                this.selectionLow = this.selectionLow + data.length();
                this.selectionHigh = this.selectionLow;
                this.leftHanded = true;
              }
            } catch (Exception e) {
              //nothing
            }
            break;
        }
      } else {
        char typedChar = key.getChar();
        String existing = this.getValue();
        if (this.setUnfilteredValue(existing.substring(0, this.selectionLow) + typedChar + existing.substring(this.selectionHigh))) {
          this.selectionLow = this.selectionLow + 1;
          this.selectionHigh = this.selectionLow;
          this.leftHanded = true;
        }
      }
    } else if (key.isArrow()) {
      if (key.equals(KeyEvent.VK_LEFT)) {
        if (this.shiftHeld) {
          //holding shift
          if (this.selectionHigh == this.selectionLow) {
            this.leftHanded = false;
          }
          if (this.leftHanded) {
            this.selectionHigh -= 1;
            this.selectionHigh = Math.max(this.selectionLow, this.selectionHigh);
          } else {
            this.selectionLow -= 1;
            this.selectionLow = Math.max(0, this.selectionLow);
          }
        } else {
          //not holding shift
          this.leftHanded = true;
          if (this.selectionHigh != this.selectionLow) {
            this.selectionHigh = this.selectionLow;
          } else {
            this.selectionLow -= 1;
            this.selectionLow = Math.max(0, this.selectionLow);
            this.selectionHigh = this.selectionLow;
          }
        }
      } else if (key.equals(KeyEvent.VK_RIGHT)) {
        if (this.shiftHeld) {
          if (this.selectionHigh == this.selectionLow) {
            this.leftHanded = true;
          }
          if (this.leftHanded) {
            this.selectionHigh += 1;
            this.selectionHigh = Math.min(this.getValue().length(), this.selectionHigh);
          } else {
            this.selectionLow += 1;
            this.selectionLow = Math.min(this.selectionHigh, this.selectionLow);
          }

        } else {
          this.leftHanded = true;
          if (this.selectionHigh != this.selectionLow) {
            this.selectionLow = this.selectionHigh;
          } else {
            this.selectionLow += 1;
            this.selectionLow = Math.min(this.getValue().length(), this.selectionLow);
            this.selectionHigh = this.selectionLow;
          }
        }
      }
    } else if (key.equals(KeyEvent.VK_BACK_SPACE)) {
      this.leftHanded = true;
      String existing = this.getValue();
      if (selectionLow != selectionHigh) {
        this.setUnfilteredValue(existing.substring(0, this.selectionLow) + existing.substring(this.selectionHigh));
        this.selectionHigh = this.selectionLow;
      } else if (selectionLow != 0) {
        this.setUnfilteredValue(existing.substring(0, this.selectionLow - 1) + existing.substring(this.selectionLow));
        this.selectionLow -= 1;
        this.selectionHigh = this.selectionLow;
      }
    } else if (key.equals(KeyEvent.VK_DELETE)) {
      this.leftHanded = true;
      String existing = this.getValue();
      if (selectionLow != selectionHigh) {
        this.setUnfilteredValue(existing.substring(0, this.selectionLow) + existing.substring(this.selectionHigh));
        this.selectionHigh = this.selectionLow;
      } else if (selectionLow != this.getValue().length()) {
        this.setUnfilteredValue(existing.substring(0, this.selectionLow) + existing.substring(this.selectionLow + 1));
      }
    } else if (key.equals(KeyEvent.VK_ENTER)) {
      this.deselect();
    }
  }

  @Override
  public void onDeselect() {
    this.selectionLow = 0;
    this.selectionHigh = 0;
    this.scrollAmount = 0;
    this.startSelection = 0;
    this.currX = 0;
  }

  @Override
  public void keyUp(Key key) {
    if (this.isDead) {
      return;
    }
    if (key.equals(KeyEvent.VK_SHIFT)) {
      this.shiftHeld = false;
    } else if (key.equals(KeyEvent.VK_CONTROL)) {
      this.controlHeld = false;
    }
  }

  private int getClosestIndex(float x) {
    float adjustedX = x - this.getX() + this.scrollAmount - this.getOutlineWeight() - .1f;
    if (x > this.getX() + this.getWidth()) {
      adjustedX = this.getWidth() + this.scrollAmount - this.getOutlineWeight();
    }
    if (adjustedX <= 0) {
      return 0;
    }
    float lastBreak = 0;
    int c = 0;
    for (String s : this.getValue().split("")) {
      float currBreak = lastBreak + Text.getWidth(s, this.getSize());
      if (adjustedX >= lastBreak && adjustedX <= currBreak) {
        //found that a 3/4 of the way down the character rather than half way as a break point feels more natural.
        //probably because we usually select from left to right...
        if (adjustedX <= (3f / 4f) * (currBreak - lastBreak) + lastBreak) {
          return c;
        } else {
          return c + 1;
        }
      }
      lastBreak = currBreak;
      c++;
    }
    return this.getValue().length();
  }

  @Override
  public void mousedDown(float x, float y) {
    if (this.isDead) {
      return;
    }
    this.select();
    this.startSelection = getClosestIndex(x);
    this.selecting = true;
    this.currX = x;
  }

  @Override
  public void mousedUp(float x, float y) {
    if (this.isDead) {
      return;
    }
    if (!this.selecting) {
      super.mousedUp(x, y);
      return;
    }
    int select = getClosestIndex(x);
    this.selectionLow = Math.min(this.startSelection, select);
    this.selectionHigh = Math.max(this.startSelection, select);
    this.leftHanded = this.startSelection <= select;
    if (this.isMouseOver(x, y)) {
      this.selecting = false;
    }
  }

  @Override
  public void mouseMoved(float x, float y) {
    if (this.isDead) {
      return;
    }
    if (this.selecting) {
      this.currX = x;
    }
  }

  @Override
  public void deselect() {
    if (this.selecting) {
      this.selecting = false;
    } else {
      super.deselect();
    }
  }

  @Override
  public void cleanFromKeyboardHandler() {
    this.getKeyboardHandler().remove(this);
  }

  @Override
  public void cleanUp() {
    this.cleanFromKeyboardHandler();
    if (this.fbo != -1) {
      Renderer.framebufferController.cleanUpFrame(this.fbo);
    }
    super.cleanUp();
  }

  // @Override
  // public void keyHeld(Key key) {
  //
  // }
}
