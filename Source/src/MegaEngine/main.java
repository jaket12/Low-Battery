package MegaEngine;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The starting and containing state of the game.
 * Everything begins with this class. It holds the low level values like if
 * it's a windowed or full screen game, and it holds the Game Container, which
 * is where the actual game logic resides.
 * There are some public variables that can be set here through an options
 * menu or saved settings file.
 * Mostly, you don't want to mess with this. Even slight changes make huge problems.
 * @author McRib
 */
public class main extends StateBasedGame 
{
    public static AppGameContainer Container;//Where all the action lies
    public static int WindowWidth = 640;//Pixel resolution of the actual game
    public static int WindowHeight = 480;
    public static boolean FullScreen = false;
    public static boolean ShowFPS = false;
    public static boolean VSync = false;
    private static boolean TargetFrameRate = true;
    public static String WindowTitle = "Low Battery";
    private static int FPSLimit = 300;//Too high, and the delta difference is so small it breaks the game
    private static int MinFPS = 1;//Every X Milliseconds, update the game
    private static int MaxFPS = 1;//If the game updates over X Milliseconds, do nothing
    public static boolean SetAlwaysRender = true;
    private static boolean SmoothDeltas = true;
    public static boolean ClearFrame = true;

    public static Global Global;
    
    public main(String title) {
      super(title);
   }
    
    /**
     * Create the screens/menus/states that are going to be used for the game.
     * Whichever state is added first will be the first one to automatically load
     * when the game starts.
     * States are unique by the int ID within them.
     * I don't think you can really add another state while the game is running.
     * @param container 
     */
    @Override
    public void initStatesList(GameContainer container) {
      
      addState(new LoadState());//0
      addState(new MovieState()); //1
      addState(new MenuStateMain()); //2
      addState(new MenuStateOptions());//3
      addState(new LevelState()); //4
      addState(new LevelStateRestart());//5
      addState(new MenuStateStageSelect());//6
      addState(new MenuStateGameOverDeath());//7
      addState(new MenuStateGameOverBattery());//8
      addState(new MenuStateVictory());//9
   }
    
    /**
     * The first function that is run; used to create and start the game!
     * You can pass arguments through the command line when you start the game
     * and it will be able to read them if necessary.
     * Default values are set in here, but may be overridden inside of the game
     * by an options menu, or by save state during the LoadState initialization.
     * It's a really, really good idea to NOT mess with this!
     * @param argv
     * @throws SlickException 
     */
    public static void main(String[] argv)  throws SlickException 
    {
        //Initialize the shared variables and resources for the entire game.
        Global = new Global();
        
        //Create the actual game
        Container = new AppGameContainer(new main(WindowTitle));
        //Set the resolution
        Container.setDisplayMode(WindowWidth, WindowHeight, FullScreen);
        //SmoothDeltas gives a normalized time period: every frame is the same number
        Container.setSmoothDeltas(SmoothDeltas);
        //Set the forced update rate of the game.
        Container.setMinimumLogicUpdateInterval(MinFPS); //Handles frames that are too high
        Container.setMaximumLogicUpdateInterval(MaxFPS); //handles frames too low
        //Force the screen to update this many times per second
        Container.setVSync(VSync);
        //Set a variable frame rate similar to Vsync
        if (TargetFrameRate)
        {
            Container.setTargetFrameRate(FPSLimit);
        }
        //Visibly show the current FPS within the game window
        Container.setShowFPS(ShowFPS);
        
        Container.setAlwaysRender(SetAlwaysRender);
        //If true, the screen will turn black after each frame, to remove all scraps of image. If false, image traces remain but FPS is boosted
        Container.setClearEachFrame(ClearFrame);
        //Start up the game!
        Container.start();
   }
    
    /*
     * Other classes can call these functions to change how the game is running.
     * Options screen is what will use these functions
     */
    public static void SetFPSlimit(boolean value)
    {
        TargetFrameRate = value;
        if (TargetFrameRate && FPSLimit > 0)
            Container.setTargetFrameRate(FPSLimit);
                
    }
    
    public static void SetFullScreen(boolean value)
    {
        try {
            FullScreen = value;
            Container.setDisplayMode(WindowWidth, WindowHeight, FullScreen);             
        } catch (SlickException ex) {
            System.out.println("Fullscreen broke: " + ex.getMessage());
        }
    }
    
    public static void SetVSync(boolean value)
    {
        VSync = value;
        Container.setVSync(VSync);
    }
    
    public static boolean getFullScreen()
    {
        return FullScreen;
    }
    
    public static boolean getVsync()
    {
        return VSync;
    }
    
    public static int getWindowWidth()
    {
        return WindowWidth;
    }
    
    public static int getWindowHeight()
    {
        return WindowHeight;
    }
    
    public static void ClearFrames(boolean value)
    {
        ClearFrame = value;
        Container.setClearEachFrame(ClearFrame);
    }

}
