package bin.graphics.objects;

import bin.ClientMain;
import bin.graphics.Color;

import com.jogamp.opengl.GL2;

public class Line {
  public static void draw(float x1, float y1, float x2, float y2, float width) {
    float length = (float) Math.sqrt((x1-x2) * (x1-x2) + (y1-y2) * (y1-y2));
    float midX = (x1 + x2) / 2f;
    float midY = (y1 + y2) / 2f;
    float rotation = (float) Math.atan((y2 - y1) / (x2 - x1)) / ((float) Math.PI) * 180f;
    Rect.draw(midX, midY, length, width, rotation);
  }
}
