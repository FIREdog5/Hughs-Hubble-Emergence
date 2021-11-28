package bin.resource.proceduralGeneration;

public abstract class ImageCombineModifier implements IImageModifier {
  protected SimpleImage simpleImage;
  public ImageCombineModifier(SimpleImage simpleImage) {
    this.simpleImage = simpleImage;
  }
  @Override
  public Parameter[] getParameters() {
    return new Parameter[0];
  }
}
