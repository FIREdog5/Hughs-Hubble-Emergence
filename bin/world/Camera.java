package bin.world;

import bin.graphics.Renderer;

public class Camera {

  public static float zoomRatio = 100;
  public static float minZoom = 1;
  public static float maxZoom = 40;
  public static float minLocation = -10000;
  public static float maxLocation = 10000;

  private float x;
  private float y;
  private float zoom;

  public Camera(float x, float y, float zoom) {
    this(x, y, zoom, 1, 40);
  }

  public Camera(float x, float y, float zoom, float minZoom, float maxZoom) {
    this.x = x;
    this.y = y;
    this.zoom = zoom;
    this.minZoom = minZoom;
    this.maxZoom = maxZoom;
  }
  public void setZoom(float zoom) {
    this.zoom = Math.max(minZoom, Math.min(maxZoom, zoom));
  }

  public void adjustZoom(float deltaZoom) {
    deltaZoom /= (maxZoom / this.zoom);
    this.zoom = Math.max(minZoom, Math.min(maxZoom, zoom + deltaZoom));
  }

  public void setLocation(float x, float y) {
    this.x = Math.max(minLocation, Math.min(maxLocation, x));
    this.y = Math.max(minLocation, Math.min(maxLocation, y));
  }

  public void adjustLocation(float deltaX, float deltaY) {
    this.x = Math.max(minLocation, Math.min(maxLocation, x + deltaX));
    this.y = Math.max(minLocation, Math.min(maxLocation, y + deltaY));
  }

  public float convertXToCamera(float x) {
    return (x - this.x) / zoomRatio * this.zoom;
  }

  public float convertYToCamera(float y) {
    return (y - this.y) / zoomRatio * this.zoom;
  }

  public float convertCameraToX(float x) {
    return  this.x + x * zoomRatio / this.zoom;
  }

  public float convertCameraToY(float y) {
    return  this.y + y * zoomRatio / this.zoom;
  }

  public float scaleToZoom(float width) {
    return width / zoomRatio * this.zoom;
  }

  public float zoomToScale(float width) {
    return width * zoomRatio / this.zoom;
  }

  public float getCameraRightBound() {
    return this.x + zoomRatio / this.zoom * Renderer.unitsWide / 2;
  }

  public float getCameraTopBound() {
    float ratioTall = (float) Renderer.getWindowHeight() / (float) Renderer.getWindowWidth();
    return this.y + zoomRatio / this.zoom * ratioTall * Renderer.unitsWide / 2;
  }

  public float getCameraLeftBound() {
    return this.x - zoomRatio / this.zoom * Renderer.unitsWide / 2;
  }

  public float getCameraBottomBound() {
    float ratioTall = (float) Renderer.getWindowHeight() / (float) Renderer.getWindowWidth();
    return this.y - zoomRatio / this.zoom * ratioTall * Renderer.unitsWide / 2;
  }
}
