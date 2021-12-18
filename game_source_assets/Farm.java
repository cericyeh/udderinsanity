import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;

// ----------------------------------------------------------------------
class Farm {
  // the world description
  // there are MAX_SLOT reference points.  All cast characters position
  // themselves at different offsets from the given point.
  public final static int BONUS_COW_SCORE =10000; // score you get new cows
  public static int nextCowBonusScore = BONUS_COW_SCORE; // score to get new cow 
  public static int bonusIncrement = BONUS_COW_SCORE; // add this to score to get nextCowBonusScore
  public final static int BONUS_INCREMENT = 5000; // inc the bonusIncrement
  public final static int MAX_BONUS_COW_SCORE = 30000; // add this to score to get nextCowBonusScore

  public static int score = 0; // score 
  public static int level = 0; // level

  public static final int WIDTH = 400;   // size of the world
  public static final int HEIGHT = 300;

  public final static int MAX_SLOTS = 11;// maximum number of possible slots
  public final static int FIRST_ROW_START = 1;
  public final static int FIRST_ROW_END = 5;
  public final static int SECOND_ROW_START = 5;
  public final static int SECOND_ROW_END = 9;
  public final static int GUN_SLOT = 10;
  public final static int STUMP_SLOT = 10;
  public final static int BUCKET_SLOT = 0;
  public static Point localeList[] = new Point[MAX_SLOTS];

  // cow info
  public final static int MAX_COWS = 8;     // maximum # of cows on screen
  public final static int INITIAL_COWS = 4; // initial # of cows on screen
  //  public static int maxCowCount = INITIAL_COWS; // max # of cows on screen
  public static int cowCount = INITIAL_COWS;  // the actual # of cows 
  public static int maxCowCount = INITIAL_COWS; // max # of cows on screen
  //  public static int cowCount = MAX_COWS;  // the actual # of cows 

  public static int INITIAL_COWS_LEFT = 8; // # of cows you can lose
  public static int cowsLeft = INITIAL_COWS_LEFT; // # of cows you can lose (lives)

  public Farm() {
    localeList[0] = new Point(20,80);
    for (int i=1; i<SECOND_ROW_START;i++) {
      localeList[i] = new Point(50 + i*80,80);
    }
    for (int i=SECOND_ROW_START; i<SECOND_ROW_END;i++) {
      //      localeList[i] = new Point(130 + (i-5)*80,160);
      localeList[i] = new Point(130 + (i-5)*80,155);
    }
    localeList[10] = new Point(30,140);
  }


  public static Point getLocale(int loc) {
    // given a locale index, returns the associated point.  If bound, returns
    // the original value.

    return localeList[loc];
  }

  public static int moveUp(int currPt) {
    // "move up" from current locale,and returns new locale
    if ((currPt >= SECOND_ROW_START) && (currPt < SECOND_ROW_END))
      return (FIRST_ROW_START + (currPt - SECOND_ROW_START));
    if (currPt == GUN_SLOT)
      return BUCKET_SLOT;
    return currPt;
  }

  public static int moveDown(int currPt) {
    // "move down" from current locale,and returns new locale
    if ((currPt >= FIRST_ROW_START) && (currPt < FIRST_ROW_END))
      return (SECOND_ROW_START + (currPt - FIRST_ROW_START));
    if (currPt == BUCKET_SLOT)
      return GUN_SLOT;
    return currPt;
  }

  public static int moveLeft(int currPt) {
    // "move left" from current locale,and returns new locale
    if (currPt == SECOND_ROW_START)
      return GUN_SLOT;
    if (currPt == GUN_SLOT)
      return GUN_SLOT;
    if (currPt == 0)
      return 0;
    //    System.out.println("move left");
    return --currPt;
  }

  public static int moveRight(int currPt) {
    // "move right" from current locale,and returns new locale
    if (currPt == SECOND_ROW_END - 1)
      return SECOND_ROW_END - 1;
    if (currPt == FIRST_ROW_END - 1)
      return FIRST_ROW_END - 1;
    if (currPt == GUN_SLOT) 
      return SECOND_ROW_START;
    return ++currPt;
  }

  public static void incCowsLeft() {
    // bonus cow!
    cowsLeft++;
  }

  public static void decCowsLeft() {
    cowsLeft--; // decrement # of cows left
    if (cowsLeft < 0)
      cowsLeft = 0;
  }

  public static void incBonusIncrement() {
    // set the inc to add to the bonus score level
    bonusIncrement += BONUS_INCREMENT;
    if (bonusIncrement > MAX_BONUS_COW_SCORE)
      bonusIncrement = MAX_BONUS_COW_SCORE;
  }

  public static void reset() {
    // resets all variables controlled by Farm
    score = 0;
    nextCowBonusScore = BONUS_COW_SCORE;
    bonusIncrement = BONUS_COW_SCORE;
    level = 0;
    maxCowCount = INITIAL_COWS;
    cowCount = INITIAL_COWS;
    cowsLeft = INITIAL_COWS_LEFT;

    /*    maxCowCount = 8;
    cowCount = 8;
    cowsLeft = INITIAL_COWS_LEFT; 
    level = 16;  */
  }

  public void draw(Graphics g) {
    g.setColor(Color.green);
    g.fillRect(0,0,WIDTH,HEIGHT);
  }

}
