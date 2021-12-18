import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;


// canvas drawing routine and container
class LDrawArea extends Canvas {
  
  public Image loadedImage;
  Container myContainer;
  Dimension mySize;
  int myWidth, myHeight;
  boolean sizeKnown = false;
  
  // constructor
  public LDrawArea(Image newImage, Container parent,
		  int newWidth, int newHeight) {
    if (newImage == null) {
      System.err.println("image not valid");
      return;
    }
    myContainer = parent;
    this.loadedImage = newImage;
    myWidth = newWidth;
    myHeight = newHeight;
    mySize = new Dimension(myWidth, myHeight);
  }
  
  
  public Dimension preferredSize() {
    return minimumSize();
  }
  
  public synchronized Dimension minimumSize() {
    return mySize;
  }
  
  public void paint(Graphics g) {
    update(g);
  }
  
  public void update(Graphics g) {
    g = getGraphics();
    if (loadedImage != null) {
      int tWidth = loadedImage.getWidth(this);
      int tHeight = loadedImage.getHeight(this);
      if ((tWidth>0) && (tHeight>0)) {
	sizeKnown = true;
	myWidth = tWidth;
	myHeight = tHeight;
	mySize = new Dimension(tWidth, tHeight);
	resize(tWidth, tHeight);
	myContainer.validate();
      }
    }
    try {
      g.drawImage(loadedImage,0,0,this); }
    catch (NullPointerException e) {
    }
  }
  
  final void setImage(Image newImage){
    loadedImage = newImage;
  }

}
