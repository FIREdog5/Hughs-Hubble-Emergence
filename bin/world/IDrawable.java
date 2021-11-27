package bin.world;

public interface IDrawable {
  public boolean shouldRender(Camera camera);
  public void render(Camera camera);
}
