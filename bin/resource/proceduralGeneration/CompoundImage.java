package bin.resource.proceduralGeneration;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class CompoundImage {
  private ArrayList<IImageModifier> imageSteps;

  public CompoundImage() {
    this.imageSteps = new ArrayList<IImageModifier>();
  }

  public void addStep(IImageModifier step) {
    this.imageSteps.add(step);
  }

  public BufferedImage resolve(int width, int height) {
    BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < this.imageSteps.size(); i++) {
      outputImage = this.imageSteps.get(i).resolve(outputImage);
    }
    return outputImage;
  }
}
