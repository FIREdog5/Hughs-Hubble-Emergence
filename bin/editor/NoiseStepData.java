package bin.editor;

import bin.resource.proceduralGeneration.IImageModifier;
import bin.resource.FastNoiseLite;
import bin.Wrapper;
import bin.resource.ImageResource;
import bin.resource.FastNoiseLite;

public class NoiseStepData {

  public IImageModifier modifier;
  public FastNoiseLite fnl;
  public Wrapper<ImageResource> noisePreviewWrapper = new Wrapper<ImageResource>();

  public NoiseStepData() {
  }
}
