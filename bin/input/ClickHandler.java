package bin.input;

import java.util.ArrayList;

public class ClickHandler {
  // TODO performance gains here using click grid zones (hopefully ill remember what that means)

  private ArrayList<IClickable> clickables;
  private ArrayList<IClickable> mousedOver;
  private ArrayList<IClickable> mousedDown;

  public ClickHandler() {
    this.clickables = new ArrayList<IClickable>();
    this.mousedOver = new ArrayList<IClickable>();
    this.mousedDown = new ArrayList<IClickable>();
  }

  public void register(IClickable clickable) {
    this.clickables.add(clickable);
    clickable.setClickHandler(this);
  }

  public void remove(IClickable clickable) {
    this.clickables.remove(clickable);
    this.mousedOver.remove(clickable);
    this.mousedDown.remove(clickable);
  }

  public void processClick(float x, float y) {
    for(IClickable clickable : this.clickables) {
      if (clickable.isMouseOver(x, y)) {
        clickable.clickedOn(x, y);
      }
    }
  }

  public void processMouseDown(float x, float y, boolean offScreen) {
    ArrayList<IClickable> mousedDownCopy = (ArrayList<IClickable>) this.mousedDown.clone();
    for(IClickable clickable : mousedDownCopy) {
      if (!clickable.isMouseOver(x, y) || offScreen) {
        clickable.mousedUp(x, y);
        this.mousedDown.remove(clickable);
      }
    }
    if (!offScreen){
      for(IClickable clickable : this.clickables) {
        if (clickable.isMouseOver(x, y)) {
          clickable.mousedDown(x, y);
          this.mousedDown.add(clickable);
        }
      }
    }
  }

  public void processMouseMove(float x, float y) {
    ArrayList<IClickable> mousedOverCopy = (ArrayList<IClickable>) this.mousedOver.clone();
    for(IClickable clickable : mousedOverCopy) {
      if (!clickable.isMouseOver(x, y)) {
        clickable.mousedOff(x, y);
        this.mousedOver.remove(clickable);
      }
    }
    for(IClickable clickable : this.clickables) {
      if (clickable.isMouseOver(x, y)) {
        clickable.mousedOver(x, y);
        this.mousedOver.add(clickable);
      }
    }
  }

  public void processScrollOver(float x, float y, float amount) {
    for(IClickable clickable : this.clickables) {
      if (clickable.isMouseOver(x, y)) {
        clickable.scrolledOver(x, y, amount);
      }
    }
  }


}
