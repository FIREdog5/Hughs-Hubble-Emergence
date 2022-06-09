package bin.input;

import bin.graphics.ui.UIElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClickHandler {
  // TODO performance gains here using click grid zones (hopefully ill remember what that means)

  private Lock clickablesLock;
  private ArrayList<IClickable> clickables;
  private ArrayList<IClickable> mousedOver;
  private ArrayList<IClickable> mousedDown;
  private int zCounter;
  private UIElement mask;

  private static final Comparator<IClickable> comparator = (IClickable o1, IClickable o2) -> o1.getZ() < 0 ? -1 : o2.getZ() < 0 ? 1 : o2.getZ() - o1.getZ();

  public ClickHandler() {
    this.clickablesLock = new ReentrantLock();
    this.clickables = new ArrayList<IClickable>();
    this.mousedOver = new ArrayList<IClickable>();
    this.mousedDown = new ArrayList<IClickable>();
    this.zCounter = 0;
    this.mask = null;
  }

  public void register(IClickable clickable) {
    clickablesLock.lock();
    try {
      if (clickable.getZ() == 0) {
        clickable.setZ(this.zCounter);
        zCounter++;
      }
      this.clickables.add(clickable);
      Collections.sort(this.clickables, comparator);
      clickable.setClickHandler(this);
    } finally {
      clickablesLock.unlock();
    }
  }

  public void remove(IClickable clickable) {
    clickablesLock.lock();
    try {
      this.clickables.remove(clickable);
      this.mousedOver.remove(clickable);
      this.mousedDown.remove(clickable);
    } finally {
      clickablesLock.unlock();
    }
  }

  public void setMask(UIElement mask) {
    this.mask = mask;
  }

  public void clearMask() {
    this.mask = null;
  }

  public void processClick(float x, float y) {
    clickablesLock.lock();
    Collections.sort(this.clickables, comparator);
    try {
      for(IClickable clickable : this.clickables) {
        if (this.mask != null && !this.mask.deepContains(clickable)) {
          continue;
        }
        if (clickable.isMouseOver(x, y)) {
          clickable.clickedOn(x, y);
          if (clickable.getZ() >= 0) {
            break;
          }
        }
      }
    } finally {
      clickablesLock.unlock();
    }
  }

  public boolean isMouseOnElement() {
    return this.mousedDown.size() > 0;
  }

  public void processMouseDown(float x, float y, boolean offScreen) {
    clickablesLock.lock();
    Collections.sort(this.clickables, comparator);
    try {
      ArrayList<IClickable> mousedDownCopy = (ArrayList<IClickable>) this.mousedDown.clone();
      for(IClickable clickable : mousedDownCopy) {
        if (!clickable.isMouseOver(x, y) || offScreen) {
          clickable.mousedUp(x, y);
          this.mousedDown.remove(clickable);
        }
      }
      if (!offScreen){
        for(IClickable clickable : this.clickables) {
          if (this.mask != null && !this.mask.deepContains(clickable)) {
            continue;
          }
          if (clickable.isMouseOver(x, y)) {
            clickable.mousedDown(x, y);
            this.mousedDown.add(clickable);
            if (clickable.getZ() >= 0) {
              break;
            }
          }
        }
      }
    } finally {
      clickablesLock.unlock();
    }
  }

  public void processMouseMove(float x, float y) {
    clickablesLock.lock();
    Collections.sort(this.clickables, comparator);
    try {
      ArrayList<IClickable> mousedOverCopy = (ArrayList<IClickable>) this.mousedOver.clone();
      for(IClickable clickable : mousedOverCopy) {
        if (!clickable.isMouseOver(x, y)) {
          clickable.mousedOff(x, y);
          this.mousedOver.remove(clickable);
        }
      }
      boolean found = false;
      for(IClickable clickable : this.clickables) {
        if ((this.mask == null || this.mask.deepContains(clickable))) {
          clickable.mouseMoved(x, y);
        }
        if (!found && (this.mask == null || this.mask.deepContains(clickable))) {
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
    } finally {
      clickablesLock.unlock();
    }
  }

  public void processMouseDragged(float x, float y) {
    this.processMouseMove(x, y);
  }

  public void processScrollOver(float x, float y, float amount) {
    clickablesLock.lock();
    Collections.sort(this.clickables, comparator);
    try {
      for(IClickable clickable : this.clickables) {
        if (this.mask != null && !this.mask.deepContains(clickable)) {
          continue;
        }
        if (clickable.isMouseOver(x, y)) {
          clickable.scrolledOver(x, y, amount);
          if (clickable.getZ() >= 0) {
            break;
          }
        }
      }
    } finally {
      clickablesLock.unlock();
    }
  }


}
