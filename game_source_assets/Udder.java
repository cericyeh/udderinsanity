import java.awt.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.applet.*;

/* --------------------------------------------------------------------------------
 *
 * MOD HISTORY
 * 2/22/01 EY - added in advertisement for the giveaways
 *
 * -------------------------------------------------------------------------------- */


// ----------------------------------------------------------------------
class HiScores {
  // contains the high scores
  public final static int MAX_HI_SCORES = 40; // max # of entries to view
  public String nameList[] = new String[MAX_HI_SCORES]; // listing of the names
  public String scoreList[] = new String[MAX_HI_SCORES];      // listing of corresponding scores
  public boolean available = false;
}




// ----------------------------------------------------------------------

public class Udder extends Applet implements Runnable {
  // declare the CGI base for the URLs here
  //  private String cgiURL = ""; // get it from the init routine
  //  private final String cgiURL = "http://www.rottentomatoes.com/game/udder/game_materials/"; // point to a specific  one

  private final String compareBase = "http://www.rottentomatoes.com/games/udder/game.php";

  boolean mediaLoaded = false; // whether or not all stuf is loaded
  Image offscreenBuffer; // offscreen double buffer
  Image backGroundBuffer; // buffer of current background image;
  MediaTracker tracker;
  int imageCount = 0; // keeps track of # of ids to watch out for
  int soundCount = 0; // keeps track of # of sounds
  int soundsLoaded = 0; // keeps track of # of sounds actually loaded

  // audio elements
  AudioClip longMoo, quickMoo, fastMoo, annoyedMoo, burstMoo,popSound;
  AudioClip milkSound, shotGunSound, noonieSound, tipsydaisySound,chachingSound;
  AudioClip tipperDieSound,dumpMilkSound,roosterSound,cricketSound;

  // UI elements
  LDrawArea mainView;
  boolean laidOut = false ; // whether or not UI is laid out

  // hiscore UI
  private final int CONGRATS_X = 50;
  private final int CONGRATS_Y = 130;

  private final int NAME_FIELD_CHAR_WIDTH = 20;
  private final int HI_SCORE_NAME_FIELD_CHAR_WIDTH = 15;
  private final int EMAIL_FIELD_CHAR_WIDTH = 50;

  private final int NAME_FIELD_LABEL_X = 10;
  private final int NAME_FIELD_LABEL_Y= 170;
  private final int NAME_FIELD_X = 80;
  private final int NAME_FIELD_Y = 155;
  private final int NAME_FIELD_WIDTH = 150;
  private final int NAME_FIELD_HEIGHT = 20;

  private final int EMAIL_FIELD_LABEL_X = 10;
  private final int EMAIL_FIELD_LABEL_Y = 195;
  private final int EMAIL_FIELD_X = 80;
  private final int EMAIL_FIELD_Y = 180;
  private final int EMAIL_FIELD_WIDTH = 150;
  private final int EMAIL_FIELD_HEIGHT = 20;

  private final int SUBMIT_BUTTON_X = 100;
  private final int SUBMIT_BUTTON_Y = 205;
  private final int SUBMIT_BUTTON_WIDTH = 70;
  private final int SUBMIT_BUTTON_HEIGHT = 20;

  //
  // 2/22/01 EY - add in click through button for sweepstakes ad
  //
  private final int AD_BUTTON_X = 160;
  private final int AD_BUTTON_Y = 35;
  private final int AD_BUTTON_WIDTH = 130;
  private final int AD_BUTTON_HEIGHT = 32;


  TextField nameField;  // submit name for highscore
  TextField emailField; // submit email
  Button submitButton;  // submit high score entry

  Color bgColor = new Color(70,200,70);

  // control constants
  private final int LEFT_ARROW_KEY = 1006;
  private final int RIGHT_ARROW_KEY = 1007;
  private final int UP_ARROW_KEY = 1004;
  private final int DOWN_ARROW_KEY = 1005;

  // main screen dimensions
  private final int SCREEN_WIDTH = 450;
  private final int SCREEN_HEIGHT = 250;

  private final int MAIN_WIDTH = 450;
  private final int MAIN_HEIGHT = 250;
 
  // drawing rectangle for the sky
  private final int SKY_WIDTH = 450;
  private final int SKY_HEIGHT = 130;

  // loading screen constants
  private final int LOADING_MESSAGE_X = 30;
  private final int LOADING_MESSAGE_Y = 20;

  // start and hiscore button info
  private final int START_BUTTON_X = (MAIN_WIDTH/2)-49;
  //  private final int START_BUTTON_Y = (MAIN_HEIGHT/2)+68;
  private final int START_BUTTON_Y = 196;
  private final int START_BUTTON_WIDTH = 98;
  private final int START_BUTTON_HEIGHT = 28;
  
  //  private final int HI_SCORE_BUTTON_X = 110;
  private final int HI_SCORE_BUTTON_X = 237;
  //  private final int HI_SCORE_BUTTON_Y = (MAIN_HEIGHT/2)+68;
  private final int HI_SCORE_BUTTON_Y = 196;
  private final int HI_SCORE_BUTTON_WIDTH = 98;
  private final int HI_SCORE_BUTTON_HEIGHT = 28;
  

  // loading bar
  private final int LOADING_BAR_X = 20;
  private final int LOADING_BAR_Y = 50;
  private final int LOADING_BAR_HEIGHT = 20;
  private final int LOADING_BAR_MAX_WIDTH = 400;

  //  private final int TITLE_X = (MAIN_WIDTH/2)-130;
  //  private final int TITLE_Y = (MAIN_HEIGHT/2)-40;
  //  private final int TITLE_Y = (MAIN_HEIGHT/2)-50;
  private final int TITLE_X = 114;
  private final int TITLE_Y = 71;

    private final int TITLE_INFO_X = (MAIN_WIDTH/2)-70;
    private final int TITLE_INFO_Y = (MAIN_HEIGHT/2)+80;


  private final int VIEW_HIS_INFO_X = (MAIN_WIDTH/2)-120;
  private final int VIEW_HIS_INFO_Y = (MAIN_HEIGHT/2)+100;

  private final int FINAL_SCORE_X = (MAIN_WIDTH/2) - 115;
  private final int FINAL_SCORE_Y = (MAIN_HEIGHT/2)+60;

  private final int BLURB_X = 90;   // coords of final blurb
  //  private final int BLURB_Y = (MAIN_HEIGHT/2)+102;
  private final int BLURB_Y = 30;

  //  private final int GAME_OVER_X = (MAIN_WIDTH/2)-100;
  //  private final int GAME_OVER_Y = (MAIN_HEIGHT/2)-50;
  private final int GAME_OVER_X = 132;
  private final int GAME_OVER_Y = 76;
  //  private final int GAME_OVER_Y = 10;

  // hi score display
  private final int SINGLE_DIGIT_LIMIT = 10; // entries before offset needed
  //  private final int TOP_SCORES_COUNT = 10; // how many scores to display each page
  //  private final int TOP_SCORE_PAGE_RANGE = 10; // entries per page
  private final int TOP_SCORES_COUNT = 8; // how many scores to display each page
  private final int TOP_SCORE_PAGE_RANGE = 8; // entries per page

  //  private final int TOP_SCORES_START_Y = 45;
  private final int TOP_SCORES_START_Y = 75;
  private final int TOP_SCORES_Y_INCREMENT = 15;
  private final int TOP_10_X = 139;
  //  private final int TOP_10_Y = 5;
  private final int TOP_10_Y = 35;
  private final int TOP_SCORE_NAME_X = 125;
  private final int TOP_SCORE_SCORE_X = 260;
  private final int TENTH_VALUE_OFFSET = 7; // offset to bump left for 10th score

  // rectangle to make it easier to see entries
  private Color topScoreRectColor = new Color(57,102,0);
  private Color topScoreBorderColor = new Color(51,51,0);
  private final int TOP_SCORE_RECT_X = 101;
  private final int TOP_SCORE_RECT_Y = 59;
  //  private final int TOP_SCORE_RECT_WIDTH = 250;
  private final int TOP_SCORE_RECT_WIDTH = 250;
  //  private final int TOP_SCORE_RECT_HEIGHT = TOP_SCORES_COUNT * TOP_SCORES_Y_INCREMENT + 8;
  //  private final int TOP_SCORE_RECT_HEIGHT =160;
  private final int TOP_SCORE_RECT_HEIGHT =130;

  private final int TOP_SCORE_BORDER_WIDTH = 4;

  Font topScoresFont = new Font("Arial",Font.PLAIN,12);
  Color topScoresFontColor = new Color(204,255,0);


  // ending blurb strings
  private final int BLURB_COUNT = 7;
  private String blurbList[] = {"no cows were harmed in the making of this game",
				"                       lactose intolerant? ",
				"         cow methane is killing the environment",
				"                             eat cheese",
				"                              eat vegan",
				"                               eat pork",
				"beef as a food source is extremely inefficient"};

  // trivia list {for beginning}
  private final int TRIVIA_X = 20;
  private final int TRIVIA_Y = 100;

  private final int TRIVIA_COUNT = 46;
  private final int TRIVIA_WAIT_TIME = 2000; // time to disp the trivia (msecs)
  long timeStamp;  // target time.  Once this is exceeded, go to next item
  int triviaChoice; // choice to display

