package MegaEngine;

import MegaEngine.Global.LevelSetupState;
import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.newdawn.slick.state.transition.*;
import java.io.*;
import java.util.*;

/**
 * Runs scripts provided from disk to display a sequence of images, text, and sound
 * to show a 'movie' on the screen. There is very little logic here: using the 
 * lines in the script, show things on screen and have them move around.
 * @author McRib
 */
public class MovieState extends BasicGameState {

    public static final int ID = 1;
    /** The ID given to this state */
    private StateBasedGame game;
    /** The game holding this state */
    
    ArrayList Script = new ArrayList(); //Each command line in a script file is placed sequentially in order here.
    int ReadLine = 0; //Current command line in the script to be read.
    String ScriptCurrentLine = ""; //A copy of the command line.
    
    MovieImageManager MovieImages;//All images which are currently in used are located here
    MovieTextManager Text;//All text is here
    
    int WaitTimer = 1000;
    //The time in milliseconds for a movie to wait until it reads
    //the next line in a script. A script can also modify this value
    
    String splits[]; //Dedicated string splitter for efficency
    //Holds All data in the script line, separated by character tilde ~

    private boolean PreRenderLoad = false;//If true, this is the first frame to be seen for the state. Goes true on Leave()
    
    //Input is only used to exit the movie and skip to the next screen.
    private KeyBoard Keyboard;
    private Input Input;
    private boolean ControllerDownHeld = false;
    private boolean ControllerLeftHeld = false;
    private boolean ControllerRightHeld = false;
    private boolean ControllerUpHeld = false;
    private boolean ControllerDownPressed = false;
    private boolean ControllerLeftPressed = false;
    private boolean ControllerRightPressed = false;
    private boolean ControllerUpPressed = false;
    
    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
        MovieImages = new MovieImageManager();
        Text = new MovieTextManager();
        Keyboard = main.Global.getKeyboard();
        Input = main.Container.getInput();//Transfer the input of the whole game into this state
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        main.ClearFrames(true);//Movies can have undrawn areas, so clear the screen before each frame
        OpenScriptFile(main.Global.GetMovieScriptLocation()); //Get images, text, sound, and order from file
        //When finished, render() and update() automatically start playing the movie
        Keyboard = main.Global.getKeyboard();

    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {

        //Shut down the state and make it ready for the next movie to play
        //This function is always called before switching a state

        MovieImages.AllVanish();//Hide every image, so when the state is entered again, there isn't a flicker of the old stuff
        Text.AllVanish();
        ResetState();
    }

    private void ResetState() { //Set all values to original state
        Script = new ArrayList();
        ReadLine = 0;
        ScriptCurrentLine = "";

        MovieImages = new MovieImageManager();
        Text = new MovieTextManager();
        WaitTimer = 0;

    }

