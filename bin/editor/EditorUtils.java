package bin.editor;

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
import java.util.ArrayList;

public class EditorUtils {

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

  public static String openResource() {
    File dirToLock = new File("assets/generated");
    Boolean old = UIManager.getBoolean("FileChooser.readOnly");
    UIManager.put("FileChooser.readOnly", Boolean.TRUE);
    JFileChooser jfc = new JFileChooser(dirToLock);
    disableNav(jfc);
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Planet Terrain", "json");
    jfc.setFileFilter(filter);
    JDialog wrapper = new JDialog((Window)null);
    wrapper.setVisible(true);
    wrapper.setAlwaysOnTop(true);
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
        return openResource();
      }
    } else {
      return openResource();
    }
  }
}
