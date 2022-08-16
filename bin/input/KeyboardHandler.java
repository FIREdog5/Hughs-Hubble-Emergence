package bin.input;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.jogamp.newt.event.KeyEvent;

public class KeyboardHandler{

  private Lock consumersLock;
  private ArrayList<IKeyConsumer> consumers;

  public KeyboardHandler() {
    this.consumersLock = new ReentrantLock();
    this.consumers = new ArrayList<IKeyConsumer>();
  }

  public void register(IKeyConsumer consumer) {
    consumersLock.lock();
    try {
      this.consumers.add(consumer);
      consumer.setKeyboardHandler(this);
    } finally {
      consumersLock.unlock();
    }
  }

  public void remove(IKeyConsumer consumer) {
    consumersLock.lock();
    try {
      this.consumers.remove(consumer);
    } finally {
      consumersLock.unlock();
    }
  }

  public void keyPressed(Key key) {
    consumersLock.lock();
    try {
      for (IKeyConsumer consumer : this.consumers) {
        consumer.keyDown(key);
      }
    } finally {
      consumersLock.unlock();
    }
  }

  public void keyReleased(Key key) {
    consumersLock.lock();
    try {
      for (IKeyConsumer consumer : this.consumers) {
        consumer.keyUp(key);
      }
    } finally {
      consumersLock.unlock();
    }
  }
}
