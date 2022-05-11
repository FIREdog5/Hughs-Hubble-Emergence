package com.input;

public interface IClickable {

  public boolean isMouseOver(float x, float y);
  public boolean isOnScreen();
  public void cleanFromClickHandler();

}
