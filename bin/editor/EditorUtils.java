package bin.editor;

import bin.input.ClickHandler;
import bin.input.KeyboardHandler;
import bin.graphics.Color;

import bin.graphics.ui.UIScreen;
import bin.graphics.ui.UIBoxRow;
import bin.graphics.ui.UIBox;
import bin.graphics.ui.UIBoxCol;
import bin.graphics.ui.UICenter;
import bin.graphics.ui.UITextBlock;
import bin.graphics.ui.UIRightPositioner;
import bin.graphics.ui.UISelectable;

import bin.graphics.ui.complex.UISelectionMenu;
import bin.graphics.ui.complex.UINumberInput;


import javax.swing.JFileChooser;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Container;
import java.util.Scanner;
import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EditorUtils {

  private static ClickHandler clickHandler;
  private static KeyboardHandler keyboardHandler;
  private static UIScreen screen;

  public static void init(ClickHandler passedClickHandler, KeyboardHandler passedKeyboardHandler, UIScreen passedScreen) {
    clickHandler = passedClickHandler;
    keyboardHandler = passedKeyboardHandler;
    screen = passedScreen;
  }

  private static void disableNav(Container c) {
    for (Component x : c.getComponents())
      if (x instanceof JComboBox)
        ((JComboBox)x).setEnabled(false);
      else if (x instanceof JButton) {
        String text = ((JButton)x).getText();
        if (text == null || text.isEmpty())
          ((JButton)x).setEnabled(false);
        }
      else if (x instanceof Container)
        disableNav((Container)x);
  }

  public static String openResource(String path) {
    File dirToLock = new File("assets/generated" + path);
    Boolean old = UIManager.getBoolean("FileChooser.readOnly");
    UIManager.put("FileChooser.readOnly", Boolean.TRUE);
    JFileChooser jfc = new JFileChooser(dirToLock);
    disableNav(jfc);
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Planet Terrain", "json");
    jfc.setFileFilter(filter);
    jfc.requestFocusInWindow();
    JDialog wrapper = new JDialog((Window)null);
    wrapper.setVisible(true);
    wrapper.setAlwaysOnTop(true);
    wrapper.toFront();
    int returnValue = jfc.showOpenDialog(wrapper);
    wrapper.setVisible(false);
    UIManager.put("FileChooser.readOnly", old);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      try {
  			File selectedFile = jfc.getSelectedFile();
        Scanner fileReader = new Scanner(selectedFile);
        ArrayList<String> returnContent = new ArrayList<String>();
        while (fileReader.hasNextLine()) {
          returnContent.add(fileReader.nextLine());
        }
        return String.join("\n", returnContent);
      } catch (Exception e) {
        return openResource(path);
      }
    } else {
      return openResource(path);
    }
  }

  public static void saveResource(String path, String content) {
    File dirToLock = new File("assets/generated" + path);
    Boolean old = UIManager.getBoolean("FileChooser.readOnly");
    UIManager.put("FileChooser.readOnly", Boolean.TRUE);
    JFileChooser jfc = new JFileChooser(dirToLock);
    disableNav(jfc);
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Planet Terrain", "json");
    jfc.setFileFilter(filter);
    jfc.requestFocusInWindow();
    JDialog wrapper = new JDialog((Window)null);
    wrapper.setVisible(true);
    wrapper.setAlwaysOnTop(true);
    wrapper.toFront();
    int returnValue = jfc.showSaveDialog(wrapper);
    wrapper.setVisible(false);
    UIManager.put("FileChooser.readOnly", old);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      try {
  			File selectedFile = jfc.getSelectedFile();

        String filePath = selectedFile.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".json")) {
            selectedFile = new File(filePath + ".json");
        }

        try {
               FileWriter fileWriter = new FileWriter(selectedFile);
               fileWriter.write(content);
               fileWriter.close();
               return;
           } catch (IOException e) {
               e.printStackTrace();
           }
      } catch (Exception e) {
        saveResource(path, content);
        return;
      }
    } else {
      saveResource(path, content);
      return;
    }
  }

  public static void constructDropdown(UIBox fieldBox, UISelectionMenu field, String[] dropdownOptions, String title) {

    UIBoxRow fieldRow = new UIBoxRow(fieldBox);
    fieldRow.outlineWeight = 0;
    fieldRow.noBackground = true;
    fieldRow.padding = .3f;
    fieldBox.addChild(fieldRow);

    if (title != null) {
      UIBoxRow fieldLabelRow = new UIBoxRow(fieldRow);
      fieldLabelRow.minWidth = 10;
      fieldLabelRow.minHeight = 2f;
      fieldRow.outlineWeight = 0;
      fieldRow.noBackground = true;
      fieldRow.addChild(fieldLabelRow);

      UIRightPositioner fieldLabelPositioner = new UIRightPositioner(fieldLabelRow);
      fieldLabelRow.addChild(fieldLabelPositioner);

      UITextBlock titleText = new UITextBlock(fieldLabelPositioner, title, .5f);
      titleText.textColor = new Color("#ffffff");
      titleText.noBackground = true;
      titleText.maxWidth = 10f;
      titleText.y = .2f;
      fieldLabelPositioner.addChild(titleText);

      UIBoxRow fieldRowPadding = new UIBoxRow(fieldRow);
      fieldRowPadding.minWidth = .3f;
      fieldRowPadding.outlineWeight = 0;
      fieldRowPadding.noBackground = true;
      fieldRow.addChild(fieldRowPadding);
    }

    field.parent = fieldRow;
    fieldRow.addChild(field);
    clickHandler.register(field);

    if (field.minWidth == 0) {
      field.minWidth = 10;
    }
    field.minHeight = 2f;
    field.selectionBoxHeight = 6;
    field.color = new Color("#000000");
    field.mouseOverColor = new Color("#444444");
    field.selectedColor = new Color("#222222");
    field.mouseOverOutlineColor = new Color("#ffffff");
    field.selectedOutlineColor = new Color("#ffffff");
    field.outlineWeight = .1f;
    field.margin = .1f;

    for (String option : dropdownOptions) {
      UISelectable entry = new UISelectable(field.scrollableList);
      entry.x = 0;
      entry.y = 0;
      entry.minWidth = field.minWidth;
      entry.minHeight = 1.5f;
      entry.color = new Color("#222222");
      entry.mouseOverColor = new Color("#888888");
      entry.outlineWeight = 0f;
      entry.setZ(10);
      field.addOption(entry, option);
      clickHandler.register(entry);

      UICenter entryCenter = new UICenter(entry);
      entryCenter.centerY = true;
      entryCenter.centerX = false;
      entry.addChild(entryCenter);

      UIBoxRow entryContents = new UIBoxRow(entryCenter);
      entryContents.noBackground = true;
      entryContents.outlineWeight = 0f;
      entryCenter.addChild(entryContents);

      UIBoxRow entryPadding = new UIBoxRow(entryContents);
      entryPadding.minWidth = .1f;
      entryPadding.noBackground = true;
      entryPadding.outlineWeight = 0f;
      entryContents.addChild(entryPadding);

      UITextBlock entryText = new UITextBlock(entryContents, option, .5f);
      entryText.textColor = new Color("#ffffff");
      entryText.noBackground = true;
      entryText.maxWidth = 10f;
      entryContents.addChild(entryText);
    }
  }

  public static void constructNumField(UIBox fieldBox, UIBoxRow fieldRow, UINumberInput field, String title) {

    fieldRow.parent = fieldBox;
    fieldRow.outlineWeight = 0;
    fieldRow.noBackground = true;
    fieldRow.padding = .3f;
    fieldBox.addChild(fieldRow);

    if (title != null) {
      UIBoxRow fieldLabelRow = new UIBoxRow(fieldRow);
      fieldLabelRow.minWidth = 10;
      fieldLabelRow.minHeight = 2f;
      fieldRow.outlineWeight = 0;
      fieldRow.noBackground = true;
      fieldRow.children.add(0, fieldLabelRow);

      UIRightPositioner fieldLabelPositioner = new UIRightPositioner(fieldLabelRow);
      fieldLabelRow.addChild(fieldLabelPositioner);

      UITextBlock titleText = new UITextBlock(fieldLabelPositioner, title, .5f);
      titleText.textColor = new Color("#ffffff");
      titleText.noBackground = true;
      titleText.maxWidth = 10f;
      titleText.y = .2f;
      fieldLabelPositioner.addChild(titleText);

      UIBoxRow fieldRowPadding = new UIBoxRow(fieldRow);
      fieldRowPadding.minWidth = .3f;
      fieldRowPadding.outlineWeight = 0;
      fieldRowPadding.noBackground = true;
      fieldRow.children.add(1, fieldRowPadding);
    }

    clickHandler.register(field);
    keyboardHandler.register(field);

    field.minWidth = 10f - .4f - (1.7f / 2f - .6f) * 2f;;
    field.minHeight = 1.7f;
    field.color = new Color("#000000");
    field.textColor = new Color("#ffffff");
    field.mouseOverColor = new Color("#444444");
    field.selectedColor = new Color("#222222");
    field.mouseOverOutlineColor = new Color("#ffffff");
    field.selectedOutlineColor = new Color("#ffffff");
    field.outlineWeight = .1f;
    field.margin = .1f;

    field.incrementButton.mouseOverOutlineColor = new Color("#ffffff");
    field.incrementButton.outlineColor = new Color("#ffffff");
    field.incrementButton.color = new Color("#000000");
    field.incrementButton.mouseOverColor = new Color("#444444");
    field.incrementIcon.padding = .3f;
    field.incrementIcon.minWidth = (1.7f / 2f - .6f) * 2f;
    field.incrementIcon.minHeight = 1.7f / 2f - .6f;

    field.decrementButton.mouseOverOutlineColor = new Color("#ffffff");
    field.decrementButton.outlineColor = new Color("#ffffff");
    field.decrementButton.color = new Color("#000000");
    field.decrementButton.mouseOverColor = new Color("#444444");
    field.decrementIcon.padding = .3f;
    field.decrementIcon.minWidth = (1.7f / 2f - .6f) * 2f;
    field.decrementIcon.minHeight = 1.7f / 2f - .6f;
  }

}
