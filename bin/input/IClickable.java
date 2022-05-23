package bin.input;

public interface IClickable {

  public void setClickHandler(ClickHandler ClickHandler);
  public ClickHandler getClickHandler();
  public boolean isMouseOver(float x, float y);
  public boolean getIsMousedOver();
  public void cleanFromClickHandler();
  public int getZ();
  //set z to -1 to be ignored
  public void setZ(int z);

  public void mouseMoved(float x, float y);
  public void clickedOn(float x, float y);
  public void mousedOver(float x, float y);
  public void mousedOff(float x, float y);
  public void mousedDown(float x, float y);
  public void mousedUp(float x, float y);
  public void scrolledOver(float x, float y, float amount);

}
