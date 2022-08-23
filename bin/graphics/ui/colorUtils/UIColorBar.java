package bin.graphics.ui.colorUtils;

import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UIElement;
import bin.graphics.Color;
import bin.graphics.objects.Rect;
import bin.graphics.objects.RoundedBoxOutline;
import bin.graphics.objects.Global;

public class UIColorBar extends UIBoxCol {

  private Color[] colors;
  //bottom left, bottom right, top right, top left

  public UIColorBar(UIElement parent, Color[] colors) {
    super(parent);
    this.colors = colors;
  }

  @Override
  public void render() {
    if (!this.getNoBackground()) {
      Rect.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f, colors);
    }
    if (this.getOutlineWeight() > 0f) {
      Global.drawColor(this.getOutlineColor());
      RoundedBoxOutline.draw(this.getX() + this.getWidth() / 2, this.getY() - this.getHeight() / 2, this.getWidth(), this.getHeight(), 0f, this.getOutlineWeight());
    }
    for (int i = 0; i < this.children.size(); i++) {
      this.children.get(i).render();
    }
  }
}