    /*
     * Open the file which contains the script for the movie.
     */
    private void OpenScriptFile(String FileLocation) 
    {
        //Access the Script File to be loaded
        //Parse each line for the data it holds
        //Place and load the data in the correct location
        try 
        {
            //System.out.println("Attempting to open move file: " + FileLocation);
            InputStream fstream = getClass().getResourceAsStream(FileLocation);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            
            while ((strLine = br.readLine()) != null) 
            {
                splits = strLine.split("~");

                if (splits[0].charAt(0) == '/')
                {//Comment in the script, ignore and move on.
                    
                }
                else if (splits[0].equalsIgnoreCase("Load"))
                {//A new item is to be added into image/text/sound...
                    if (splits[1].equalsIgnoreCase("Image"))
                    {
                            //Params:
                            //Filepath, X, Y, Visible, Keyword
                             MovieImages.AddStillImage(splits[2], Float.valueOf(splits[3]), Float.valueOf(splits[4]), Boolean.valueOf(splits[5]), splits[6]);
                    }
                    else if (splits[1].equalsIgnoreCase("Animation"))
                    {
                            //Params:
                            //SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating, Keyword)
                            MovieImages.AddAnimatedImage(splits[2], Integer.valueOf(splits[3]), Integer.valueOf(splits[4]), 
                                    Integer.valueOf(splits[5]), Integer.valueOf(splits[6]), Integer.valueOf(splits[7]), 
                                    Float.valueOf(splits[8]), Float.valueOf(splits[9]), Boolean.valueOf(splits[10]), Boolean.valueOf(splits[11]), splits[12]);
                    }
                    else if (splits[1].equalsIgnoreCase("Text"))
                    {
                        //Params:
                        //Message, X, Y, Red, Green, Blue, Trans, Keyint
                        Text.AddText(splits[2], Integer.valueOf(splits[3]), Integer.valueOf(splits[4]), Integer.valueOf(splits[5]), 
                                Integer.valueOf(splits[6]), Integer.valueOf(splits[7]), Integer.valueOf(splits[8]), Integer.valueOf(splits[9]));
                    }
                    
                }
                else
                {//Add a line to the script.  It will be parsed when it should run.
                    Script.add(strLine);
                }

            }
            
            in.close();
            
        } 
        catch (IOException e) 
        {
            System.err.println("MovieState Error OpenScriptFile: " + e.getMessage());
            EmergencyErrorMovie();
        } catch (NumberFormatException e) {
            System.err.println("MovieState Error OpenScriptFile: " + e.getMessage());
            EmergencyErrorMovie();
        }

    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) 
    {
        MovieImages.Draw(g);
        Text.Draw(g);

    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) 
    {
        main.Global.Update(delta);//This should be called in pretty much every state
        
        //This is where the script is used.
        //Update will go line by line, and wait when told (offset by delta).
        WaitTimer -= delta;

        MovieImages.Update(delta);
        Text.Update(delta);
        
        //If there is more lines on the script to read, run them once the timer delay is out
        if (WaitTimer <= 0 && ReadLine < Script.size()) 
        { //Read next line in the script and execute it
            RunScript();
        }
        
        //Pretty much, if the player hits a button, skip to the next of the movie and swap states
        if (Keyboard.Player1UsingController || Keyboard.Player2UsingController)
       {

           for (int i = 0; i < Input.getControllerCount(); i++) {

               if (Input.isControllerUp(i)) {//Action is bound to controller move up
                   if (!ControllerUpHeld) {
                       controllerButtonPressed(i, -1);
                   }
                   ControllerUpPressed = true;
               } else if (Input.isControllerDown(i)) {//Action is bound to controller move up
                   if (!ControllerDownHeld) {
                       controllerButtonPressed(i, -2);
                   }
                   ControllerDownPressed = true;
               }
               if (Input.isControllerLeft(i)) {//Action is bound to controller move up
                   if (!ControllerLeftHeld) {
                       controllerButtonPressed(i, -3);
                   }
                   ControllerLeftPressed = true;
               }
               if (Input.isControllerRight(i)) {//Action is bound to controller move up
                   if (!ControllerRightHeld) {
                       controllerButtonPressed(i, -4);
                   }
                   ControllerRightPressed = true;
               }
           }

           SaveControllerPress();
       }
    }

    /**
     * Grab the next line in the script and execute it.
     */
    private void RunScript()
    {
        ScriptCurrentLine = Script.get(ReadLine).toString();
        
        String splitsR[] = ScriptCurrentLine.split("~");
        
        if (splitsR[0].equalsIgnoreCase("Wait"))
        {//Set time to the WaitTimer variable.
                WaitTimer = Integer.parseInt(splitsR[1]);
        }
        else if (splitsR[0].equalsIgnoreCase("Text"))
        {//Do some thing with text (show, load, hide, move...)
            RunTextScript(splitsR);
        }
        else if (splitsR[0].equalsIgnoreCase("Image"))
        {//Show, hide, move an images
            RunImageScript(splitsR);
        }
        else if (splitsR[0].equalsIgnoreCase("Sound"))
        {//Start, stop a sound
            if (splitsR[1].equalsIgnoreCase("Play"))
            {
                main.Global.Sound.PlaySound(Integer.valueOf(splitsR[2]));
            }
            else if (splitsR[1].equalsIgnoreCase("Stop All"))
            {
                main.Global.Sound.StopAllSound();
            }
            else if (splitsR[1].equalsIgnoreCase("Stop"))
            {
                main.Global.Sound.StopSound(Integer.valueOf(splitsR[2]));
            }
            
        }
        else if (splitsR[0].equalsIgnoreCase("Music"))
        {//Start, stop a music
            if (splitsR[1].equalsIgnoreCase("Play"))
            {
                main.Global.Music.PlayMusic(Integer.valueOf(splitsR[2]));
            }
            else if (splitsR[1].equalsIgnoreCase("Stop"))
            {
                main.Global.Music.StopMusic();
            }
            else if (splitsR[1].equalsIgnoreCase("Loop"))
            {
                main.Global.Music.LoopMusic(Integer.valueOf(splitsR[2]));
            }
            else if (splitsR[1].equalsIgnoreCase("Play Random"))
            {
                int min = Integer.valueOf(splitsR[2]);
                int max = Integer.valueOf(splitsR[3]);
                
                main.Global.Music.PlayMusic(main.Global.Random.nextInt(max - min) + min);
            }
            else if (splitsR[1].equalsIgnoreCase("Loop Random"))
            {
                int min = Integer.valueOf(splitsR[2]);
                int max = Integer.valueOf(splitsR[3]);
                
                main.Global.Music.LoopMusic(main.Global.Random.nextInt(max - min) + min);
            }
        }
        else if (splitsR[0].equalsIgnoreCase("Set Current Level"))
        {//Set the current level number in Global
            main.Global.CurrentLevel = Integer.valueOf(splitsR[1]);
        }
        else if (splitsR[0].equalsIgnoreCase("Set Level Setup State"))
        {//Set the current level number in Global
            switch (Integer.valueOf(splitsR[1])) {
                case 0:
                    main.Global.SetupState = LevelSetupState.New;
                    break;
                case 1:
                    main.Global.SetupState = LevelSetupState.Restart;
                    break;
                case 2:
                    main.Global.SetupState = LevelSetupState.Running;
                    break;
            }
            
        }
        else if (splitsR[0].equalsIgnoreCase("Swap State"))
        {//Move to another state in the application (Menu, movie, level, etc)
            SwapState(Integer.valueOf(splitsR[1]));
        }
        else if (splitsR[0].equalsIgnoreCase("Full Game Reset"))
        {//Start the game variables over for a completely new game
            main.Global.FullReset();
        }
        else
        {//Unknown command
            System.out.println("MovieState Error: What is the meaning of: " + ScriptCurrentLine);
        }
        
        ReadLine++;

    }

