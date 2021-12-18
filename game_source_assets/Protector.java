import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;

// implements protection variables
class Protector {
  private int value;
  
  public Protector(int val) {
    value = val;
  }

  public synchronized boolean P() {
    if (value < 0)
      value = 0;
    
    if (value > 0) { 
      value--;
      return true;
    }
    else
      return false;
  }

  public synchronized void V() {
    value++;
  }
}
