package bin.resource;

import bin.resource.FastNoiseLite;
import bin.resource.proceduralGeneration.*;
import bin.resource.proceduralGeneration.modifiers.*;

import java.awt.image.BufferedImage;
import bin.graphics.Color;


public class ImageResources{
  //tests
  public static ImageResource error = null;
  public static ImageResource test = null;
  public static ImageResource starTest1 = null;
  public static ImageResource primordialGalaxy = null;
  public static ImageResource poleTest = null;
  public static ImageResource earthTest = null;
  public static ImageResource transparencyTest = null;
  public static ImageResource planetTestMask = null;
  public static ImageResource cloudTest = null;
  public static ImageResource noiseTest = null;
  public static ImageResource maskTest = null;
  public static ImageResource maskTest2 = null;
  public static ImageResource maskTest3 = null;
  public static ImageResource capTest = null;
  public static ImageResource cloudTest2 = null;
  public static ImageResource generationTest = null;
  public static ImageResource generationTest2 = null;
  public static ImageResource terainShaderTest = null;
  public static ImageResource biomeShaderTest = null;
  public static ImageResource biomeGradientTest = null;
  public static ImageResource sphericalTest = null;
  public static ImageResource gradientTest = null;
  public static ImageResource stripeTest = null;
  public static ImageResource domainWarpRBTest = null;

  //resources
  public static ImageResource blankGradient = null;

  //icons
  public static ImageResource globeIcon = null;
  public static ImageResource mapIcon = null;
  public static ImageResource addIcon = null;
  public static ImageResource menuIcon = null;
  public static ImageResource settingsIcon = null;
  public static ImageResource littleArrow = null;

