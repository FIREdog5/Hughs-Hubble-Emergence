package bin.graphics.ui;

import bin.graphics.objects.Text;

import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;
import bin.graphics.Color;

public class UITextBlock extends UIBoxCol {

  private ArrayList<String> textPeices;
  private String text;
  private float size;
  private float oldMaxWidth;
  public Color textColor;
  private boolean dirty;


  public UITextBlock(UIElement parent, String startingText, float size) {
    super(parent);
    this.textPeices = new ArrayList<String>();
    this.text = startingText;
    this.textColor = new Color("#ffffff");
    this.dirty = true;
    this.size = size;
    this.oldMaxWidth = this.maxWidth;
  }

  public void setText(String text) {
    this.text = text;
    this.dirty = true;
  }

  public String getText() {
    return this.text;
  }

  public void setSize(float size) {
    this.size = size;
    this.dirty = true;
  }

  public float getSize() {
    return this.size;
  }

  private void applyText() {
    this.textPeices = new ArrayList<String>();
    String[] lines = this.text.split("\n");
    // I think W is usually the widest character
    if (this.maxWidth == -1 || this.maxWidth < Text.getWidth("WW", this.size)) {
      for (int i = 0; i < lines.length; i++) {
        String line = lines[i].strip();
        if (line.length() == 0) {
          textPeices.add("$$break$$");
        } else {
          this.textPeices.add(line);
        }
      }
    } else {
      for (int i = 0; i < lines.length; i++) {
        String line = lines[i].strip();
        String[] words = line.split(" ");
        String createdLine = "";
        for(int j = 0; j < words.length; j++) {
          String word = words[j].strip();
          if(Text.getWidth(createdLine + " " + word, this.size) <= this.maxWidth) {
            createdLine += (createdLine.length() == 0 ? "" : " ") + word;
          } else {
            this.textPeices.add(createdLine);
            createdLine = "";
            if (Text.getWidth(word, this.size) <= this.maxWidth) {
              createdLine = word;
            } else {
              String[] characters = ((createdLine.length() == 0 ? "" : " ") + word).split("");
              for (int k = 0; k < characters.length; k++) {
                String character = characters[k];
                if(Text.getWidth(createdLine + character, this.size) <= this.maxWidth) {
                  createdLine += character;
                } else {
                  this.textPeices.add(createdLine);
                  createdLine = character;
                }
              }
            }
          }
        }
        if (createdLine.length() > 0) {
          textPeices.add(createdLine);
        }
        if (line.length() == 0) {
          textPeices.add("$$break$$");
        }
      }
    }

    for (int i = 0; i < this.children.size(); i++) {
      UIElement child = this.children.get(i);
      child.cleanUp();
    }

    for (int i = 0; i < this.textPeices.size(); i++) {
      String textPiece = this.textPeices.get(i);
      if (textPiece.equals("$$break$$")) {
        UIBoxCol line = new UIBoxCol(this);
        line.minHeight = Text.getHeight("WW", this.size);
        this.addChild(line);
      } else {
        UIText line = new UIText(this, this.textPeices.get(i), this.size);
        line.color = this.textColor;
        line.noBackground = true;
        line.outlineWeight = 0f;
        this.addChild(line);
      }
    }

    this.dirty = false;
    this.oldMaxWidth = this.maxWidth;
  }

  @Override
  public void render() {
    if (this.dirty || this.oldMaxWidth != this.maxWidth) {
      this.applyText();
    }
    super.render();
  }

}
