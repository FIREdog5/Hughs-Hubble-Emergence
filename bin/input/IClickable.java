package bin.input;

public interface IClickable {

  public void setClickHandler(ClickHandler ClickHandler);
  public ClickHandler getClickHandler();
  public boolean isMouseOver(float x, float y);
  public void cleanFromClickHandler();

  public void clickedOn(float x, float y);
  public void mousedOver(float x, float y);
  public void mousedOff(float x, float y);
  public void mousedDown(float x, float y);
  public void mousedUp(float x, float y);
  public void scrolledOver(float x, float y, float amount);

}
