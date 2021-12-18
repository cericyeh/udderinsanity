import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;


// ----------------------------------------------------------------------
class Sun {
  // this is the timer object used in Udder, a sun traversing the top of the
  // screen.

  private final int SUN_START_X = -20;
  private final int SUN_Y = 0;
  public Point loc;

  public final static int ALL_COWS_DEAD_TIME = 50; // time to wait if all cows dead
  public final static int MAX_TIME = 900;
  public final static int ENDING_TIME = 50; // time at which all stuff stops
  public static int timeLeft = MAX_TIME;

  private final double SUN_X_INCREMENT = 0.5; // x increment per time step
  private double distanceTraversed = 0;
  private Image imageList[]; // ref to list of images

  // the "bell", or sound signifying day's end.  The bellPlayed flag
  // determines whether or not the ending bell has sounded.
  public AudioClip cricketSound;
  private boolean bellPlayed = false; 

  // state code
  private final int SUN_1 = 0;
  private final int SUN_2 = 1;
  public int state = SUN_1;


  public Sun(Image imgList[], AudioClip bellSound) {
    imageList = imgList; // set up ref to list of images
    loc = new Point(SUN_START_X,SUN_Y);
    cricketSound = bellSound;
    reset();
  }

  public void handle() {
    timeLeft--;
    distanceTraversed += SUN_X_INCREMENT;
    loc.x = (int)(distanceTraversed);
    if (state == SUN_1)
      state = SUN_2;
    else
      state = SUN_1;

    // if all cows dead, then jump time ahead to end the round quickly
    if ((Farm.cowCount <= 0) && (timeLeft > ALL_COWS_DEAD_TIME))
      timeLeft = ALL_COWS_DEAD_TIME;

    // play bell day is almost over
    if (timeLeft <= ENDING_TIME) {
      if (bellPlayed == false) {
	cricketSound.play();
	bellPlayed = true;
      }
    }
  }

  public void reset() {
    timeLeft = MAX_TIME;
    loc.x = SUN_START_X; loc.y = SUN_Y;
    state = SUN_1;
    distanceTraversed = 0;
    bellPlayed = false;
  }

  public static boolean endTime() {
    if (timeLeft <= ENDING_TIME)
      return true;
    else
      return false;
  }
   
  public void draw(Graphics g) {
    g.drawImage(imageList[state],loc.x,loc.y,null);
  }

}