  public static void init() {
    //tests
    error = new ImageResource("/assets/images/tests/error.png");
    test = new ImageResource("/assets/images/tests/test.png", 3, 30);
    starTest1 = new ImageResource("/assets/images/tests/startest1.png", 8, 3, true);
    primordialGalaxy = new ImageResource("/assets/images/tests/Primordial-galaxy.jpg");
    poleTest = new ImageResource("/assets/images/tests/poletest.png", 1, 60);
    earthTest = new ImageResource("/assets/images/tests/earth2.png", 1, 1000);
    transparencyTest = new ImageResource("/assets/images/tests/transparencyTest.png");
    planetTestMask = new ImageResource("/assets/images/tests/planetTestMask.png", 1, 200);
    cloudTest = new ImageResource("/assets/images/tests/cloudTest.png", 1, 800);
    noiseTest = new PolarResource("/assets/images/tests/noiseTest.png", 1, 100);
    maskTest = new NoiseMaskedResource("/assets/images/tests/noiseTest.png",.4f , 1, 1000);
    maskTest2 = new NoiseMaskedResource("/assets/images/tests/noiseTest.png",.6f , 1, 1000);
    maskTest3 = new NoiseMaskedResource("/assets/images/tests/noiseTest.png",.65f , 1, 1000);
    capTest = new NoiseMaskedFlatResource("/assets/images/tests/capTest.png",.1f , 1, 1000);
    cloudTest2 = new NoiseMaskedFlatResource("/assets/images/tests/cloudTest2.png",.4f , 1, 800);
    terainShaderTest = new ImageResource("/assets/images/tests/terainShader.png");
    biomeShaderTest = new ImageResource("/assets/images/tests/biomeShaderTest.png");
    biomeGradientTest = new ImageResource("/assets/images/tests/biomeGradientTest.png");
    stripeTest = new ImageResource("/assets/images/tests/stripeTest.png", 1, 1000);

    FastNoiseLite fnlRB1 = new FastNoiseLite();
    fnlRB1.SetSeed(1);
    FastNoiseLite fnlRB2 = new FastNoiseLite();
    fnlRB2.SetSeed(2);
    domainWarpRBTest = new ImageResource(ImageGenerator.SphericalFNLRB(fnlRB1, fnlRB2, 2400), 1, 4000);

    //TODO: make contrast its own thing lol
    generationTest = new PolarResource(ImagePreprocessor.adjustContrast(
      ImagePreprocessor.combineImagesMult(
        ImagePreprocessor.adjustContrast(
          ImageGenerator.OpenSimpleNoiseTexture(1L, 70d, 2400, 800),
            1.9f,
            -130f),
        ImagePreprocessor.adjustContrast(
          ImagePreprocessor.invert(
            ImageGenerator.OpenSimpleNoiseTexture(1L, 20d, 2400, 800)),
              1.3f,
              70f)),
        1.6f, -100f), 1, 1000);

    CompoundImage baseImage = new CompoundImage();
    FastNoiseLite fnl1 = new FastNoiseLite();
    fnl1.SetSeed(1);
    fnl1.SetFrequency(.01f);
    FastNoiseLite basefnl1 = new FastNoiseLite();
    basefnl1.SetSeed(3);
    basefnl1.SetFrequency(.005f);
    baseImage.addStep(new ImageCreateModifier(basefnl1));
    baseImage.addStep(new ImageContrastModifier(1.9d, -130d));
    baseImage.addStep(new ImageMultModifier(fnl1));
    baseImage.addStep(new ImageContrastModifier(1.6d, -100d));
    generationTest = new ImageResource(baseImage.resolve(2400, 800), 1, 1000);

    CompoundImage baseImage2 = new CompoundImage();
    FastNoiseLite fnl2 = new FastNoiseLite();
    fnl2.SetSeed(2);
    fnl2.SetFrequency(.005f);
    FastNoiseLite basefnl2 = new FastNoiseLite();
    basefnl2.SetSeed(2);
    basefnl2.SetFrequency(.001f);
    baseImage2.addStep(new ImageCreateModifier(basefnl2));
    baseImage2.addStep(new ImageContrastModifier(1.9d, -130d));
    baseImage2.addStep(new ImageMultModifier(fnl2));
    baseImage2.addStep(new ImageContrastModifier(1.6d, -100d));
    generationTest2 = new ImageResource(baseImage2.resolve(2400, 800), 1, 1000);

    sphericalTest = new ImageResource(ImageGenerator.SphericalOpenSimplexNoise(1L, 70d, 2400), 1, 1000);

    BufferedImage gradientTestBuffer = GradientGenerator.newGradient(1);
    // GradientGenerator.setColumn(gradientTestBuffer, 0, new ColorPosition[]{new ColorPosition(new Color("#ff0000"), 1, 20)}, new ColorPosition[]{new ColorPosition(new Color("#ffff00"), 4, 40)});
    GradientGenerator.setColumn(gradientTestBuffer, 0, new ColorPosition[]{new ColorPosition(new Color("#ff0000"), 1, 20), new ColorPosition(new Color("#00ff00"), 1, 21), new ColorPosition(new Color("#0000ff"), 1, 30)}, new ColorPosition[]{new ColorPosition(new Color("#ffff00"), 4, 40)});
    gradientTest = new ImageResource(gradientTestBuffer);

    //resources
    BufferedImage blankGradientBuffer = GradientGenerator.newGradient(1);
    GradientGenerator.setColumn(blankGradientBuffer, 0, new ColorPosition[]{new ColorPosition(new Color("#000000"), 1, 255)}, new ColorPosition[]{});
    blankGradient = new ImageResource(blankGradientBuffer);

    //icons
    globeIcon = new ImageResource("/assets/images/icons/globeIcon.png");
    mapIcon = new ImageResource("/assets/images/icons/mapIcon.png");
    addIcon = new ImageResource("/assets/images/icons/addIcon.png");
    menuIcon = new ImageResource("/assets/images/icons/menuIcon.png");
    settingsIcon = new ImageResource("/assets/images/icons/settingsIcon.png");
    littleArrow = new ImageResource("/assets/images/icons/LittleArrow.png");

    System.out.println("Loaded all resources");
  }
}
