class BiomeBar {
  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;
  private static Wrapper<ImageResource> palettResourceWrapper = new Wrapper<ImageResource>();

  public static void init(ClickHandler passedClickHandler, KeyboardHandler passedKeyboardHandler, UIScreen passedScreen, Wrapper<ImageResource> passedPalettResourceWrapper) {
    clickHandler = passedClickHandler;
    keyboardHandler = passedKeyboardHandler;
    screen = passedScreen;
    palettResourceWrapper = passedPalettResourceWrapper;
  }

}
