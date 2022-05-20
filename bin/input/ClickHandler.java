package bin.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class ClickHandler {
  // TODO performance gains here using click grid zones (hopefully ill remember what that means)

  private ArrayList<IClickable> clickables;
  private ArrayList<IClickable> mousedOver;
  private ArrayList<IClickable> mousedDown;
  private int zCounter;

  private static final Comparator<IClickable> comparator = (IClickable o1, IClickable o2) -> o1.getZ() < 0 ? -1 : o2.getZ() < 0 ? 1 : o2.getZ() - o1.getZ();

  public ClickHandler() {
    this.clickables = new ArrayList<IClickable>();
    this.mousedOver = new ArrayList<IClickable>();
    this.mousedDown = new ArrayList<IClickable>();
    this.zCounter = 0;
  }

  public void register(IClickable clickable) {
    if (clickable.getZ() == 0) {
      clickable.setZ(this.zCounter);
      zCounter++;
    }
    this.clickables.add(clickable);
    Collections.sort(this.clickables, comparator);
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
        if (clickable.getZ() >= 0) {
          break;
        }
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
          if (clickable.getZ() >= 0) {
            break;
          }
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
    boolean found = false;
    for(IClickable clickable : this.clickables) {
      if (!found) {
        if (clickable.isMouseOver(x, y)) {
          clickable.mousedOver(x, y);
          this.mousedOver.add(clickable);
          if (clickable.getZ() >= 0) {
            found = true;
          }
        }
      } else {
        if (this.mousedOver.contains(clickable)) {
          clickable.mousedOff(x, y);
          this.mousedOver.remove(clickable);
        }
      }
    }
  }

  public void processScrollOver(float x, float y, float amount) {
    for(IClickable clickable : this.clickables) {
      if (clickable.isMouseOver(x, y)) {
        clickable.scrolledOver(x, y, amount);
        if (clickable.getZ() >= 0) {
          break;
        }
      }
    }
  }


}