  private String triviaList[] = {
    "Film is made from cow gelatin.",
    "Phonographic records are made from cows.",
    "A cow can drink 25-30 gallons of water a day.",
    "The weight of a cow is 1400 pounds on the average.",
    "No two Holstein cows have the same spot patterns.",
    "Cows were brought to America by European settlers.",
    "8% of a cow's energy from food is converted to methane.",
    "US EPA/USDA Ruminant Livestock Methane Program.",
    "You can lease dairy cows.",
    "The Jersey cow has the fattest milk of all cows.",
    "The Jersey cow can produce milk into it old age.",
    "The Illawarra cow makes milk and can be eaten too.",
    "The Illawarra cow makes lots of milk.",
    "Holstein-Friesian cows make more milk than other breeds.",
    "Male Holstein-Friesian cows can be used for beef (food).",
    "Guernsey cows are known for their docility.",
    "The fat content of Ayrshire cows milk is around 4-5%.",
    "Ayrshire cow milk is good for making cheese.",
    "Feb 18th, 1930: 1st cow to take flight and make milk.",
    "Cows explode if it is too hot.",
    "Cows explode if exposed to large amounts of current.",
    "The milk bottle was invented in 1884.",
    "Dr. Harvey Tatcher invented the milk bottle.",
    "The milk bottle was invented in Potssdam, New York.",
    "Louis Pasteur begins experimentation in 1856.",
    "A milk cow can make six pounds of butter in one day.",
    "Milk cows can make five gallons of ice cream in one day.",
    "Dairy cows can make 14 pounds of cheese in a day.",
    "Dairy cows can make 256 glases of milk in a day.",
    "In a day, a milk cow can eat 3 pounds of pulped beet.",
    "In a day, a milk cow can eat 4 pounds of wheat bran.",
    "In a day, a milk cow consumes 3/25 pounds of salt.",
    "Gail Borden builds first condensery in 1857.",
    "The Pilgrims took cows with them to America.",
    "Columbus took cows with him to America.",
    "Cows are not native to the Americas.",
    "Cows have four stomachs.",
    "The 1st stomach in a cow is the rumen.",
    "A cow's rumen helps it digest cellulose.",
    "A cow's rumen contains no digestive fluid.",
    "A cow's rumen contains bacteria to ferment cellulose.",
    "Cud is a bolus of fermented food from the rumen.",
    "The cow's true stomach is it's abomasum.",
    "The cow's second stomach is the reticulum.",
    "Angus cows are used primarily for beef.",
    "Murray Grey cows are good mothers."
  };


  // intermission stuff
  // cow count
  private final int INT_COWS_LEFT_X = 100;
  private final int INT_COWS_LEFT_Y = 130;



  // bonus
  private final int INT_BONUS_X = 100;
  private final int INT_BONUS_Y = 150;

  // total score
  private final int INT_SCORE_X = 100;
  private final int INT_SCORE_Y = 170;

  // bonus cow
  private final int INT_BONUS_COW_X = 100;
  private final int INT_BONUS_COW_Y = 200;
  private final int INT_BONUS_COW_IMAGE_X = 200;
  private final int INT_BONUS_COW_IMAGE_Y = 170;

  // stampede

  // add a timer in before stampede.  that way any death animations can
  // be completed before the stampede begins
  private final int STAMPEDE_COUNT_DOWN = 30;
  private int stampedeCountDown = 0;


  private final int STAMPEDE_MOO_CHANCE = 10;
  private final int STAMPEDE_TIME = 100;
  private final int STAMPEDE_COUNT = 10;
  private final int STAMPEDE_SPEED_RANGE = 50;
  private final int STAMPEDE_BASE_SPEED = 10;
  private final int STAMPEDE_START_X = MAIN_WIDTH;
  private final int STAMPEDE_START_Y = 70;
  private final int STAMPEDE_Y_RANGE = 100;
  private final int STAMPEDE_LEFT_BOUNDARY = -10;

  /*  private final int EASY_X = 70;
  private final int EASY_Y = MAIN_HEIGHT + 5;
  private final int EASY_WIDTH = 60;
  private final int EASY_HEIGHT = 20;
  private final int MED_X = 130;
  private final int MED_Y = MAIN_HEIGHT + 5;
  private final int MED_WIDTH = 60;
  private final int MED_HEIGHT = 20;
  private final int HARD_X = 190;
  private final int HARD_Y = MAIN_HEIGHT + 5;
  private final int HARD_WIDTH = 60;
  private final int HARD_HEIGHT = 20;
  private final int GAME_BUTTON_X = 355;
  private final int GAME_BUTTON_Y = MAIN_HEIGHT + 5;
  private final int GAME_BUTTON_WIDTH = 90;
  private final int GAME_BUTTON_HEIGHT = 25;
  Button easyButton, medButton, hardButton;
      Button gameButton; */

  //  Color backgroundColor = new Color(255,225,225);

  // vars 
  Thread animThread;
  long targetTime = 30; // time for the thread to sleep
  
  // offset of game display area
  private final int FIELD_OFFSET_X = 0;
  private final int FIELD_OFFSET_Y = 0;

  private final int MILK_BAR_WIDTH = 350;
  private final int MILK_BAR_HEIGHT = 14;

  //  private final int MILK_TEXT_X = 40;  // 'milk' next to bar
  //  private final int MILK_TEXT_Y = 243;
  private final int MILK_BAR_X = 70;   // status bar constants
  private final int MILK_BAR_Y = 234;
  private final int DUMP_MILK_MSG_X = 200;  // dump milk msg & flash cntdwn
  private final int DUMP_MILK_MSG_Y = 245;
  private final int DUMP_COUNT_DOWN = 30;
  private final int DUMP_DISPLAY_THRESHOLD = 15;
  private int dumpMilkTimer = DUMP_COUNT_DOWN;

  // game flow 
  private final int STAMPEDE = 3;
  private final int INTERMISSION = 2;
  private final int PLAYING = 1;
  private final int GAME_OVER = 0;
  private final int ENTER_HIGH_SCORE = 4; // entering high score

  // new game states (11/10/99)
  private final int PAUSED = 5;
  private final int DISPLAYING_CREDITS = 6;
  int gameState = GAME_OVER;

  // game over- different screen countdowns.  Hold each screen for the
  // gameOverTimer time and then switch.  The timer is set after the game
  // ends in handleGameOver.
  //
  // note: the game over screens are cycled in order from 0..MAX__.  
  private final int DISPLAY_GAME_OVER = 0;
  // a game over display for each of the game over ranges
  private final int DISPLAY_HI_SCORES_1 = 1;
  private final int DISPLAY_HI_SCORES_2 = 2;
  private final int DISPLAY_HI_SCORES_3 = 3;
  private final int DISPLAY_HI_SCORES_4 = 4;
  private final int DISPLAY_HI_SCORES_5 = 5;
  private final int DISPLAY_TITLE = 6;
  private final int MAX_GAME_OVER_SCREENS = 6; // cap on game over screens
  private final int GAME_OVER_DELAY_TIME = 100;
  private final int HI_SCORE_DISPLAY_TIME = 200; // time to dally over hiscores

  // modification 11/10/99
  private final int DISPLAY_CREDITS_TIME = 200; // time to dally over credits display

  private final int INITIAL_DISPLAY_TIME = 40;
  private final int APP_START_DISPLAY_TIME = 300;
  int gameOverTimer = APP_START_DISPLAY_TIME;
  int gameOverState = DISPLAY_TITLE;

  private final int SCORE_X = 320;
  private final int SCORE_Y = 20;
  private final int SCORE_WIDTH = 80;
  private final int SCORE_HEIGHT = 10;

  HiScores hiScoreList = new HiScores(); // listing of hiscores
  private final String NEW_HI_SCORE_STRING = new String("new high score");

  // font info
  Font scoreFont = new Font("Arial",Font.BOLD,14);
  Font creditFont = new Font("Arial",Font.BOLD,12);
  Font propsFont = new Font("Arial",Font.BOLD,10); // for listing our credits
  Font dumpMilkFont = new Font("Arial",Font.BOLD,12);
  Color textColor = new Color(30,30,0);


  // images
  private final int MAX_FARMER_IMAGES = 6;
  private final int MAX_COW_IMAGES = 28; // OKOK bad, I'll take care of it later
  private final int MAX_STAMPEDE_IMAGES = 2;
  private final int MAX_TIPPER_IMAGES = 7;
  private final int MAX_SUN_IMAGES = 2;
  Image farmerImages[] = new Image[MAX_FARMER_IMAGES];
  Image cowImages[] = new Image[MAX_COW_IMAGES];
  Image stampedeImages[] = new Image[MAX_STAMPEDE_IMAGES];
  Image tipperImages[] = new Image[MAX_TIPPER_IMAGES];
  Image sunImages[] = new Image[MAX_SUN_IMAGES];

  Image startButtonImage, hiScoreButtonImage;

  private final int STUMP_X_OFFSET = -10;
  private final int STUMP_Y_OFFSET = 20;

  private final int BUCKET_X_OFFSET = -15;
  private final int BUCKET_Y_OFFSET = 15;

  private final int ICON_X = 10;
  private final int ICON_Y = 5;

  private final int COWS_LEFT_X = 40;
  private final int COWS_LEFT_Y = 20;

  // day display
  private final int DAY_X = 200;
  private final int DAY_Y = 20;
  
  // sweepstakes button
  Image winButtonImage;
  
  Image bucketImage;
  Image stumpImage;
  Image backGroundImage;
  Image iconImage, gameOverImage, titleImage;
  Image topTenImage;

  String sunImageNameList[] = {"sun.gif","sun2.gif"};

  String farmerImageNameList[] = {"farmer.gif","fmilk1.gif","fmilk2.gif",
				  "dump.gif", "fgun.gif","fgun2.gif"};

