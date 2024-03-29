package bin.editor;

import bin.resource.proceduralGeneration.IImageModifier;
import bin.Wrapper;
import bin.resource.ImageResource;
import bin.resource.FastNoiseLite;
import bin.resource.FastNoiseLiteDomainWarp;

public class NoiseStepData {

  public IImageModifier modifier;
  public FastNoiseLite fnl;
  public Wrapper<ImageResource> noisePreviewWrapper = new Wrapper<ImageResource>();
  public boolean init = false;

  public NoiseStepData() {
  }

  public NoiseStepData(NoiseStepData old) {
    this.modifier = old.modifier.copy();
    this.fnl = new FastNoiseLiteDomainWarp(old.fnl);
    this.init = old.init;
  }
}
