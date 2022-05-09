package com.input;

import java.util.ArrayList;

public class ClickHandler {
  // TODO performance gains here using click grid zones (hopefully ill remember what that means)

  private ArrayList<IClickable> clickables;

  public ClickHandler() {
    this.clickables = new ArrayList<IClickable>();
  }

  public void register(IClickable clickable) {
    this.clickables.add(clickable);
  }

  public void remove(IClickable clickable) {

  }

  public void processClick() {

  }

  public void processMouseMove() {

  }

  public void processScrollOver() {
    
  }


}