  String cowImageNameList[] = {"cow11.gif","cow12.gif","cow21.gif","cow22.gif","cow31.gif",
			       "cow32.gif","cow41.gif","cow42.gif","cow51.gif","cow52.gif",
			       "cowd1.gif","cowd2.gif","cowd3.gif","cowd4.gif","cowd5.gif",
			       "cowm1.gif","cowm2.gif","cowm3.gif","cowm4.gif","cowm5.gif",
			       "cowm6.gif","cowtip1.gif","cowtip2.gif","cowtip3.gif",
			       "cowflip1.gif","cowflip2.gif","cowtipd1.gif","cowtipd2.gif"
  };

  String stampedeImageNameList[] = {"cows1.gif","cows2.gif"};

  String tipperImageNameList[] = {"tipper1.gif","tipper2.gif","tipperd1.gif",
			     "tipperd2.gif","tipperd3.gif","tipperd4.gif",
			     "tipperd5.gif"};
  

  // objects
  Sun sun; // the timer object
  Farmer hero;  // the farmer hero!
  Tipper badguy; // the ne-er well do-er
  MilkFairy fairy; // the milk fairy, bringer of milk

  //  Color skyColor = new Color(180,180,255); // the color of the sky

  // world loc description
  private final int COW_OFFSET = 1;
  private final int MILK_DUMP_POS = 0;
  Cow cowList[] = new Cow[Farm.MAX_COWS];
  Farm farm; // the game world specification

  //----------------------------------------------------------------------
  //
  //  Game initialization routines
  //
  // ----------------------------------------------------------------------

  // force load of images
  public void preloadImages() {
    // force JRE to load the image by displaying it into a temporary buffer
    Image dumpBuffer;
    
    dumpBuffer = createImage(30,30);
    Graphics g = dumpBuffer.getGraphics();
    for (int i=0;i<MAX_COW_IMAGES;i++)
      g.drawImage(cowImages[i],0,0,null);
    for (int i=0;i<MAX_STAMPEDE_IMAGES;i++)
      g.drawImage(stampedeImages[i],0,0,null);
    for (int i=0;i<MAX_FARMER_IMAGES;i++)
      g.drawImage(farmerImages[i],0,0,null);
    g.drawImage(bucketImage,0,0,null);
    g.drawImage(stumpImage,0,0,null);
    g.drawImage(backGroundImage,0,0,null);
    g.drawImage(iconImage,0,0,null);
    g.drawImage(gameOverImage,0,0,null);
    g.drawImage(titleImage,0,0,null);
    g.drawImage(topTenImage,0,0,null);
    g.drawImage(startButtonImage,0,0,null);
    g.drawImage(hiScoreButtonImage,0,0,null);

    // for sweepstakes
    g.drawImage(winButtonImage,0,0,null);
  }

  public void preloadSounds() {
    // preload the sounds by forcing the sound to play and then terminating it.
    // OK, really hackish, but dpn't have time now..
    longMoo.play();longMoo.stop();     displayLoading(++soundsLoaded,0);
    fastMoo.play();fastMoo.stop();     displayLoading(++soundsLoaded,0);
    quickMoo.play();quickMoo.stop();     displayLoading(++soundsLoaded,0);
    annoyedMoo.play();annoyedMoo.stop();     displayLoading(++soundsLoaded,0);
    burstMoo.play(); burstMoo.stop();     displayLoading(++soundsLoaded,0);
    milkSound.play();milkSound.stop();     displayLoading(++soundsLoaded,0);
    shotGunSound.play();shotGunSound.stop();     displayLoading(++soundsLoaded,0);
    noonieSound.play();noonieSound.stop();     displayLoading(++soundsLoaded,0);
    tipsydaisySound.play();tipsydaisySound.stop();     displayLoading(++soundsLoaded,0);
    chachingSound.play();chachingSound.stop();     displayLoading(++soundsLoaded,0);
    popSound.play(); popSound.stop();     displayLoading(++soundsLoaded,0);
    tipperDieSound.play();tipperDieSound.stop();     displayLoading(++soundsLoaded,0);
    dumpMilkSound.play();dumpMilkSound.stop();     displayLoading(++soundsLoaded,0);
    roosterSound.play();roosterSound.stop();     displayLoading(++soundsLoaded,0);
    cricketSound.play();cricketSound.stop();     displayLoading(++soundsLoaded,0);
  }


  public void loadMedia() {
    // load in the sounds
    longMoo = getAudioClip(getCodeBase(),"long_moo.au");
    fastMoo = getAudioClip(getCodeBase(),"fast_moo.au");
    quickMoo = getAudioClip(getCodeBase(),"quick_moo.au");
    annoyedMoo = getAudioClip(getCodeBase(),"annoyed.au");    
    burstMoo = getAudioClip(getCodeBase(),"die1.au");
    milkSound = getAudioClip(getCodeBase(),"milk.au");
    shotGunSound = getAudioClip(getCodeBase(),"shotgun.au");
    noonieSound = getAudioClip(getCodeBase(),"noonie.au");
    chachingSound = getAudioClip(getCodeBase(),"chaching.au");
    tipsydaisySound = getAudioClip(getCodeBase(),"tipsydaisy.au");
    popSound = getAudioClip(getCodeBase(),"pop.au");
    tipperDieSound = getAudioClip(getCodeBase(),"tipperdiesound.au");
    dumpMilkSound = getAudioClip(getCodeBase(),"dumpMilk.au");
    roosterSound = getAudioClip(getCodeBase(),"rooster.au");
    cricketSound = getAudioClip(getCodeBase(),"cricket.au");

    soundCount = 15; // total # of sounds

    // set up the refs
    Cow.longMoo = longMoo; Cow.burstMoo = burstMoo;
    Cow.popSound = popSound;
    Cow.annoyedMoo = annoyedMoo;
    Cow.quickMoo = quickMoo;
    Cow.fastMoo = fastMoo;
    Tipper.noonieSound = noonieSound;
    Tipper.chachingSound = chachingSound;
    Tipper.tipsydaisySound = tipsydaisySound;
    Tipper.dieSound = tipperDieSound;

    // load in the images
    tracker = new MediaTracker(this);

    for (int curr = 0; curr < MAX_SUN_IMAGES;curr++) {
      //      System.out.println("curr="+curr+" "+sunImageNameList[curr]);
      sunImages[curr] = getImage(getCodeBase(), "./"+sunImageNameList[curr]);
      tracker.addImage(sunImages[curr],imageCount++);
    }

    for (int curr = 0; curr < MAX_FARMER_IMAGES; curr++) {
      //      System.out.println("curr="+curr+" "+farmerImageNameList[curr]);
      farmerImages[curr] = getImage(getCodeBase(), "./"+farmerImageNameList[curr]);
      tracker.addImage(farmerImages[curr],imageCount++);
    }

    for (int curr = 0; curr < MAX_COW_IMAGES; curr++) {
      //      System.out.println("curr="+curr+" "+cowImageNameList[curr]);
      cowImages[curr] = getImage(getCodeBase(), "./"+cowImageNameList[curr]);
      tracker.addImage(cowImages[curr],imageCount++);
    }

    for (int curr = 0; curr < MAX_STAMPEDE_IMAGES; curr++) {
      //      System.out.println("curr="+curr+" "+cowImageNameList[curr]);
      stampedeImages[curr] = getImage(getCodeBase(), "./"+stampedeImageNameList[curr]);
      tracker.addImage(stampedeImages[curr],imageCount++);
    }

    for (int curr = 0; curr < MAX_TIPPER_IMAGES; curr++) {
      //      System.out.println("curr="+curr+" "+tipperImageNameList[curr]);
      tipperImages[curr] = getImage(getCodeBase(), "./"+tipperImageNameList[curr]);
      tracker.addImage(tipperImages[curr],imageCount++);
    }

    bucketImage = getImage(getCodeBase(),"./"+"bucket.gif");
    stumpImage = getImage(getCodeBase(),"./"+"stump.gif");
    backGroundImage = getImage(getCodeBase(),"./"+"backgnd.jpg");
    iconImage = getImage(getCodeBase(),"./"+"icon.gif");
    gameOverImage = getImage(getCodeBase(),"./"+"gameover.gif");
    titleImage = getImage(getCodeBase(),"./"+"title.gif");
    topTenImage = getImage(getCodeBase(),"./"+"top_ten.gif");
    startButtonImage = getImage(getCodeBase(),"./"+"button_start.gif");
    hiScoreButtonImage = getImage(getCodeBase(),"./"+"button_hi_score.gif");

    // for sweepstakes
    winButtonImage = getImage(getCodeBase(),"./"+"win_button.gif");

    tracker.addImage(bucketImage,imageCount++);
    tracker.addImage(stumpImage,imageCount++);
    tracker.addImage(backGroundImage,imageCount++);
    tracker.addImage(iconImage,imageCount++);
    tracker.addImage(titleImage,imageCount++);
    tracker.addImage(startButtonImage,imageCount++);
    tracker.addImage(hiScoreButtonImage,imageCount++);
    tracker.addImage(topTenImage,imageCount++);

    // add in sweepstakes button
    tracker.addImage(winButtonImage,imageCount++);

    tracker.addImage(gameOverImage,imageCount);


    preloadImages();
  }


  // ----------------------------------------------------------------------
  //
  // loading and status display routines
  //
  // ----------------------------------------------------------------------

