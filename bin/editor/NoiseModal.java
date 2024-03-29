package bin.editor;

import bin.world.Planet;
import bin.graphics.Renderer;
import bin.resource.palette.EditorPalette;
import bin.resource.palette.Palette;
import bin.resource.ImageResources;
import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.input.Key;
import bin.graphics.Color;
import bin.graphics.Shaders;
import bin.ClientMain;
import bin.Wrapper;
import bin.resource.ImageResource;
import bin.resource.GradientGenerator;
import bin.resource.ImageGenerator;
import bin.resource.ColorPosition;
import bin.resource.FastNoiseLite;
import bin.resource.FastNoiseLiteDomainWarp;

import bin.graphics.objects.Image;
import bin.graphics.objects.Global;
import bin.graphics.objects.Pointer;
import bin.graphics.objects.PointerOutline;

import bin.graphics.ui.UIScreen;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UIButton;
import bin.graphics.ui.UIImage;
import bin.graphics.ui.UITextBlock;
import bin.graphics.ui.UIElement;
import bin.graphics.ui.UIRightPositioner;
import bin.graphics.ui.UIBoxLayered;
import bin.graphics.ui.UISelectable;

import bin.graphics.ui.complex.UIToolTip;
import bin.graphics.ui.complex.UIModal;
import bin.graphics.ui.complex.UIVerticalValueSlider;
import bin.graphics.ui.complex.UISelectionMenu;
import bin.graphics.ui.complex.UINumberInput;
import bin.graphics.ui.complex.UIScrollableBox;
import bin.graphics.ui.colorUtils.UIColorWheel;
import bin.graphics.ui.colorUtils.UIColorSlider;
import bin.graphics.ui.colorUtils.UIGlobeDisplay;

import java.awt.image.BufferedImage;
import com.jogamp.opengl.GL2;
import java.awt.MouseInfo;
import java.awt.Point;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.jogamp.newt.event.KeyEvent;
import java.util.function.Consumer;

class NoiseModal {
  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;
  private static Palette editorPalette;

  public static void init(ClickHandler passedClickHandler, KeyboardHandler passedKeyboardHandler, UIScreen passedScreen, Wrapper<ImageResource> palettResourceWrapper) {
    clickHandler = passedClickHandler;
    keyboardHandler = passedKeyboardHandler;
    screen = passedScreen;
    editorPalette = new EditorPalette(palettResourceWrapper,
                                      new Color("#03fcf8"), //atmosphere lower
                                      new Color("#0000ff", .3f) //atmosphere upper
                                      );
  }

  public static void openNoiseModal() {
    FastNoiseLite fnl = new FastNoiseLiteDomainWarp();
    openNoiseModal(fnl, (FastNoiseLite newFnl) -> {});
  }

  public static void openNoiseModal(FastNoiseLite fnl) {
    openNoiseModal(fnl, (FastNoiseLite newFnl) -> {});
  }