    /**
     * Functionality allowed for text within the movie state.
     * Stuff that you can do with text: show, hide, change it, move it.
     * All text are targeted by their array index value. When text is created
     * you need to keep track of the index if you ever want to change it.
     * Text~5~Center
     * @param splits 
     */
    private void RunTextScript(String[] splits)
    {
        int Location = Integer.valueOf(splits[1]);
        if (splits[2].equalsIgnoreCase("Message"))
        {//Change the text to something else
            Text.setText(Location, splits[3]);
        }
        else if (splits[2].equalsIgnoreCase("Fade In"))
        {//Fade opacity to full
            if (splits.length == 3)
            {
                Text.FadeIn(Location);
            }
            else if (splits.length == 4)
            {
                Text.FadeIn(Location, Integer.valueOf(splits[3]));
            }
        }
        else if (splits[2].equalsIgnoreCase("Fade Out"))
        {
            if (splits.length == 3)
            {//Make text fade out at the default speed
                Text.FadeOut(Location);
            }
            else if (splits.length == 4)
            {//Make it fade out at a precise speed
                Text.FadeOut(Location, Integer.valueOf(splits[3]));
            }
        }
        else if (splits[2].equalsIgnoreCase("Vanish"))
        {//Text instantly goes invisible. It's still there, though
            Text.Vanish(Location);
        }
        else if (splits[2].equalsIgnoreCase("Appear"))
        {//Instantly appear at full opacity
            Text.Appear(Location);
        } else if (splits[2].equalsIgnoreCase("Center"))
        {//Calculate the width of text, and place it square in the middle of the game screen
            Text.CenterText(Integer.valueOf(splits[1]));
        }
    }