  private final void displayLoading(int sndLoadedVal, int imgLoadedVal) {
    // based on the ID loaded up to, display the output
    //        System.out.println("snd = "+sndLoadedVal+" img = "+imgLoadedVal+" maxImages = "+imageCount);
    Graphics g = offscreenBuffer.getGraphics();
    g.setColor(bgColor);
    g.fillRect(0,0,MAIN_WIDTH, MAIN_HEIGHT); 
    g.setColor(Color.black);
    g.setFont(scoreFont);
    g.drawString("Loading sounds and images...", LOADING_MESSAGE_X, LOADING_MESSAGE_Y);
    displayLoadingBar(imageCount+soundCount,sndLoadedVal+imgLoadedVal);

    // modification 11/10/99
    // give props where due
    g.setFont(creditFont);
    g.drawString("Art by John Joh",30,180);
    g.drawString("Program and Concept by Eric Yeh",30,200);
    g.drawString("Copyright Rotten Tomatoes 1998,1999,2000",30,220);
    g.drawString("http://www.rottentomatoes.com",30,240);

    boolean newMessage = false; // flag to trigger new message
    // get current time
    
    if (timeStamp > 0) {
      long currTime = (new Date()).getTime();
      if (currTime >= timeStamp) {
	newMessage = true;
	timeStamp = currTime + TRIVIA_WAIT_TIME;
      }
    } else { 
      // 1st instance
      timeStamp = (new Date()).getTime();
      timeStamp += TRIVIA_WAIT_TIME;
      newMessage = true; // 1st instance
    }

    if (newMessage) {
      // now choose a new random trivia element to plop out
       triviaChoice = (int) (Math.random() * TRIVIA_COUNT);
    }

    g.setColor(Color.black);
    g.setFont(creditFont);
    g.drawString(triviaList[triviaChoice],TRIVIA_X,TRIVIA_Y);

    mainView.setImage(offscreenBuffer);
    mainView.update(this.getGraphics());
  }

  private final void displayLoadingBar(int maxVal, int currVal) {
    // draws the media load bar, maxVal = max index, currVal = the current ID up to.
    // currVal / maxVal = fraction to display
   Graphics g = offscreenBuffer.getGraphics();
   g.setColor(Color.black);
   g.fillRect(LOADING_BAR_X,LOADING_BAR_Y,LOADING_BAR_MAX_WIDTH,LOADING_BAR_HEIGHT);
   
   // now determine width filled up to
   double percentLoaded = (double)currVal / (double)maxVal;

   int drawWidth = (int) (percentLoaded * LOADING_BAR_MAX_WIDTH); // get a value to draw to
   //   System.out.println("percentLoaded = "+percentLoaded+" width = "+drawWidth);
   // now draw the new rectangle
   g.setColor(Color.white);
   g.fillRect(LOADING_BAR_X,LOADING_BAR_Y,drawWidth,LOADING_BAR_HEIGHT);
  }


  // --------------------------------------------------
  // displays the title
  // --------------------------------------------------
  private final void displayTitle() {
    Graphics g = offscreenBuffer.getGraphics();

    g.drawImage(backGroundImage,0,0,null);

    g.drawImage(titleImage,TITLE_X,TITLE_Y,null);

    g.drawImage(startButtonImage,START_BUTTON_X,START_BUTTON_Y,null);

    if (hiScoreList.available) 
      g.drawImage(hiScoreButtonImage,HI_SCORE_BUTTON_X,HI_SCORE_BUTTON_Y,null);

    // modification 11/10/99
    // give props where due
    g.setColor(topScoreRectColor);
    g.fillRect(0,0,450,30);

    g.setFont(propsFont);
    g.setColor(topScoresFontColor);
    g.drawString("Art by John Joh    - - -    Program and Concept by Eric Yeh",85,10);
    g.drawString("Copyright Rotten Tomatoes 1998,1999,2000 - - - http://www.rottentomatoes.com",40,20);

    longMoo.play();
    mainView.setImage(offscreenBuffer);
    mainView.update(this.getGraphics());
  }



  // ----------------------------------------------------------------------
  //
  // UI layout routines
  //
  // ----------------------------------------------------------------------

  private final void layOutUI() {
    // set layout (absolute)
    Insets inset = insets();
    try {
      System.out.println("layout out ui");
      mainView.reshape(inset.left + FIELD_OFFSET_X, 
		       inset.top + FIELD_OFFSET_Y,
		       MAIN_WIDTH, 
		       MAIN_HEIGHT);

      nameField.reshape(inset.left + NAME_FIELD_X,
			inset.top + NAME_FIELD_Y,
			NAME_FIELD_WIDTH,
			NAME_FIELD_HEIGHT);

      emailField.reshape(inset.left + EMAIL_FIELD_X,
			 inset.top + EMAIL_FIELD_Y,
			 EMAIL_FIELD_WIDTH,
			 EMAIL_FIELD_HEIGHT);

      submitButton.reshape(inset.left + SUBMIT_BUTTON_X,
			   inset.top + SUBMIT_BUTTON_Y,
			   SUBMIT_BUTTON_WIDTH,
			   SUBMIT_BUTTON_HEIGHT);  
      

    } catch (NullPointerException e) {
      
    }
    laidOut = true;
  }

  private final void initUI() {
    //    System.out.println("instantiating UI");
    mainView = new LDrawArea(offscreenBuffer, this, MAIN_WIDTH, 
			       MAIN_HEIGHT);

    nameField = new TextField(NAME_FIELD_CHAR_WIDTH);
    emailField = new TextField(EMAIL_FIELD_CHAR_WIDTH);
    submitButton = new Button("submit");


    Font buttonFont = new Font("Arial",Font.BOLD,12);
    Color buttonColor = new Color(255,200,200);
    Color textColor = new Color(15,10,10); 


    // now set the colors and fonts for the button
    submitButton.setFont(buttonFont);
    submitButton.setBackground(buttonColor);
    submitButton.setForeground(textColor);
    
    setLayout(null);
    add(mainView);
    add(nameField);
    add(emailField);
    add(submitButton);

	  /*    nameField.hide();
		emailField.hide();
		submitButton.hide(); */
  }
  
  // initialization
  public void init() {
    System.out.println("initializing...");

    System.out.println("Udder Insanity \n");
    System.out.println("by Eric Yeh\n");
    System.out.println("copyright 1998,1999,2000 Rotten Tomatoes\n");
    System.out.println("You may not copy, redistribute,\n");
    System.out.println("reframe, re-link, nor sell this applet etc.. without\n");
    System.out.println("my explicit written permission \n");    


    // compare codebase.  If succeed, continue with the initialization
    String currBase = getDocumentBase().toString();
    //    System.out.println("currBase="+currBase+"\n");

    //    if (!compareBase.equalsIgnoreCase(currBase)) {
    if (false) {
      System.out.println("failed to access multimedia resources \n");
    } else {
      // finish init
      //    cgiURL = getCodeBase().toString(); // get it directly from the site
      
      // create the offscreen buffers
      offscreenBuffer = createImage(MAIN_WIDTH, MAIN_HEIGHT);
      backGroundBuffer = createImage(MAIN_WIDTH,MAIN_HEIGHT);
      
      loadMedia();  // load in sounds and images
      
      initUI();  // initialize and layout the UI
      //    layOutUI();
      
      // create game objects
      createFarm();
      createHero();
      createBadGuy();
      fairy = new MilkFairy();
      sun = new Sun(sunImages,cricketSound);
      
      gameState = GAME_OVER;
      
      System.out.println("init finished\n");
    }
  }

  private final void incrementLevel() {
    Farm.level++;
    Farm.maxCowCount++;
    if (Farm.maxCowCount > Farm.MAX_COWS)
      Farm.maxCowCount = Farm.MAX_COWS;
  }

  private final void setupRound() {
    // sets up the round
    createCows();
    Farm.cowCount = Farm.maxCowCount; // reset cow count
    sun.reset();  // reset timer
    badguy.reset();
    badguy.setCowList(cowList);
    fairy.setCowList(cowList);
    fairy.reset();
    // empty milk and position farmer
    hero.reset();
  }

  // create the farm
  public void createFarm() {
    //    System.out.println("instantiating farm\n");
    farm = new Farm();
  }

  // create the farmer
  public void createHero() {
    //    System.out.println("instantiating Farmer\n");
    hero = new Farmer(0,farmerImages);
  }

  // create the bad guy
  public void createBadGuy() {
    //    System.out.println("instantiating tipper\n");
    badguy = new Tipper(tipperImages,backGroundBuffer);
    badguy.appear = true;
  }

  // create the cows
  public void createCows() {
    //    System.out.println("instantiating cows\n");
    for (int i=0; i<Farm.maxCowCount;i++) {
      cowList[i] = new Cow(i+COW_OFFSET, cowImages);
    }
  }

  //----------------------------------------------------------------------
  //
  //           highscore mechanism
  //
  //----------------------------------------------------------------------

