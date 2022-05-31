package bin.resource;

import bin.resource.proceduralGeneration.*;
import bin.resource.proceduralGeneration.modifiers.*;

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

  //resources

  //icons
  public static ImageResource globeIcon = null;
  public static ImageResource mapIcon = null;

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
        1.6f, -100f));

    CompoundImage baseImage = new CompoundImage();
    baseImage.addStep(new ImageCreateModifier(1L, 70d));
    baseImage.addStep(new ImageContrastModifier(1.9d, -130d));
    SimpleImage secondImage = new SimpleImage();
    secondImage.addStep(new ImageCreateModifier(1L, 20d));
    secondImage.addStep(new ImageInvertModifier());
    secondImage.addStep(new ImageContrastModifier(1.3d, 70d));
    baseImage.addStep(new ImageMultModifier(secondImage));
    baseImage.addStep(new ImageContrastModifier(1.6d, -100d));
    generationTest = new PolarResource(baseImage.resolve(2400, 800), 1, 1000);

    CompoundImage baseImage2 = new CompoundImage();
    baseImage2.addStep(new ImageCreateModifier(2L, 270d));
    baseImage2.addStep(new ImageContrastModifier(1.9d, -130d));
    SimpleImage secondImage2 = new SimpleImage();
    secondImage2.addStep(new ImageCreateModifier(2L, 170d));
    secondImage2.addStep(new ImageInvertModifier());
    secondImage2.addStep(new ImageContrastModifier(1.3d, 70d));
    baseImage2.addStep(new ImageMultModifier(secondImage2));
    baseImage2.addStep(new ImageContrastModifier(1.6d, -100d));
    generationTest2 = new PolarResource(baseImage2.resolve(2400, 800), 1, 1000);

    sphericalTest = new ImageResource(ImagePreprocessor.doubleImage(ImageGenerator.SphericalOpenSimplexNoise(1L, 70d, 2400)), 1, 1000);


    //resources

    //icons
    globeIcon = new ImageResource("/assets/images/icons/globeIcon.png");
    mapIcon = new ImageResource("/assets/images/icons/mapIcon.png");

    System.out.println("Loaded all resources");
  }
}