  public static void openNoiseModal(FastNoiseLite fnl, Consumer<FastNoiseLite> afterClose) {

    Wrapper<ImageResource> noisePreviewWrapper = new Wrapper<ImageResource>();
    noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));

    UIModal noiseModal = new UIModal(screen);
    // noiseModal.centerBox.minWidth = 20f;
    // noiseModal.centerBox.minHeight = 20f;
    noiseModal.centerBox.color = new Color("#000000");
    noiseModal.centerBox.outlineColor = new Color("#ffffff");
    noiseModal.centerBox.outlineWeight = .1f;
    noiseModal.centerBox.radius = .5f;
    noiseModal.centerBox.margin = .5f;
    screen.addChild(noiseModal);
    clickHandler.setMask(noiseModal.centerBox);

    UIBoxRow modalContentRow = new UIBoxRow(noiseModal.centerBox);
    modalContentRow.outlineWeight = 0;
    modalContentRow.noBackground = true;
    noiseModal.centerBox.addChild(modalContentRow);

    UIBoxCol controls = new UIBoxCol(modalContentRow);
    controls.outlineWeight = 0;
    controls.noBackground = true;
    modalContentRow.addChild(controls);

    UITextBlock generalText = new UITextBlock(controls, "General", .5f);
    generalText.textColor = new Color("#ffffff");
    generalText.noBackground = true;
    generalText.maxWidth = 10f;
    controls.addChild(generalText);

    UISelectionMenu noiseTypeField = new UISelectionMenu(null, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetNoiseType()) {
          case OpenSimplex2:
            return "OpenSimplex2";
          case OpenSimplex2S:
            return "OpenSimplex2S";
          case Cellular:
            return "Cellular";
          case Perlin:
            return "Perlin";
          case ValueCubic:
            return "ValueCubic";
          case Value:
            return "Value";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "OpenSimplex2":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
            break;
          case "OpenSimplex2S":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
            break;
          case "Cellular":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
            break;
          case "Perlin":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            break;
          case "ValueCubic":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
            break;
          case "Value":
            fnl.SetNoiseType(FastNoiseLite.NoiseType.Value);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] noiseTypeFieldOptions = {
      "OpenSimplex2",
      "OpenSimplex2S",
      "Cellular",
      "Perlin",
      "ValueCubic",
      "Value"
    };

    EditorUtils.constructDropdown(controls, noiseTypeField, noiseTypeFieldOptions, "Noise Type");

    UISelectionMenu rotationType3DField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetRotationType3D()) {
          case None:
            return "None";
          case ImproveXYPlanes:
            return "ImproveXYPlanes";
          case ImproveXZPlanes:
            return "ImproveXZPlanes";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            fnl.SetRotationType3D(FastNoiseLite.RotationType3D.None);
            break;
          case "ImproveXYPlanes":
            fnl.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXYPlanes);
            break;
          case "ImproveXZPlanes":
            fnl.SetRotationType3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] rotationType3DFieldOptions = {
      "None",
      "ImproveXYPlanes",
      "ImproveXZPlanes"
    };

    EditorUtils.constructDropdown(controls, rotationType3DField, rotationType3DFieldOptions, "Rotation Type 3D");

    Wrapper<String> frequencyStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFrequency()));
    UIBoxRow frequencyFieldRow = new UIBoxRow(null);
    UINumberInput frequencyField = new UINumberInput(frequencyFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFrequency();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFrequency(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return frequencyStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        frequencyStringWrapper.set(val);
        return true;
      }
    };
    frequencyField.minValue = -1000;
    frequencyField.maxValue = 1000;
    frequencyField.incrementAmount = .005f;
    frequencyField.precision = 3;

    EditorUtils.constructNumField(controls, frequencyFieldRow, frequencyField, "Frequency");

    UITextBlock fractalText = new UITextBlock(controls, "Fractal", .5f);
    fractalText.textColor = new Color("#ffffff");
    fractalText.noBackground = true;
    fractalText.maxWidth = 10f;
    controls.addChild(fractalText);

    UISelectionMenu fractalTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetFractalType()) {
          case None:
            return "None";
          case FBm:
            return "FBm";
          case Ridged:
            return "Ridged";
          case PingPong:
            return "PingPong";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            fnl.SetFractalType(FastNoiseLite.FractalType.None);
            break;
          case "FBm":
            fnl.SetFractalType(FastNoiseLite.FractalType.FBm);
            break;
          case "Ridged":
            fnl.SetFractalType(FastNoiseLite.FractalType.Ridged);
            break;
          case "PingPong":
            fnl.SetFractalType(FastNoiseLite.FractalType.PingPong);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] fractalTypeFieldOptions = {
      "None",
      "FBm",
      "Ridged",
      "PingPong"
    };

    EditorUtils.constructDropdown(controls, fractalTypeField, fractalTypeFieldOptions, "Type");

    Wrapper<String> fractalOctavesStringWrapper = new Wrapper<String>(Integer.toString(fnl.GetFractalOctaves()));
    UIBoxRow fractalOctavesFieldRow = new UIBoxRow(null);
    UINumberInput fractalOctavesField = new UINumberInput(fractalOctavesFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          int oldVal = fnl.GetFractalOctaves();
          if (oldVal == Integer.parseInt(val)) {
            return;
          }
          fnl.SetFractalOctaves(Integer.parseInt(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalOctavesStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalOctavesStringWrapper.set(val);
        return true;
      }
    };
    fractalOctavesField.minValue = 0;
    fractalOctavesField.maxValue = 10;
    fractalOctavesField.incrementAmount = 1f;
    fractalOctavesField.precision = 0;

    EditorUtils.constructNumField(controls, fractalOctavesFieldRow, fractalOctavesField, "Octaves");

    Wrapper<String> fractalLacunarityStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalLacunarity()));
    UIBoxRow fractalLacunarityFieldRow = new UIBoxRow(null);
    UINumberInput fractalLacunarityField = new UINumberInput(fractalLacunarityFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalLacunarity();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalLacunarity(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalLacunarityStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalLacunarityStringWrapper.set(val);
        return true;
      }
    };
    fractalLacunarityField.minValue = -10;
    fractalLacunarityField.maxValue = 10;
    fractalLacunarityField.incrementAmount = .1f;
    fractalLacunarityField.precision = 2;

    EditorUtils.constructNumField(controls, fractalLacunarityFieldRow, fractalLacunarityField, "Lacunarity");

    Wrapper<String> fractalGainStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalGain()));
    UIBoxRow fractalGainFieldRow = new UIBoxRow(null);
    UINumberInput fractalGainField = new UINumberInput(fractalGainFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalGain();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalGain(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalGainStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalGainStringWrapper.set(val);
        return true;
      }
    };
    fractalGainField.minValue = -10;
    fractalGainField.maxValue = 10;
    fractalGainField.incrementAmount = .1f;
    fractalGainField.precision = 2;

    EditorUtils.constructNumField(controls, fractalGainFieldRow, fractalGainField, "Gain");

    Wrapper<String> fractalWeightedStrengthStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalWeightedStrength()));
    UIBoxRow fractalWeightedStrengthFieldRow = new UIBoxRow(null);
    UINumberInput fractalWeightedStrengthField = new UINumberInput(fractalWeightedStrengthFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalWeightedStrength();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalWeightedStrength(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalWeightedStrengthStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalWeightedStrengthStringWrapper.set(val);
        return true;
      }
    };
    fractalWeightedStrengthField.minValue = -1000;
    fractalWeightedStrengthField.maxValue = 1000;
    fractalWeightedStrengthField.incrementAmount = .1f;
    fractalWeightedStrengthField.precision = 2;

    EditorUtils.constructNumField(controls, fractalWeightedStrengthFieldRow, fractalWeightedStrengthField, "Weighted Strength");

    Wrapper<String> fractalPingPongStrengthStringWrapper = new Wrapper<String>(Float.toString(fnl.GetFractalPingPongStrength()));
    UIBoxRow fractalPingPongStrengthFieldRow = new UIBoxRow(null);
    UINumberInput fractalPingPongStrengthField = new UINumberInput(fractalPingPongStrengthFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetFractalPingPongStrength();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetFractalPingPongStrength(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return fractalPingPongStrengthStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        fractalPingPongStrengthStringWrapper.set(val);
        return true;
      }
    };
    fractalPingPongStrengthField.minValue = -100;
    fractalPingPongStrengthField.maxValue = 100;
    fractalPingPongStrengthField.incrementAmount = .1f;
    fractalPingPongStrengthField.precision = 2;

    EditorUtils.constructNumField(controls, fractalPingPongStrengthFieldRow, fractalPingPongStrengthField, "Ping Pong Strength");

    UITextBlock cellularText = new UITextBlock(controls, "Cellular", .5f);
    cellularText.textColor = new Color("#ffffff");
    cellularText.noBackground = true;
    cellularText.maxWidth = 10f;
    controls.addChild(cellularText);

    UISelectionMenu cellularDistanceFunctionField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetCellularDistanceFunction()) {
          case Euclidean:
            return "Euclidean";
          case EuclideanSq:
            return "EuclideanSq";
          case Manhattan:
            return "Manhattan";
          case Hybrid:
            return "Hybrid";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "Euclidean":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Euclidean);
            break;
          case "EuclideanSq":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.EuclideanSq);
            break;
          case "Manhattan":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Manhattan);
            break;
          case "Hybrid":
            fnl.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Hybrid);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] cellularDistanceFunctionFieldOptions = {
      "Euclidean",
      "EuclideanSq",
      "Manhattan",
      "Hybrid"
    };

    EditorUtils.constructDropdown(controls, cellularDistanceFunctionField, cellularDistanceFunctionFieldOptions, "Distance Function");

    UISelectionMenu cellularReturnTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetCellularReturnType()) {
          case CellValue:
            return "CellValue";
          case Distance:
            return "Distance";
          case Distance2:
            return "Distance2";
          case Distance2Add:
            return "Distance2Add";
          case Distance2Sub:
            return "Distance2Sub";
          case Distance2Mul:
            return "Distance2Mul";
          case Distance2Div:
            return "Distance2Div";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "CellValue":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);
            break;
          case "Distance":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance);
            break;
          case "Distance2":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2);
            break;
          case "Distance2Add":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Add);
            break;
          case "Distance2Sub":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Sub);
            break;
          case "Distance2Mul":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Mul);
            break;
          case "Distance2Div":
            fnl.SetCellularReturnType(FastNoiseLite.CellularReturnType.Distance2Div);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] cellularReturnTypeFieldOptions = {
      "CellValue",
      "Distance",
      "Distance2",
      "Distance2Add",
      "Distance2Sub",
      "Distance2Mul",
      "Distance2Div"
    };

    EditorUtils.constructDropdown(controls, cellularReturnTypeField, cellularReturnTypeFieldOptions, "Return Type");

    Wrapper<String> cellularJitterStringWrapper = new Wrapper<String>(Float.toString(fnl.GetCellularJitter()));
    UIBoxRow cellularJitterFieldRow = new UIBoxRow(null);
    UINumberInput cellularJitterField = new UINumberInput(cellularJitterFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetCellularJitter();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetCellularJitter(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return cellularJitterStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        cellularJitterStringWrapper.set(val);
        return true;
      }
    };
    cellularJitterField.minValue = -10;
    cellularJitterField.maxValue = 10;
    cellularJitterField.incrementAmount = .1f;
    cellularJitterField.precision = 2;

    EditorUtils.constructNumField(controls, cellularJitterFieldRow, cellularJitterField, "Jitter");

    UITextBlock domainWarpText = new UITextBlock(controls, "Domain Warp", .5f);
    domainWarpText.textColor = new Color("#ffffff");
    domainWarpText.noBackground = true;
    domainWarpText.maxWidth = 10f;
    controls.addChild(domainWarpText);

    UISelectionMenu domainWarpTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (fnl.GetDomainWarpType()) {
          case None:
            return "None";
          case OpenSimplex2:
            return "OpenSimplex2";
          case OpenSimplex2Reduced:
            return "OpenSimplex2Reduced";
          case BasicGrid:
            return "BasicGrid";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.None);
            break;
          case "OpenSimplex2":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2);
            break;
          case "OpenSimplex2Reduced":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.OpenSimplex2Reduced);
            break;
          case "BasicGrid":
            fnl.SetDomainWarpType(FastNoiseLite.DomainWarpType.BasicGrid);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] domainWarpTypeFieldOptions = {
      "None",
      "OpenSimplex2",
      "OpenSimplex2Reduced",
      "BasicGrid"
    };

    EditorUtils.constructDropdown(controls, domainWarpTypeField, domainWarpTypeFieldOptions, "Type");

    Wrapper<String> domainWarpAmplitudeStringWrapper = new Wrapper<String>(Float.toString(fnl.GetDomainWarpAmp()));
    UIBoxRow domainWarpAmplitudeFieldRow = new UIBoxRow(null);
    UINumberInput domainWarpAmplitudeField = new UINumberInput(domainWarpAmplitudeFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = fnl.GetDomainWarpAmp();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          fnl.SetDomainWarpAmp(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return domainWarpAmplitudeStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        domainWarpAmplitudeStringWrapper.set(val);
        return true;
      }
    };
    domainWarpAmplitudeField.minValue = -10000;
    domainWarpAmplitudeField.maxValue = 10000;
    domainWarpAmplitudeField.incrementAmount = 5f;
    domainWarpAmplitudeField.precision = 2;

    EditorUtils.constructNumField(controls, domainWarpAmplitudeFieldRow, domainWarpAmplitudeField, "Amplitude");

    Wrapper<String> domainWarpFrequencyStringWrapper = new Wrapper<String>(Float.toString(((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFrequency()));
    UIBoxRow domainWarpFrequencyFieldRow = new UIBoxRow(null);
    UINumberInput domainWarpFrequencyField = new UINumberInput(domainWarpFrequencyFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = ((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFrequency();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFrequency(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return domainWarpFrequencyStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        domainWarpFrequencyStringWrapper.set(val);
        return true;
      }
    };
    domainWarpFrequencyField.minValue = -1000;
    domainWarpFrequencyField.maxValue = 1000;
    domainWarpFrequencyField.incrementAmount = .005f;
    domainWarpFrequencyField.precision = 3;

    EditorUtils.constructNumField(controls, domainWarpFrequencyFieldRow, domainWarpFrequencyField, "Frequency");

    UISelectionMenu domainWarpRotation3DField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (((FastNoiseLiteDomainWarp) fnl).GetDomainWarpRotation3D()) {
          case None:
            return "None";
          case ImproveXYPlanes:
            return "ImproveXYPlanes";
          case ImproveXZPlanes:
            return "ImproveXZPlanes";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpRotation3D(FastNoiseLite.RotationType3D.None);
            break;
          case "ImproveXYPlanes":
            ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpRotation3D(FastNoiseLite.RotationType3D.ImproveXYPlanes);
            break;
          case "ImproveXZPlanes":
            ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpRotation3D(FastNoiseLite.RotationType3D.ImproveXZPlanes);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] domainWarpRotation3DFieldOptions = {
      "None",
      "ImproveXYPlanes",
      "ImproveXZPlanes"
    };

    EditorUtils.constructDropdown(controls, domainWarpRotation3DField, domainWarpRotation3DFieldOptions, "Rotation 3D");

    UITextBlock domainWarpFractalText = new UITextBlock(controls, "Domain Warp Fractal", .5f);
    domainWarpFractalText.textColor = new Color("#ffffff");
    domainWarpFractalText.noBackground = true;
    domainWarpFractalText.maxWidth = 10f;
    controls.addChild(domainWarpFractalText);

    UISelectionMenu domainWarpFractalTypeField = new UISelectionMenu(controls, screen, clickHandler){
      @Override
      public String getValue() {
        switch (((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalType()) {
          case None:
            return "None";
          case DomainWarpProgressive:
            return "DWarpProgressive";
          case DomainWarpIndependent:
            return "DWarpIndependent";
        }
        return "";
      }

      @Override
      public void setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return;
        }
        switch (val) {
          case "None":
            ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFractalType(FastNoiseLite.FractalType.None);
            break;
          case "DWarpProgressive":
            ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFractalType(FastNoiseLite.FractalType.DomainWarpProgressive);
            break;
          case "DWarpIndependent":
            ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFractalType(FastNoiseLite.FractalType.DomainWarpIndependent);
            break;
        }
        noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
      }
    };

    String[] domainWarpFractalTypeFieldOptions = {
      "None",
      "DWarpProgressive",
      "DWarpIndependent",
    };

    EditorUtils.constructDropdown(controls, domainWarpFractalTypeField, domainWarpFractalTypeFieldOptions, "Type");

    Wrapper<String> domainWarpFractalOctavesStringWrapper = new Wrapper<String>(Float.toString(((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalOctaves()));
    UIBoxRow domainWarpFractalOctavesFieldRow = new UIBoxRow(null);
    UINumberInput domainWarpFractalOctavesField = new UINumberInput(domainWarpFractalOctavesFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          int oldVal = ((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalOctaves();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFractalOctaves(Integer.parseInt(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return domainWarpFractalOctavesStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        domainWarpFractalOctavesStringWrapper.set(val);
        return true;
      }
    };
    domainWarpFractalOctavesField.minValue = 0;
    domainWarpFractalOctavesField.maxValue = 10;
    domainWarpFractalOctavesField.incrementAmount = 1f;
    domainWarpFractalOctavesField.precision = 0;

    EditorUtils.constructNumField(controls, domainWarpFractalOctavesFieldRow, domainWarpFractalOctavesField, "Octaves");

    Wrapper<String> domainWarpFractalLacunarityStringWrapper = new Wrapper<String>(Float.toString(((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalLacunarity()));
    UIBoxRow domainWarpFractalLacunarityFieldRow = new UIBoxRow(null);
    UINumberInput domainWarpFractalLacunarityField = new UINumberInput(domainWarpFractalLacunarityFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = ((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalLacunarity();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFractalLacunarity(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return domainWarpFractalLacunarityStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        domainWarpFractalLacunarityStringWrapper.set(val);
        return true;
      }
    };
    domainWarpFractalLacunarityField.minValue = -10;
    domainWarpFractalLacunarityField.maxValue = 10;
    domainWarpFractalLacunarityField.incrementAmount = .1f;
    domainWarpFractalLacunarityField.precision = 2;

    EditorUtils.constructNumField(controls, domainWarpFractalLacunarityFieldRow, domainWarpFractalLacunarityField, "Lacunarity");

    Wrapper<String> domainWarpFractalGainStringWrapper = new Wrapper<String>(Float.toString(((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalGain()));
    UIBoxRow domainWarpFractalGainFieldRow = new UIBoxRow(null);
    UINumberInput domainWarpFractalGainField = new UINumberInput(domainWarpFractalGainFieldRow, .5f, clickHandler){
      @Override
      public void onDeselect() {
        super.onDeselect();
        try {
          String val = this.getValue();
          Float oldVal = ((FastNoiseLiteDomainWarp) fnl).GetDomainWarpFractalGain();
          if (oldVal == Float.parseFloat(val)) {
            return;
          }
          ((FastNoiseLiteDomainWarp) fnl).SetDomainWarpFractalGain(Float.parseFloat(val));
          noisePreviewWrapper.set(new ImageResource(ImageGenerator.SphericalFNL(fnl, 2400), 1, 1000));
        } catch (Exception e) {
          return;
        }
      }

      @Override
      public String getValue() {
        return domainWarpFractalGainStringWrapper.get();
      }

      @Override
      public boolean setValue(String val) {
        String oldVal = this.getValue();
        if (oldVal.equals(val)) {
          return false;
        }
        domainWarpFractalGainStringWrapper.set(val);
        return true;
      }
    };
    domainWarpFractalGainField.minValue = -10;
    domainWarpFractalGainField.maxValue = 10;
    domainWarpFractalGainField.incrementAmount = .1f;
    domainWarpFractalGainField.precision = 2;

    EditorUtils.constructNumField(controls, domainWarpFractalGainFieldRow, domainWarpFractalGainField, "Gain");

    UIBoxCol previewCol = new UIBoxCol(modalContentRow);
    previewCol.outlineWeight = 0;
    previewCol.noBackground = true;
    modalContentRow.addChild(previewCol);

    UIImage noisePreview = new UIImage(previewCol, noisePreviewWrapper);
    noisePreview.minWidth = 31.4f;
    noisePreview.minHeight = 15.7f;
    previewCol.addChild(noisePreview);

    UICenter globeCenter = new UICenter(previewCol);
    globeCenter.centerY = false;
    previewCol.addChild(globeCenter);

    UIBoxRow globeRow = new UIBoxRow(globeCenter);
    globeRow.outlineWeight = 0;
    globeRow.noBackground = true;
    globeCenter.addChild(globeRow);

    UIGlobeDisplay grayGlobe = new UIGlobeDisplay(globeRow, noisePreviewWrapper, null);
    grayGlobe.setRadius(12);
    grayGlobe.padding = 1f;
    globeRow.addChild(grayGlobe);

    UIGlobeDisplay shadedGlobe = new UIGlobeDisplay(globeRow, noisePreviewWrapper, editorPalette);
    shadedGlobe.setRadius(12);
    shadedGlobe.padding = 1f;
    globeRow.addChild(shadedGlobe);

    UICenter modalButtonRowCenterer = new UICenter(noiseModal.centerBox);
    modalButtonRowCenterer.centerY = false;
    noiseModal.addChild(modalButtonRowCenterer);

    UIBoxRow modalButtonRow = new UIBoxRow(modalButtonRowCenterer);
    modalButtonRow.outlineWeight = 0;
    modalButtonRow.noBackground = true;
    modalButtonRowCenterer.addChild(modalButtonRow);

    UIButton confirmButton = new UIButton(modalButtonRow){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.popMask();
        noiseModal.close();
        afterClose.accept(fnl);
      }
    };
    confirmButton.color = new Color("#000000");
    confirmButton.outlineColor = new Color("#ffffff");
    confirmButton.outlineWeight = .1f;
    confirmButton.noBackground = false;
    confirmButton.padding = .5f;
    confirmButton.margin = .3f;
    confirmButton.mouseOverColor = new Color("00aa00");
    modalButtonRow.addChild(confirmButton);
    clickHandler.register(confirmButton);
    UITextBlock confirmButtonText = new UITextBlock(confirmButton, "Confirm", .5f);
    confirmButtonText.textColor = new Color("#ffffff");
    confirmButtonText.noBackground = false;
    confirmButton.addChild(confirmButtonText);

    UIButton cancelButton = new UIButton(modalButtonRow){
      @Override
      public void mousedUp(float x, float y) {
        super.mousedUp(x, y);
        clickHandler.popMask();
        noiseModal.close();
      }
    };
    cancelButton.color = new Color("#000000");
    cancelButton.outlineColor = new Color("#ffffff");
    cancelButton.outlineWeight = .1f;
    cancelButton.noBackground = false;
    cancelButton.padding = .5f;
    cancelButton.margin = .3f;
    cancelButton.mouseOverColor = new Color("aa0000");
    modalButtonRow.addChild(cancelButton);
    clickHandler.register(cancelButton);
    UITextBlock cancelButtonText = new UITextBlock(cancelButton, "Cancel", .5f);
    cancelButtonText.textColor = new Color("#ffffff");
    cancelButtonText.noBackground = false;
    cancelButton.addChild(cancelButtonText);
  }
}