  public void getHiScore() {
    // sets the hiScore object after requesting it
    URL myURL; URLConnection myConn;
    DataOutputStream outStream; DataInputStream inStream;
    String entry;  // response holder
 
    try {
      // set up the URL 
      // myURL = new URL(getCodeBase().toString() + "getHiScores.asp");
      //      myURL = new URL( cgiURL+"getHiScores.php");
      myURL = new URL( getCodeBase().toString() +"getHiScores.php");
      
      myConn = myURL.openConnection();
      
      // specify type of content to expect
      myConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

      // set up prefs
      myConn.setUseCaches(false);  // don't cache
      myConn.setDoInput(true); myConn.setDoOutput(true);

      // now open stream
      outStream = new DataOutputStream(myConn.getOutputStream());
      outStream.flush();outStream.close();  // close output stream (to allow cgi to process)
      
      // now get incoming
      inStream = new DataInputStream(myConn.getInputStream());

      int lineRead = 0;
      while ((entry = inStream.readLine()) != null) {
	//	System.out.println(entry);
	if (lineRead < HiScores.MAX_HI_SCORES) {
	  // read in the highscores, delimit on '+', newlines, and tabs
	  StringTokenizer analyzer = new StringTokenizer(entry,"+\n\t");
	  if (analyzer.hasMoreTokens()) {
	    String token = analyzer.nextToken(); // get name
	    hiScoreList.nameList[lineRead] = token; 
	  } else {
	    hiScoreList.nameList[lineRead] = "no-entry";
	  }
	  if (analyzer.hasMoreTokens()) {
	    String token = analyzer.nextToken(); // get score
	    hiScoreList.scoreList[lineRead] = token;
	  } else {
	    hiScoreList.scoreList[lineRead] = "";
	  }
	} 
	lineRead++;
      }
      inStream.close();
      hiScoreList.available = true; // high scores now valid
    }
    catch (MalformedURLException e)  {
      System.out.println("bad URL "+e);
      hiScoreList.available = false;
    }
    catch (IOException e) {
      System.out.println("IOException ="+e.getMessage());
      hiScoreList.available = false;
    }
  }


  public boolean checkHiScore() {
    URL myURL; URLConnection myConn;
    DataOutputStream outStream; DataInputStream inStream;
    String entry;  // response holder
    boolean newHighScore = false;
 
    try {
      // set up the URL 
      //      myURL = new URL(getCodeBase().toString() + "checkHiScore.asp");
      //      myURL = new URL(cgiURL+"checkHiScore.php");
      myURL = new URL(getCodeBase().toString()+"checkHiScore.php");
      myConn = myURL.openConnection();
      
      // specify type of content to expect
      myConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

      // set up prefs
      myConn.setUseCaches(false);  // don't cache
      myConn.setDoInput(true); myConn.setDoOutput(true);

      // now open stream
      outStream = new DataOutputStream(myConn.getOutputStream());
      String request = "score_val="+URLEncoder.encode(""+Farm.score);

      outStream.writeBytes(request);        // write out the request via POST
      outStream.flush();outStream.close();  // close output stream (to allow cgi to process)
      
      // now get incoming
      inStream = new DataInputStream(myConn.getInputStream());

      // read response
      entry = inStream.readLine();
      //      System.out.println(entry);
      if (entry.compareTo(NEW_HI_SCORE_STRING) == 0)
	newHighScore = true;

      inStream.close(); // close stream
    }
    catch (MalformedURLException e)  {
      System.out.println("bad URL "+e);
      hiScoreList.available = false;
      newHighScore = false;
    }
    catch (IOException e) {
      System.out.println("IOException ="+e.getMessage());
      hiScoreList.available = false;
      newHighScore = false;
    }
    return newHighScore; // return whether or not a new high score has been entered or not
  }


  public void submitHiScore() {
    URL myURL; URLConnection myConn;
    DataOutputStream outStream; DataInputStream inStream;
    String entry;  // response holder
 
    try {
      // set up the URL 
      //      myURL = new URL(getCodeBase().toString() + "newHighScoreTest.asp");
      //      myURL = new URL(cgiURL + "newHighScore.php");      
      myURL = new URL(getCodeBase().toString() + "newHighScore.php");      
      myConn = myURL.openConnection();
      
      // specify type of content to expect
      myConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

      // set up prefs
      myConn.setUseCaches(false);  // don't cache
      myConn.setDoInput(true); myConn.setDoOutput(true);

      // now open stream
      outStream = new DataOutputStream(myConn.getOutputStream());
      // format the submission text
      String request = "name="+URLEncoder.encode(nameField.getText())+"&"+
	"score_val="+URLEncoder.encode(""+Farm.score)+"&"+
	"email="+URLEncoder.encode(""+emailField.getText()) + "&" +
	"udderVal="+URLEncoder.encode("swinging udders part 2");

      outStream.writeBytes(request);        // write out the request via POST
      outStream.flush();outStream.close();  // close output stream (to allow cgi to process)

      // now read in debug info and dump it to console
      inStream = new DataInputStream(myConn.getInputStream());
	      
      // the below may seem strange, but Netscape seems to want this in order for the
      // script to reach completion.
      while ((entry = inStream.readLine()) != null) 
	; // eat up entries
    }
    catch (MalformedURLException e)  {
      System.out.println("bad URL "+e);
      hiScoreList.available = false;
    }
    catch (IOException e) {
      System.out.println("IOException ="+e.getMessage());
      hiScoreList.available = false;
    }
  }

  private final void processHighScores() {
    //    System.out.println("getting hiscore request...\n");
    // sends score over to server to see if it deserves a spot in top 10
    if (checkHiScore() == true) {
      System.out.println("new high score!");
      // now enter the high score mechanism
      // first draw in message onto backgroudn buffer
      Graphics g = backGroundBuffer.getGraphics();
      g.setFont(scoreFont);
      //      g.setColor(Color.black);
      g.setColor(textColor);
      g.drawString("Congratulations, you made the top 40!",CONGRATS_X,CONGRATS_Y);
      g.drawString("name",NAME_FIELD_LABEL_X,NAME_FIELD_LABEL_Y);
      g.drawString("email",EMAIL_FIELD_LABEL_X,EMAIL_FIELD_LABEL_Y);
      
      startEnterHiScore();
    } 

    //    System.out.println("finished highscore check");
    // test- get and display the highscores
    getHiScore();
    /*        for (int i=0;i<HiScores.MAX_HI_SCORES;i++) {
      System.out.println("name="+hiScoreList.nameList[i]+
			 " "+hiScoreList.scoreList[i]);
    }*/
  }

  private final void displayEnterHiScore(Graphics g) {
    // draws the highscore screen
    drawBackGround(g);
  }

  private final void startEnterHiScore() {
    // hides mainView and sets others visible. Sets state too.
    gameState = ENTER_HIGH_SCORE;
    mainView.setVisible(false);
    nameField.setVisible(true);
    emailField.setVisible(true);
    submitButton.setVisible(true); 
    update(this.getGraphics());

    /*    mainView.hide();
	  nameField.show();
	  emailField.show();
	  submitButton.show();*/
    //    update(getGraphics()); 
  }

  private final void exitEnterHiScore() {
    // restores conditions after leaving hiscores
    gameState = GAME_OVER;
    mainView.setVisible(false); // try deactivating 
    nameField.setVisible(false);
    emailField.setVisible(false);
    submitButton.setVisible(false);

    /*    mainView.show();
    nameField.hide();
    emailField.hide();
    submitButton.hide(); */
    //    update(getGraphics());
  }

  //----------------------------------------------------------------------
  //
  //  Game thread & running routines
  //
  // ----------------------------------------------------------------------

  private final void displayStampede() {
    Point cowArray[] = new Point[STAMPEDE_COUNT];
    int speedArray[] = new int[STAMPEDE_COUNT];
    int stateArray[] = new int[STAMPEDE_COUNT];

    //    System.out.println("cows stampede!");
    stopSounds(); // clear all other sounds first

    // draw the current image in the mainview into the background
    // buffer, thereby freezing the action
    Graphics g = backGroundBuffer.getGraphics();
    g.drawImage(mainView.loadedImage,0,0,null);

    g = offscreenBuffer.getGraphics(); // now perform all drawing to offscreen
    // initialize the stampeding cows
    for (int i=0;i<STAMPEDE_COUNT;i++) {
      int yPt = (int)(Math.random() * STAMPEDE_Y_RANGE);
      yPt += STAMPEDE_START_Y;
      //      System.out.println("alloc ypt="+yPt);
      cowArray[i] = new Point(STAMPEDE_START_X,yPt);
      speedArray[i] = STAMPEDE_BASE_SPEED + 
	((int)(Math.random() * STAMPEDE_BASE_SPEED));
      stateArray[i] = (int) (Math.random() * (MAX_STAMPEDE_IMAGES-1));
    }

    // now draw the stampeding cows
    for (int i=0;i<STAMPEDE_TIME;i++) {
    // main drawing loop
      g.drawImage(backGroundBuffer,0,0,null); // refresh

      for (int j=0;j<STAMPEDE_COUNT;j++) {
	// loop through each cow

	// flip the state
	if (stateArray[j] == 0)
	  stateArray[j] = 1;
	else
	  stateArray[j] = 0;

	// increment each cow.  If it has moved past the left boundary,
	// then reset it
	cowArray[j].x -= speedArray[j];
	if (cowArray[j].x <= STAMPEDE_LEFT_BOUNDARY) {
	  // reset the cow
	  int yPt = (int)(Math.random() * STAMPEDE_Y_RANGE);
	  yPt += STAMPEDE_START_Y;
	  // use move, as setLocation kills N Communicator
	  //	  cowArray[j].setLocation(STAMPEDE_START_X,yPt);
	  cowArray[j].move(STAMPEDE_START_X,yPt);
	}
	// now draw the cow
	g.drawImage(stampedeImages[stateArray[j]],
		    cowArray[j].x,cowArray[j].y,null);
      }

      // now play a random moo sound
      if (((int)(Math.random()*100)) < STAMPEDE_MOO_CHANCE) {
	switch ((int)(Math.random() * 2)) {
	case 0:
	  longMoo.play();
	  break;
	case 1:
	  quickMoo.play();
	  break;
	case 2:
	  fastMoo.play();
	  break;
	}
      }
      // now update the main screen
      mainView.setImage(offscreenBuffer);
      mainView.update(this.getGraphics());
      delay();
    } // end main drawing loop

    // reset the backGroundBuffer (so it's not messy)
    g = backGroundBuffer.getGraphics();
    g.drawImage(backGroundImage,0,0,null);
  }