    /**
     * Various commands that can be done in relation to images.
     * Image~Top~Move To~0~128~0~140
     * @param splits 
     */
    private void RunImageScript(String[] splits)
    {
        String Keyword = splits[1];
        if (splits[2].equalsIgnoreCase("Fade In"))
        {//Image increases in opactity until it hits full. Can also provide a speed to do it
            if (splits.length == 4)
            {
                MovieImages.FadeIn(Keyword, Integer.valueOf(splits[3]));
            }
            else
            {
                MovieImages.FadeIn(Keyword);
            }

        }
        else if (splits[2].equalsIgnoreCase("Fade Out"))
        {//Image goes invisible by default time, or a provided speed in milliseconds
            if (splits.length == 4)
            {
                MovieImages.FadeOut(Keyword, Integer.valueOf(splits[3]));
            }
            else
            {
                MovieImages.FadeOut(Keyword);
            }

        }
        else if (splits[2].equalsIgnoreCase("Fade Out Black"))
        {//Image turns black (not transparent) over time
            if (splits.length == 4)
            {
                MovieImages.FadeOutBlack(Keyword, Integer.valueOf(splits[3]));
            }
            else
            {
                MovieImages.FadeOutBlack(Keyword);
            }

        }
        else if (splits[2].equalsIgnoreCase("Fade In Black"))
        {//Image turns from black and into the normal colors over time
            if (splits.length == 4)
            {
                MovieImages.FadeInBlack(Keyword, Integer.valueOf(splits[3]));
            }
            else
            {
                MovieImages.FadeInBlack(Keyword);
            }

        }
        else if (splits[2].equalsIgnoreCase("Vanish"))
        {//Instantly make an image invisible. It's still there though
            MovieImages.Vanish(Keyword);
        }
        else if (splits[2].equalsIgnoreCase("Appear"))
        {//Instantly make an image full opacity
            MovieImages.Appear(Keyword);
        }
        else if (splits[2].equalsIgnoreCase("Set Velocity X"))
        {//Set a constant horizontal speed
            MovieImages.SetVelocityX(Keyword, Integer.valueOf(splits[3]));
        }
        else if (splits[2].equalsIgnoreCase("Set Velocity Y"))
        {//Set a constant vertical speed
            MovieImages.SetVelocityY(Keyword, Integer.valueOf(splits[3]));
        }
        else if (splits[2].equalsIgnoreCase("Set Location X"))
        {//Instantly move the image to this X co-ordinate
            MovieImages.SetLocationX(Keyword, Integer.valueOf(splits[3]));
        }
        else if (splits[2].equalsIgnoreCase("Set Location Y"))
        {//Instantly move the image to this Y co-ordinate
            MovieImages.SetLocationY(Keyword, Integer.valueOf(splits[3]));
        }
        else if (splits[2].equalsIgnoreCase("Add Script"))
        {//Add a personal script line to this image, so it can run it's own logic
            //Requires an individual line to be provided
            String tScript = splits[3];
            int i;
            for (i = 4; i < splits.length; i++)
            {
                tScript = tScript.concat("~" + splits[i]);
            }
            MovieImages.AddScript(Keyword, tScript);
        }
        else if (splits[2].equalsIgnoreCase("Start Script"))
        {//Start or continue the script that the image personally has
            MovieImages.StartScript(Keyword, true);
        }
        else if (splits[2].equalsIgnoreCase("Stop Script"))
        {//Stop the personal script that the image has
            MovieImages.StartScript(Keyword, false);
        }
        else if (splits[2].equalsIgnoreCase("Move To"))
        {//Move the image to an exact location on the screen at a certain velocity
            //Params:
            //keyword, Destination X, Destination Y, Velocty X, Velocity Y
            //If a given destination or velocity is an "X" then that variable
            //does not matter on the destination.
            MovieImages.MoveToLocation(Keyword, splits[3], splits[4], splits[5], splits[6]);
        }
        else if (splits[2].equalsIgnoreCase("Flip Image Horizontally"))
        {
            MovieImages.FlipHorizontally(Keyword);
        }
        else if (splits[2].equalsIgnoreCase("Flip Image Vertically"))
        {
            MovieImages.FlipVertically(Keyword);
        }
    }
    
    /**
     * If no script was found during the loading of the movie state, run an error one.
     */
    private void EmergencyErrorMovie()
    {
        main.Global.SetMovieScriptLocation("/Resource/Movie/EmergencyError.scr");
        SwapState(MovieState.ID);
    }
    
    @Override
    public void keyReleased(int key, char c) {
        if (key == Keyboard.Player1Pause || key == Keyboard.Player1Menu || key == Input.KEY_SPACE || key == Keyboard.Player1Shoot) {
            //Player wants to skip movie.
            //Last item in the script is ALWAYS the default way to get out.
            //move to that state number
            String state = Script.get(Script.size() - 1).toString();
            state = state.substring(state.lastIndexOf("~") + 1);
            SwapState(Integer.valueOf(state));
        }
    }

    private void SwapState(int ID) 
    {
        main.Global.Sound.StopAllSound();
        game.enterState(ID, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
    }
    
    
    /*
     * Saves the controller press for the MENUs.
     * In game keys are saved by the character and are not recorded in the game state.
     */
    private void SaveControllerPress()
    {

        ControllerDownHeld = ControllerDownPressed;
        ControllerUpHeld = ControllerUpPressed;
        ControllerLeftHeld = ControllerLeftPressed;
        ControllerRightHeld = ControllerRightPressed;
        ControllerDownPressed = false;
        ControllerUpPressed = false;
        ControllerLeftPressed = false;
        ControllerRightPressed = false;
    }
    
    @Override
    public void controllerButtonPressed(int controller, int button)
    {
        System.out.println("Controller " + controller + " pressed " + button);
        
        if (controller == Keyboard.Player1ControllerNumber) {
            if (button == Keyboard.Player1CUp) {
                
            } else if (button == Keyboard.Player1CLeft) {
                
            } else if (button == Keyboard.Player1CDown) {
                
            } else if (button == Keyboard.Player1CRight) {
                
            } else if (button == Keyboard.Player1CPause) {
                
            }
        }
    }

    @Override
    public void controllerButtonReleased(int controller, int button)
    {
        for (int i = 0; i < Input.getControllerCount(); i++)
        {
            if (button == Keyboard.Player1CPause || button == Keyboard.Player1CMenu) {
                //Player wants to skip movie.
                //Last item in the script is ALWAYS the default way to get out.
                //move to that state number
                String state = Script.get(Script.size() - 1).toString();
                state = state.substring(state.lastIndexOf("~") + 1);
                //System.out.println(state);
                SwapState(Integer.valueOf(state));
            }
        }
    }
}