package bin.input;

import com.jogamp.newt.event.KeyEvent;

public class Key {

  private char kChar;
  private short vk;

  public Key(short vk) {
    this.kChar = '\0';
    this.vk = vk;
  }

  public Key(char kChar, short vk) {
    this.kChar = kChar;
    this.vk = vk;
  }

  public Key(KeyEvent e) {
    if (e.isPrintableKey()) {
      this.kChar = e.getKeyChar();
      this.vk = e.getKeySymbol();
    } else {
      this.kChar = '\0';
      this.vk = e.getKeySymbol();
    }
  }

  public short getVK() {
    return this.vk;
  }

  public char getChar() {
    return this.kChar;
  }

  public boolean isChar() {
    return (this.vk == 32) ||
           (this.vk == 39) ||
           (this.vk >= 44 && this.vk <= 57) ||
           (this.vk == 59) ||
           (this.vk == 61) ||
           (this.vk >= 65 && this.vk <= 93) ||
           (this.vk == 96);
  }

  public boolean isArrow() {
    return this.vk >= 149 && this.vk <= 152;
  }

  public boolean equals(short other) {
    return this.vk == other;
  }

  public boolean equals(char other) {
    return this.kChar != '\0' && this.kChar == other;
  }

  public boolean equals(Key other) {
    return other.equals(this.vk);
  }

}