  private final void displayHiScores(int page) {
    // display the high scorers.  Note: presumes hiscores are valid!
    // displays the high scores in the given page range (1-10, 11-20, etc...)
    // NOTE: presumes the page range is valid!
    // NOTE: page is 0 indexed, so page=0 corresponds to 1-10
    Graphics g = offscreenBuffer.getGraphics();
    g.drawImage(backGroundImage,0,0,null);

    // draw the border
    g.setColor(topScoreBorderColor);
    g.fillRect(TOP_SCORE_RECT_X-TOP_SCORE_BORDER_WIDTH,
	       TOP_SCORE_RECT_Y-TOP_SCORE_BORDER_WIDTH,
	       TOP_SCORE_RECT_WIDTH + 2*TOP_SCORE_BORDER_WIDTH,
	       TOP_SCORE_RECT_HEIGHT + 2*TOP_SCORE_BORDER_WIDTH);

    // draw background rectangle
    g.setColor(topScoreRectColor);
    g.fillRect(TOP_SCORE_RECT_X,TOP_SCORE_RECT_Y,TOP_SCORE_RECT_WIDTH,
	       TOP_SCORE_RECT_HEIGHT);

    g.drawImage(topTenImage,TOP_10_X,TOP_10_Y,null);

    // modification 11/10/99
    // give props where due
    g.setColor(topScoreRectColor);
    g.fillRect(0,0,450,30);

    g.setFont(propsFont);
    g.setColor(topScoresFontColor);

    g.drawString("Art by John Joh    - - -    Program and Concept by Eric Yeh",85,10);
    g.drawString("Copyright Rotten Tomatoes 1998,1999,2000 - - - http://www.rottentomatoes.com",40,20);


    int currY = TOP_SCORES_START_Y;

    g.setFont(topScoresFont);
    g.setColor(topScoresFontColor);
   
 
    for (int i=0;i<TOP_SCORES_COUNT;i++) {
      // index value (based on page range)
      int index = i + (TOP_SCORE_PAGE_RANGE * page);

      // constrain name width (so it don't look funky)
      String nameString = hiScoreList.nameList[index];

      // perform check for null entries.  If so, plop in the appropriate
      // placer
      if (nameString == null)
	nameString = "no entry";
      if (hiScoreList.scoreList[index] == null)
	hiScoreList.scoreList[index] = "";
      int nameLength = nameString.length();
      if (nameLength > HI_SCORE_NAME_FIELD_CHAR_WIDTH) 
	nameString = nameString.substring(0,HI_SCORE_NAME_FIELD_CHAR_WIDTH);

      if ((index+1) >= SINGLE_DIGIT_LIMIT) {
	// draw the offset-ed version
	g.drawString((index+1)+") "+nameString,TOP_SCORE_NAME_X - 
		     TENTH_VALUE_OFFSET,currY);
	g.drawString(""+hiScoreList.scoreList[index],TOP_SCORE_SCORE_X,currY);
      } else {
	// draw normal version
	g.drawString((index+1)+") "+nameString,TOP_SCORE_NAME_X,currY);
	g.drawString(""+hiScoreList.scoreList[index],TOP_SCORE_SCORE_X,currY);
      }
      currY+=TOP_SCORES_Y_INCREMENT;
    }

    g.drawImage(startButtonImage,START_BUTTON_X,START_BUTTON_Y,null);
    g.drawImage(hiScoreButtonImage,HI_SCORE_BUTTON_X,HI_SCORE_BUTTON_Y,null);

    mainView.setImage(offscreenBuffer);
    mainView.update(this.getGraphics());
  }


  // --------------------------------------------------
  // displays the game over screen
  // --------------------------------------------------
  private final void displayGameOver() {
    Graphics g = offscreenBuffer.getGraphics();
    g.drawImage(backGroundImage,0,0,null);
    g.drawImage(gameOverImage,GAME_OVER_X,GAME_OVER_Y,null);

    //    drawScore(g); // draw in the score
    
    // draw message
    //    g.setColor(Color.black);
    g.setColor(textColor);
    g.setFont(scoreFont);
    g.drawString("final score $"+Farm.score,FINAL_SCORE_X+60,FINAL_SCORE_Y);
    //    g.drawString("hit 's' to play",TITLE_INFO_X,FINAL_SCORE_Y+20);
    g.drawImage(startButtonImage,START_BUTTON_X,START_BUTTON_Y,null);

    if (hiScoreList.available) 
      g.drawImage(hiScoreButtonImage,HI_SCORE_BUTTON_X,HI_SCORE_BUTTON_Y,null);

    //
    // 2/22/01 EY: clear out blurb for now and display the advertisement for the sweepstakes
    //

    // now choose a blurb and write it
    //    String blurb = blurbList[( (int) (Math.random() * BLURB_COUNT))];
    //    g.setFont(creditFont);
    //    g.drawString(blurb,BLURB_X,BLURB_Y);

    //
    // add in blurb about the RT Giveaway
    //
    g.setFont(creditFont);
    g.drawString("Psst... wanna win a free Chicken Run DVD, and more?",BLURB_X,BLURB_Y);
    g.drawImage(winButtonImage,AD_BUTTON_X, AD_BUTTON_Y, null);

    mainView.setImage(offscreenBuffer);
    mainView.update(this.getGraphics());
  }


  // --------------------------------------------------
  // handles game over logistics
  // --------------------------------------------------
  private final void handleGameOver() {
    // handles game over mechanics, once the game ends.  Checks for high
    // scores and calls display function

    //    System.out.println("game over!");
    gameState = GAME_OVER;

    // now set the game over timer so that it'll flip screens, and 
    // show game over screen first.
    gameOverTimer = INITIAL_DISPLAY_TIME;
    gameOverState = DISPLAY_GAME_OVER;

    // test for hiscores
    //
    // 1/23/01 - hiscores disabled for now
    // processHighScores();
    displayGameOver(); // display the standard image

    //    System.out.println("gamestate = "+gameState);
    // if no new high score, then grab the highscore list (as the submit
    // action will cause the highscores to be grabbed, but only if a new one
    // is entered).

    // disable hiscores for now
    /*    if (gameState != ENTER_HIGH_SCORE) {
          getHiScore();
	  System.out.println("highscores avail="+hiScoreList.available);
	  } */
    
    // draw
    mainView.setImage(offscreenBuffer);
    mainView.update(this.getGraphics());
  }


  // --------------------------------------------------
  //  displays the intermission screen
  // --------------------------------------------------
  private final void displayIntermission() {
    // in between rounds
    Graphics g = offscreenBuffer.getGraphics();
    g.drawImage(backGroundImage,0,0,null);
    //    g.setColor(Color.black);
    g.setColor(textColor);
    g.setFont(scoreFont);
    g.drawString("Lives left: "+Farm.cowsLeft,
		 INT_COWS_LEFT_X,INT_COWS_LEFT_Y);
    mainView.setImage(offscreenBuffer);
    mainView.update(this.getGraphics());
    longMoo.play();
    for (int i=0;i<50;i++)
      delay();

    if (Farm.cowsLeft > 0) {
      // cows left, so continue
      int bonus = Farm.cowCount * 100;
      for (int i=0;i<=bonus;i+=100) {
	// this needs to be re-drawn too
	g.drawImage(backGroundImage,0,0,null);
	g.drawString("Lives left: "+Farm.cowsLeft,
		     INT_COWS_LEFT_X,INT_COWS_LEFT_Y);
	g.drawString("Cows surviving = $100 x"+Farm.cowCount+" = "+i,
		     INT_BONUS_X,INT_BONUS_Y);
	mainView.setImage(offscreenBuffer);
	mainView.update(this.getGraphics());
	if (bonus > 0)
	  milkSound.play();
	for (int j=0;j<5;j++)
	  delay();
      }
      for (int i=0;i<50;i++)
	delay();
      
      
      Farm.score += bonus;
      g.drawString("Total score = $"+Farm.score,INT_SCORE_X,INT_SCORE_Y);
      mainView.setImage(offscreenBuffer);
      mainView.update(this.getGraphics());
      fastMoo.play();
      for (int i=0;i<60;i++)
	delay();

      // now display any bonus cows
      if (Farm.score >= Farm.nextCowBonusScore) {
	// new cow!
	g.drawString("Bonus cow!",INT_BONUS_COW_X,INT_BONUS_COW_Y);
	g.drawImage(cowImages[0],INT_BONUS_COW_IMAGE_X,
		    INT_BONUS_COW_IMAGE_Y,null);
	mainView.setImage(offscreenBuffer);
	mainView.update(this.getGraphics());
	burstMoo.play();
	for (int i=0;i<70;i++)
	  delay();

	Farm.incCowsLeft(); // increment lives
	// set next goal value
	Farm.incBonusIncrement();
	Farm.nextCowBonusScore += Farm.bonusIncrement;

	System.out.println("next cow at "+Farm.nextCowBonusScore);
      }

      // the next day...
      g.drawImage(backGroundImage,0,0,null);
      //      g.setColor(Color.black);
      g.setColor(textColor);
      g.setFont(scoreFont);
      g.drawString("The next day...",INT_SCORE_X,INT_SCORE_Y);
      mainView.setImage(offscreenBuffer);
      mainView.update(this.getGraphics());
      roosterSound.play();
      for (int i=0;i<50;i++)
	delay();
    } else {
      // they're all dead!
      g.drawString("The cows tire of dying...",INT_BONUS_X,
		   INT_BONUS_Y);
      g.drawString("They revolt!",INT_SCORE_X,
		   INT_SCORE_Y);
      mainView.setImage(offscreenBuffer);
      mainView.update(this.getGraphics());
      for (int i=0;i<50;i++)
	delay();

    }


  }


  // --------------------------------------------------
  // delays the thread for the constant targetTime
  // --------------------------------------------------
  private void delay() {
    // causes thread to sleep for targettime ms
    try {
      Thread.sleep(targetTime);
    } 
    catch (InterruptedException e) {
      ;
    }
  }
  

  // --------------------------------------------------
  // Thread control: start routine.  If not started
  // then spawns a new thread of itself and starts it.
  // This spawns the animation thread.
  // --------------------------------------------------
   public void start() {
     if (animThread == null) {
       animThread = new Thread(this);
       System.out.println("anim thread started\n");
    }
     animThread.start();  
  } 
  
  // --------------------------------------------------
  // Thread control: stop routine.  This stops and kills
  // the animation thread.  In addition, it stops all 
  // sounds.
  // --------------------------------------------------
  public void stop() {
    // stop the animation thread
    animThread.stop();
    animThread = null; 
    stopSounds();
  }

  // --------------------------------------------------
  // this terminates all possible sounds
  // --------------------------------------------------
  public void stopSounds() {
    // terminate all sounds
    longMoo.stop();
    fastMoo.stop();
    quickMoo.stop();
    annoyedMoo.stop();
    burstMoo.stop();
    milkSound.stop();
    shotGunSound.stop();
    noonieSound.stop();
    tipsydaisySound.stop();
    chachingSound.stop();
    popSound.stop();
    roosterSound.stop();
    cricketSound.stop();
  } 
  

  // --------------------------------------------------
  // main point of execution for the thread
  // --------------------------------------------------
  public void run() {
    mediaLoaded = false;

    // set high score ui invisible
    nameField.setVisible(false);
    emailField.setVisible(false);
    submitButton.setVisible(false);  

    System.out.println("beginning run\n");
    //    System.out.println("soundsloaded = "+soundsLoaded);
    displayLoading(soundsLoaded,0);
    update(this.getGraphics());

    preloadSounds();

    try {
      //      tracker.waitForAll(); 
      for (int curr = 0; curr < imageCount; curr++) {
	tracker.waitForID(curr);
	displayLoading(soundsLoaded,curr);
      }

    } catch (InterruptedException e){
      System.err.println("image loading error");
    }

    mediaLoaded = true;
    displayTitle(); 
    //    getHiScore(); // get scores

    // while the current thread is the animating thread (i.e. this shoudl only execute
    // in the game animation thread), execute the game content.
    while (Thread.currentThread() == animThread) {
      try {
	/*if (!tracker.checkAll()) {
	  //	  displayLoading(soundsLoaded,0);
	  update(this.getGraphics());
	}
	else {   */
	  //      if ((gameState == PLAYING) && (tracker.checkAll())) {
	  switch (gameState) {

	    // game is running.  Handle the main game loop
	  case PLAYING: {
	    sun.handle();
	    handleFarmer();
	    badguy.handle();
	    fairy.handle();
	    handleCows();
	    drawMainField();

	    // check cowsLeft.  If none, and timer hasn't been set yet,
	    // then stampede!
	    if ((Farm.cowsLeft <= 0) && (stampedeCountDown == 0)) {
	      stampedeCountDown = STAMPEDE_COUNT_DOWN;
	      sun.timeLeft += STAMPEDE_COUNT_DOWN; // set it so that the 
	      // round won't end before the start of the stampede
	    }

	    if (stampedeCountDown > 0) {
	      stampedeCountDown--;
	      if (stampedeCountDown <= 0) {
		gameState = STAMPEDE;
		displayStampede();
		sun.timeLeft = 0; // clear time to trigger intermission
	      }
	    }

	    // end of round round handling code
	    if (sun.timeLeft <= 0) {
	      gameState = INTERMISSION;
	      displayIntermission();
	      if (Farm.cowsLeft <= 0) {
		// game over! no more cows in reserve..
		handleGameOver();
	      }
	      else {
		// onto the next round!
		incrementLevel();
		setupRound(); // next round
		gameState = PLAYING;
	      }
	    }
	    update(this.getGraphics());
	    break;
	  } // end case for PLAYING

	  // enter high score
	  case ENTER_HIGH_SCORE: {
	    // process
	    //	    update(this.getGraphics());
	    break;
	  }

	  // game over
	  case GAME_OVER: {
	    // decrement the timer.  If timer off, then switch screens to
	    // the next in line
	    gameOverTimer--;
	    if (gameOverTimer <= 0) {
	      // switch background image
	      gameOverState++;
	      if (gameOverState > MAX_GAME_OVER_SCREENS)
		gameOverState = 0; // reset cycle
	      switch (gameOverState) {
		// set the new delays based on state
	      case DISPLAY_HI_SCORES_1:
	      case DISPLAY_HI_SCORES_2:
	      case DISPLAY_HI_SCORES_3:
	      case DISPLAY_HI_SCORES_4:
	      case DISPLAY_HI_SCORES_5:
		gameOverTimer = HI_SCORE_DISPLAY_TIME;
		break;
	      default:
		gameOverTimer = GAME_OVER_DELAY_TIME;
		break;
	      }
	      
	      // now display the appropriate screen
	      //	      System.out.println("gameOverState = "+gameOverState);
	      switch (gameOverState) {
	      case DISPLAY_GAME_OVER:
		displayGameOver();
		break;
	      case DISPLAY_HI_SCORES_1:
	      case DISPLAY_HI_SCORES_2:
	      case DISPLAY_HI_SCORES_3:
	      case DISPLAY_HI_SCORES_4:
	      case DISPLAY_HI_SCORES_5:
		if (hiScoreList.available == true)
		  displayHiScores(gameOverState - DISPLAY_HI_SCORES_1);
		else
		  displayGameOver();
		break;
	      case DISPLAY_TITLE:
		displayTitle();
		break;
	      }
	      
	    } // end resetting the gameover timer
	    break;
	  }

	  // default (just updates mainview)
	  default: {
	    // default (display the screen)
	    if (hiScoreList.available) 
	      displayHiScores(gameState - DISPLAY_HI_SCORES_1);
	    update(this.getGraphics());
	    break;
	  }
	  } // end switch
      }
      //    }
       catch (NullPointerException e) {
	;
      } 
      // Sleep until beginning of next cycle
      delay();
    }
    
    
  }
  
  
  // ----------------------------------------------------------------------
  //
  //  Game graphics and animation routines
  //
  // ----------------------------------------------------------------------

  public void paint(Graphics g) {
    update(g);
  }

  public void update(Graphics g) {
    if (!laidOut) {
      System.out.println("layout ui\n");
      layOutUI();
    }

    // based on the situation, draw the appropriate background and items
    switch (gameState) {
    case ENTER_HIGH_SCORE:
      displayEnterHiScore(g);
      break;
    default:
      mainView.update(g);
      break;
    } // end switch
  }
  
  private final void drawMainField() {
    Graphics g = offscreenBuffer.getGraphics();
    drawBackGround(g);
    drawWorld(g);
    mainView.setImage(offscreenBuffer);
    //    mainView.update(this.getGraphics());
    update(this.getGraphics());
  }

  private final void drawBackGround(Graphics g) {
    try {
      /*      g.setColor(skyColor);
	      g.fillRect(0,0,SKY_WIDTH, SKY_HEIGHT);  */
      g.drawImage(backGroundBuffer,0,0,this);
    } catch (NullPointerException e) {
    }
  }

  private final void drawCowsLeft(Graphics g) {
    //    g.setColor(Color.black);
    g.setColor(textColor);
    g.setFont(scoreFont);
    g.drawImage(iconImage,ICON_X,ICON_Y,null);
    // add one to day since levels are 0 indexed, and day0 doesn't make much sense
    g.drawString(" Lives: "+Farm.cowsLeft,COWS_LEFT_X,COWS_LEFT_Y);
    g.drawString(" Day: "+(Farm.level+1),DAY_X,DAY_Y);
  }

  private final void drawStatusBar(Graphics g) {
    // given milk amount and max milk, draw out the
    // amount
    /*    g.setColor(textColor);
    g.setFont(creditFont);
    g.drawString("Milk",MILK_TEXT_X,MILK_TEXT_Y); */
    g.setColor(Color.gray);
    g.fillRect(MILK_BAR_X,MILK_BAR_Y,MILK_BAR_WIDTH,MILK_BAR_HEIGHT);
    int length = (hero.milkCollected * MILK_BAR_WIDTH);
    //    System.out.println("length="+length);
    length /= hero.MAX_MILK;
    //    System.out.println("lengthf="+length);
    g.setColor(Color.white);
    g.fillRect(MILK_BAR_X,MILK_BAR_Y,(int)length,MILK_BAR_HEIGHT);

    if (hero.milkCollected >= hero.MAX_MILK) {
      // draw emergency dump message
      g.setColor(Color.red);
      g.setFont(dumpMilkFont);
      
      // 
      dumpMilkTimer--;
      if (dumpMilkTimer > DUMP_DISPLAY_THRESHOLD) {
	g.drawString("Empty Bucket!!!",
		     DUMP_MILK_MSG_X,DUMP_MILK_MSG_Y); 
      } else {
	if (dumpMilkTimer <= 0)
	  dumpMilkTimer = DUMP_COUNT_DOWN;
      }
    }
  }
  
  private final void drawScore(Graphics g) {
    //    g.setColor(Color.black);
    g.setColor(textColor);
    g.setFont(scoreFont);
    g.drawString("Funds: $"+Farm.score,SCORE_X,SCORE_Y);
  }


  private final void drawWorld(Graphics g) {
    // draw all game elements
    badguy.draw(g); 
    for (int i=0; i<Farm.maxCowCount;i++) {
      cowList[i].draw(g);
    }
    sun.draw(g);
    drawBucket(g);
    drawStump(g);
    hero.draw(g);
    drawCowsLeft(g);
    drawStatusBar(g);
    drawScore(g);
  }

  private final void drawBucket(Graphics g) {
    Point loc = Farm.getLocale(Farm.BUCKET_SLOT);
    try {
      g.drawImage(bucketImage,loc.x + BUCKET_X_OFFSET,loc.y + BUCKET_Y_OFFSET,null); 
}
    catch (NullPointerException e) {
    }
  }

  private final void drawStump(Graphics g) {
    Point loc = Farm.getLocale(Farm.STUMP_SLOT);
    try {
      g.drawImage(stumpImage,loc.x + STUMP_X_OFFSET,loc.y + STUMP_Y_OFFSET,null); 
}
    catch (NullPointerException e) {
    }
  }

  // --------------------------------------------------
  //   game object management
  // --------------------------------------------------
  private final void handleCows() {
    for (int i=0; i<Farm.maxCowCount;i++) {
      cowList[i].handle();
    }
  }

  private final void performAction() {
    // perform action in given area and handle results.
  }

  private final void handleFarmer() {
    hero.handle();  
  }

  private final void farmerAction() {
    int pos = hero.locale;
    
    // farmer can perform action only if not delayed
    if (hero.delayTime <= 0) {
      if (pos == MILK_DUMP_POS) {
	// if at milk dump, then inc score & dump milk.
	Farm.score += hero.milkCollected;
	// reward full bucket
	if (hero.milkCollected == hero.MAX_MILK)
	  Farm.score += hero.FULL_BUCKET_BONUS;
	hero.dumpMilk();
	dumpMilkSound.play();
      }
      
      if (pos == Farm.GUN_SLOT) {
	shotGunSound.play();
	hero.fireGun();
	badguy.shootAt(); // fire at the tipper (if any)
	hero.delayTime = hero.GUN_DELAY; // farmer delays
      }
      
      if ((pos >= COW_OFFSET) && (pos < (COW_OFFSET + Farm.maxCowCount)) 
	  && (cowList[pos-COW_OFFSET].state < Cow.COW_DEATH)) {
	hero.milk();  // start milking!
	int amount = hero.milkCollectRate;
	int milk = cowList[pos - COW_OFFSET].milk - amount;
	if (milk < 0) {
	  
	  cowList[pos-COW_OFFSET].stress();
	  amount = amount + milk;
	  milk = 0;
	}
	int runoff = hero.addMilk(amount);
	if (runoff > 0) {
	  // uh oh, bucket is full, and cow isn't milked
	  amount -= runoff;
	  hero.state = Farmer.STAND;
	} else
	  milkSound.play();
	//      System.out.println("amnt = "+amount+" runoff="+runoff);
	cowList[hero.locale - COW_OFFSET].milk -= amount;  // reduce cow milk level
      }
    }
  }

  //----------------------------------------------------------------------
  //
  //  Game management routines
  //
  // ----------------------------------------------------------------------

  private final void resetGame() {
    // draw the background image into the backGroundBuffer
    Graphics g = backGroundBuffer.getGraphics();
    g.drawImage(backGroundImage,0,0,null);

    // initialize state variables
    stampedeCountDown = 0; // no stampeding!
    Farm.reset();
    setupRound();
  }
    
  
  // begins game
  private final void playGame() {
    resetGame();
    gameState = PLAYING;
    drawMainField();
  }


  private final void gameOver() {
  }

  // ----------------------------------------------------------------------
  //
  //  Event handling
  //
  // ----------------------------------------------------------------------
  


  public boolean action(Event e, Object arg) {
    // check to see which item received the notification
    Object myTarget = e.target;

    if ((myTarget == submitButton) && (gameState == ENTER_HIGH_SCORE)) {
      // enter the score, email, and name of the high scorer, and exit
      // the highscore loop
      System.out.println("entering high score submission");
      submitHiScore();// enter the score
      exitEnterHiScore();

      // now regrab the highscores
      // grab the highscores before starting the game
      //
      // temporarily disable hiscores for now
      //      getHiScore(); 
      //      System.out.println("highscores avail="+hiScoreList.available);
      
    }
    return true;
  }
  
 public boolean handleEvent(Event evt) {
   if ((evt.id == Event.KEY_ACTION) && (gameState == PLAYING)) {
     if (evt.key == LEFT_ARROW_KEY)
       hero.moveLeft();
     if (evt.key == RIGHT_ARROW_KEY)
       hero.moveRight();
     if (evt.key == UP_ARROW_KEY)
       hero.moveUp();
     if (evt.key == DOWN_ARROW_KEY)
       hero.moveDown();
   }


   if ((evt.id == Event.KEY_PRESS) && (gameState == PLAYING)) {
     if (((char) evt.key == 'j') || ((char) evt.key == 'J'))
       hero.moveLeft();
     if (((char) evt.key == 'l') || ((char) evt.key == 'L'))
       hero.moveRight();
     if (((char) evt.key == 'i') || ((char) evt.key == 'I'))
       hero.moveUp();
     if (((char) evt.key == 'k') || ((char) evt.key == 'K'))
       hero.moveDown();
     if ((char) evt.key == ' ') 
       farmerAction();
     if (((char) evt.key == 'q') || ((char) evt.key == 'Q'))
       ; // quitgame
     return true;
   }
   
   if ((evt.id == Event.KEY_PRESS)&& (gameState == GAME_OVER) &&
       (mediaLoaded == true)) {
     if (((char) evt.key == 's') || ((char) evt.key == 'S'))
       playGame();
     // display hi scores using h
     if (((char) evt.key == 'h') || ((char) evt.key == 'H')) {
       if ((gameOverState >= DISPLAY_HI_SCORES_1) && 
	   (gameOverState < DISPLAY_HI_SCORES_5))
	 gameOverState++; // go to next page of hi scores
       else
	 gameOverState = DISPLAY_HI_SCORES_1 - 1; // dec by one since timer countdown will inc it, so that the 1st hiscore page will be shown
       gameOverTimer = 0 ; // cause immediate switch to the high score screen
     }
   }

   if ((evt.id == Event.MOUSE_DOWN)&& (gameState == GAME_OVER) &&
       (mediaLoaded == true)) {
     // OK, this is a major hack, but it'l have to do until the updated AWT is acoomdated
     // for in the public and the buttons are objects themselves.
     //     System.out.println("x="+evt.x+" y="+evt.y);
     // check for collision with start button
     if ((evt.x >= START_BUTTON_X) && (evt.x <= (START_BUTTON_X + START_BUTTON_WIDTH)) &&
	 (evt.y >= START_BUTTON_Y) && (evt.y <= (START_BUTTON_Y + START_BUTTON_HEIGHT)))
       // start a new game
       playGame();
     else
     if (((evt.x >= HI_SCORE_BUTTON_X) && 
	 (evt.x <= (HI_SCORE_BUTTON_X + HI_SCORE_BUTTON_WIDTH)) &&
	 (evt.y >= HI_SCORE_BUTTON_Y) && 
	 (evt.y <= (HI_SCORE_BUTTON_Y + HI_SCORE_BUTTON_HEIGHT))) && (hiScoreList.available))  {
       // display the high scores
       if ((gameOverState >= DISPLAY_HI_SCORES_1) && 
	   (gameOverState < DISPLAY_HI_SCORES_5))
	 ; // automatically let it go on to next page listing
       else
	 gameOverState = DISPLAY_HI_SCORES_1 - 1; // dec by one since timer countdown will inc it, so that the 1st hiscore page will be shown
       gameOverTimer = 0 ; // cause immediate switch to the high score screen
     } else 
     if ((evt.x >= AD_BUTTON_X) && (evt.x <= (AD_BUTTON_X + AD_BUTTON_WIDTH)) &&
	 (evt.y >= AD_BUTTON_Y) && (evt.y <= (AD_BUTTON_Y + AD_BUTTON_HEIGHT))) {
       //
       // clicked on the fancy dandy RT sweepstakes advertisement.  Bring up the 
       // sweepstakes page in a seperate window
       //
       //       showDocument(new URL("http://www.rottentomatoes.com/features/sweepstakes/index.php?ListID=1"),
       //		    "sweepstakes");

       try {
	 getAppletContext().showDocument(new URL("http://www.rottentomatoes.com/features/sweepstakes/index.php?ListID=1"),
					 "RT Sweepstakes");
       } catch (MalformedURLException mfe) {
	 System.out.println("malformed sweepstakes URL!");
       } // end try catch
       
     } // end hideous loop through all hacked up graphic buttons
  }

     /*  if ((evt.id == Event.MOUSE_DOWN)&& (gameState == ENTER_HIGH_SCORE) &&
      ((char) evt.key == '+')) {
    ; // kill all '+' symbols
  } */

   return super.handleEvent(evt);
 }

  
}





