package MegaEngine;

import org.newdawn.slick.*;
import java.util.*;

public class UIImage {

    //Menu Animation variables
    private boolean isAnimated = false;
    private SpriteSheet sheet;
    private Image Image;
    private Animation MenuAnimation; //Holds a sprite sheet
    private int AnimationRow = 0; //The current row of sprites to show
    private boolean TransparencyWasModified = false; //Check to see if every frame should be updated due to an effect on alpha
    boolean playAnimation = true;
    
    //Location and movements
    private float LocX = 0;
    private float LocY = 0;
    private float VelocityX = 0;
    private float VelocityY = 0;
    private float DestinationX = 0; //For move commands, where image should stop
    private float DestinationY = 0;
    private int DestinationDirectionX = 0; //See which way object should move
    private int DestinationDirectionY = 0;
    private boolean isMoving = false; //If this object has a destination to reach
    
    //Visual effects
    private float Transparency = 255; //0 is invisible, 255 is visible
    private Color TransparentColor = new Color(0, 128, 0); //The color which is used to be invisible.  Default 0,128,0 green
    private Color FilterColor = new Color(1f,1f,1f,1f);//Image color tinting. Used for fading
    private int Ti = 0; //Dedicated iterator
    private boolean isFadingOut = false;
    private boolean isFadingIn = false;
    private boolean isVisible;
    private float FadeOutRate = 255f;
    private float FadeInRate = 255f;
    private float FadeOutRateDefault = 255f;
    private float FadeInRateDefault = 255f;
    private boolean isFadingBlack = false;//If true, the image fades to black, does not go invisible. Already transparent pixels remain transparent
    private boolean isFadingTransparent = false;//If true, the image fades invisible. Pixels behind the image will become visible.
    
    //free use data
    private String Keyword = ""; //Used for when an image needs to do something based on a name given to it
    protected int KeyInt = -1; //Used for quicker comparisons as a keyword.
    //Current use for Keyint resides in MenuOption:
    //0 means UIImage exists but is not selected nor activated
    //1 is selected
    //2 is activated
    //3 is always show
    
    //Script data
    private ArrayList Script = new ArrayList(); //Each line in a script file is placed in order here.
    private int ReadLine = 0; //Current Line in the script being read
    private String ScriptCurrentLine = ""; //A copied peice of that line
    private boolean RunScript = false; //Should the script be performed on this update?
    private int WaitTimer = 0; //The time in milliseconds to wait until it reads the next line in a script

    private String splits[]; //Dedicated string splitter for efficency
    
    private boolean FlipHorizontally = false; //If the image drawn should be inversed
    private boolean FlipVertically = false;
    
    /*
     * Constructor.
     * Given a specified file path, location, and level of visibility, the class
     * will create an image which may be displayed on screen.
     * 
     * Although the image will be created as a spritesheet, it will only have
     * one frame that is always displayed in full width and height.
     */
    public UIImage(String StillImagePath, float X, float Y, boolean Visible) throws SlickException 
    {
        //Create the still image
        Image = new Image(StillImagePath, TransparentColor);
        
        if (Visible) 
        {
            Transparency = 255;
        } 
        else 
        {
            Transparency = 0;
        }

        isAnimated = false;
        playAnimation = false;
        LocX = X;
        LocY = Y;
        isVisible = Visible;
    }//End constructor

    /*
     * Constructor.
     * Given a sprite sheet and the necessary values to interpret the image's
     * frames, a sprite will be displayed on screen.
     */
    public UIImage(String SpriteSheetPath, int duration, int framewidth, int frameheight, int spacing, int margin, float X, float Y, boolean Visible, boolean Animating) throws SlickException 
    {
        Image temp = new Image(SpriteSheetPath, TransparentColor);
        sheet = new SpriteSheet(temp, framewidth, frameheight, spacing, margin);
        
        MenuAnimation = new Animation(sheet, duration);
        LocX = X;
        LocY = Y;
        if (Visible) 
        {
            Transparency = 255;
        } 
        else 
        {
            Transparency = 0;
        }

        if (Animating) 
        {
            playAnimation = true;
        } 
        else 
        {
            playAnimation = false;
        }

        isAnimated = true;
        isVisible = Visible;
    }//End constructor

    /*
     * Draw the image to screen.
     */
    public void draw() 
    {

        if (isVisible) 
        {
                
            if (isAnimated) 
            {
                MenuAnimation.getCurrentFrame().getFlippedCopy(FlipHorizontally, FlipVertically).draw(LocX, LocY, FilterColor);
            } 
            else
            {
                //Image.draw(LocX, LocY, FilterColor);
                Image.startUse();
             //   Image.drawEmbedded(LocX, LocY, Image.getWidth(), Image.getHeight());
                Image.drawEmbedded(LocX, LocY, LocX + Image.getWidth(), LocY + Image.getHeight(), 0, 0, Image.getWidth(), Image.getHeight(), FilterColor);
            //    System.out.println(Image.getAlpha());
             //   System.out.println(FilterColor.a);
                Image.endUse();
            }
        }
        
    }

    /*
     * Draw a part of an image
     */
    public void draw(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4)
    {
        if (isVisible) 
        {
            if (isAnimated) 
            {
                MenuAnimation.getCurrentFrame().getFlippedCopy(FlipHorizontally, FlipVertically).draw(LocX, LocY, FilterColor);
            }
            else
            {
                Image.startUse();
                Image.drawEmbedded(x1, y1, x2, y2, x3, y3, x4, y4);
                Image.endUse();
            }
        }
    }
    
    public float getX() {
        return LocX;
    }

    public void setX(float x) {
        LocX = x;
    }

    public float getY() {
        return LocY;
    }

    public void setY(float Y) {
        LocY = Y;
    }

    public float getAlpha() {
        return Transparency;
    }

    public void setAlpha(float value) {
        Transparency = value;
    }

    /*
     * Run the logical functions of the image.
     * 
     * Should the image have any movement or other commands, this function
     * will execute them based on real time elapsed.
     * 
     * Current actions being updated:
     * - Execute script commands, if any
     * - Movement and velocity across the screen
     * - Calculate levels of transparency
     */
    public void Update(int delta)
    {
        if (isAnimated)
        {
            MenuAnimation.update(delta);
        }
        
        WaitTimer -= delta;
        if (RunScript && WaitTimer <= 0 && ReadLine < Script.size())
        { //Read next line in script and execute it
           RunNextScript();
        }

        if(isMoving)
        {
            CheckIfDestinationIsReached();
        }

        LocY += VelocityY * (delta / 1000f);
        LocX += VelocityX * (delta / 1000f);
        
        if (isFadingOut) 
        {
            Transparency -= FadeOutRate * (delta / 1000f);            
            TransparencyWasModified = true;
        }
        else if (isFadingIn) 
        {
            Transparency += FadeInRate * (delta / 1000f);
            TransparencyWasModified = true;
        }
        
        if (Transparency > 255) 
        {
            Transparency = 255;
            isFadingIn = false;
            isVisible = true;
            isFadingTransparent = false;
            isFadingBlack = false;
            TransparencyWasModified = true;
        } 
        else if (Transparency < 0) 
        {
            Transparency = 0;
            isFadingOut = false;
            isVisible = false;
            isFadingTransparent = false;
            isFadingBlack = false;
            TransparencyWasModified = true;
        }

        //If any updates to alpha are made, it must apply to all images of sprite
        //This is to prevent flicker on the animation
        if (TransparencyWasModified) 
        {
            if (isAnimated)
            {
                for (Ti = 0; Ti < MenuAnimation.getFrameCount(); Ti++) 
                {
                    if (isFadingBlack)
                    {
                        FilterColor.r = (Transparency / 255);
                        FilterColor.g = (Transparency / 255);
                        FilterColor.b = (Transparency / 255);
                    }
                    if (isFadingTransparent)
                    {
                        FilterColor.a = (Transparency / 255);
                    }
                    MenuAnimation.getImage(Ti).setAlpha(Transparency);
                }
            }
            else
            {
                if (isFadingBlack)
                    {
                        FilterColor.r = (Transparency / 255);
                        FilterColor.g = (Transparency / 255);
                        FilterColor.b = (Transparency/ 255);
                    }
                    if (isFadingTransparent)
                    {
                        FilterColor.a = (Transparency / 255);
                    }
            }
            
            TransparencyWasModified = false;
        }
       
    }

    /*
     * When the image has a destination to reach, this function will determine
     * if it has reached it location by comparing the current XY to the 
     * destination XY.
     * 
     * Once the destination is reached, the image will snap to the destination
     * co-orindates and stop moving.
     */
    private void CheckIfDestinationIsReached()
    {
        if (LocX == DestinationX || DestinationDirectionX == -1 && LocX <= DestinationX || DestinationDirectionX == 1 && LocX >= DestinationX) 
        {
            VelocityX = 0;
            LocX = DestinationX;
        }
        if (LocY == DestinationY || DestinationDirectionY == -1 && LocY <= DestinationY || DestinationDirectionY == 1 && LocY >= DestinationY) 
        {
            VelocityY = 0;
            LocY = DestinationY;
        }

        if (LocX == DestinationX && LocY == DestinationY) 
        {
            isMoving = false;
            // System.out.println("Destination reached!");
        }
    }
    
    public void fadeIn() {
        isFadingIn = true;
        isFadingTransparent = true;
        FadeInRate = FadeInRateDefault;
        isVisible = true;
    }

    public void fadeIn(int rate) {
        isFadingIn = true;
        isFadingTransparent = true;
        FadeInRate = rate;
        isVisible = true;
    }
    
    public void fadeOut() {
        isFadingOut = true;
        isFadingTransparent = true;
        FadeOutRate = FadeOutRateDefault;
        isVisible = true;
    }

    public void fadeOut(int rate) {
        isFadingOut = true;
        isFadingTransparent = true;
        FadeOutRate = rate;
        isVisible = true;
    }
    
    public void fadeInBlack()
    {
        isFadingIn = true;
        isFadingBlack = true;
        FadeInRate = FadeOutRateDefault;
        isVisible = true;
    }
    
    public void fadeInBlack(int rate)
    {
        isFadingIn = true;
        isFadingBlack = true;
        FadeInRate = rate;
        isVisible = true;
    }
    
    public void fadeOutBlack()
    {
        isFadingIn = true;
        isFadingBlack = true;
        FadeInRate = FadeOutRateDefault;
        isVisible = true;
    }
    
    public void fadeOutBlack(int rate)
    {
        isFadingIn = true;
        isFadingBlack = true;
        FadeInRate = rate;
        isVisible = true;
    }
    
    public void Vanish() {
        Transparency = 0;
        isFadingOut = false;
        isFadingIn = false;
        isVisible = false;
    }

    public void Appear() {
        Transparency = 255;
        isFadingOut = false;
        isFadingIn = false;
        isVisible = true;
    }

    public boolean IsPlaying() {
        return playAnimation;
    }

    public void IsPlaying(boolean value) {
        playAnimation = value;
        if (value) {
            MenuAnimation.start();
        } else {
            MenuAnimation.stop();
        }

    }

    /* NOTE: THIS SHOULD BE REMOVED. POINTLESS TO TIE THE FUNCTION TO ANOTHER CLASS
     * Set the current velocity of the image moving across the screen.
     * 
     * X is left/right, negative numbers are left, positive are right
     * Y is up/down, negative numbers are up, positive are down
     */
    public void SetVelocity(String CommandFromMovie) {//The parameter is expected to be like "X 15"
        String[] splitsf = CommandFromMovie.split("\\s+");

        if (splitsf[0].equals("X")) {
            SetVelocityX(Float.parseFloat(splitsf[1]));
        }

    }
    
    /*
     * Set the current velocity of the image moving across the screen.
     * 
     * X is left/right, negative numbers are left, positive are right
     * Y is up/down, negative numbers are up, positive are down
     */
    public void SetVelocity(float X, float Y) 
    {
            SetVelocityX(X);
            SetVelocityY(Y);
    }
    
    public void SetVelocityX(float value) {
        VelocityX = value;
    }

    public void SetVelocityY(float value) {
        VelocityY = value;
    }
    
    /*
     * Add a new line into the current script for the image.
     * This new line will be inserted at the end of the array.
     */
    public void AddScriptLine(String CommandFromMovie) {
        Script.add(CommandFromMovie);
    }

    /*
     * Execute the next line in the script.
     * 
     */
    private void RunNextScript() 
    {
        ScriptCurrentLine = Script.get(ReadLine).toString();
        System.out.println("Image script: " + ScriptCurrentLine);
        
        splits = ScriptCurrentLine.split("~");
        
        if (splits[0].equalsIgnoreCase("Wait"))
        {//Add time to the WaitTimer variable.
            if (splits.length == 2)
            {
                WaitTimer = Integer.parseInt(splits[1]);
            }
            
        }
        if (splits[0].equalsIgnoreCase("Fade In"))
        {
            if (splits.length == 4)
            {
                fadeIn(Integer.valueOf(splits[1]));
            }
            else
            {
                fadeIn();
            }

        }
        else if (splits[0].equalsIgnoreCase("Fade Out"))
        {
            if (splits.length == 4)
            {
                fadeOut(Integer.valueOf(splits[1]));
            }
            else
            {
                fadeOut();
            }

        }
        else if (splits[0].equalsIgnoreCase("Vanish"))
        {
            Vanish();
        }
        else if (splits[0].equalsIgnoreCase("Appear"))
        {
            Appear();
        }
        else if (splits[0].equalsIgnoreCase("Set Velocity X"))
        {
            SetVelocityX(Float.valueOf(splits[1]));
        }
        else if (splits[0].equalsIgnoreCase("Set Velocity Y"))
        {
            SetVelocityY(Integer.valueOf(splits[1]));
        }
        else if (splits[0].equalsIgnoreCase("Set Location X"))
        {
            setX(Integer.valueOf(splits[1]));
        }
        else if (splits[0].equalsIgnoreCase("Set Location Y"))
        {
            setY(Integer.valueOf(splits[1]));
        }
        else if (splits[0].equalsIgnoreCase("Add Script"))
        {
            AddScriptLine(splits[1]);
        }
        else if (splits[0].equalsIgnoreCase("Start Script"))
        {
            SetRunScript(true);
        }
        else if (splits[0].equalsIgnoreCase("Stop Script"))
        {
            SetRunScript(false);
        }
        else if (splits[0].equalsIgnoreCase("Move To"))
        {//Move the image to an exact location on the screen at a certain velocity
            //Params:
            //keyword, Destination X, Destination Y, Velocty X, Velocity Y
            //If a given destination or velocity is an "X" then that variable
            //does not matter on the destination.
            MoveToLocation(splits[1], splits[2], splits[3], splits[4]);
        }
        else if (splits[0].equalsIgnoreCase("Go To Line"))
        {
            ReadLine = (Integer.parseInt(splits[1]) - 1); //-1 For Readline++
        }
        else if (splits[0].equalsIgnoreCase("Flip Image Horizontally"))
        {
            FlipHorizontally();
            
        }
        else if (splits[0].equalsIgnoreCase("Flip Image Vertically"))
        {
            FlipVertically();
        }
        else if (splits[0].equalsIgnoreCase("Sound"))
        {
            if (splits[1].equalsIgnoreCase("Play"))
            {
                main.Global.Sound.PlaySound(Integer.valueOf(splits[2]));
            }
            else if (splits[1].equalsIgnoreCase("Stop All"))
            {
                main.Global.Sound.StopAllSound();
            }
            else if (splits[1].equalsIgnoreCase("Stop"))
            {
                main.Global.Sound.StopSound(Integer.valueOf(splits[2]));
            }
            
        }

        ReadLine++;

    }

    public void FlipHorizontally()
    {
        if (FlipHorizontally) {
            FlipHorizontally = false;
        } else {
            FlipHorizontally = true;
        }
        System.out.println("Flipped: " + FlipHorizontally);
    }
    
    public void FlipVertically()
    {
        if (FlipVertically)
            FlipVertically = false;
        else
            FlipVertically = true;
    }
    
    public void MoveToLocation(String FinalX, String FinalY, String VelX, String VelY) {
        //Should look like this: 400 X -15 X
        //This animation will continue moving in the velocity given until it reaches the destination
        if (FinalX.equals("X")) {
            DestinationX = LocX;
        } else {
            DestinationX = Integer.parseInt(FinalX);
        }
        if (FinalY.equals("X")) {
            DestinationY = LocY;
        } else {
            DestinationY = Integer.parseInt(FinalY);
        }
        if (VelX.equals("X")) {
        } else {
            VelocityX = Integer.parseInt(VelX);
        }
        if (VelY.equals("X")) {
        } else {
            VelocityY = Integer.parseInt(VelY);
        }

        if (DestinationX > LocX) {
            DestinationDirectionX = 1;
        } else {
            DestinationDirectionX = -1;
        }
        if (DestinationY > LocY) {
            DestinationDirectionY = 1;
        } else {
            DestinationDirectionY = -1;
        }

        isMoving = true;

    }

    public void NewSpriteSheet(String params) {//params is expected to be "Movies/Images/MegaManStandLeft.png 1000 40 44 0 0 X X F F"
        String[] splitsf = params.split("\\s+");

        SpriteSheet spsheet;
        try {
            spsheet = new SpriteSheet(splitsf[0], Integer.parseInt(splitsf[2]), Integer.parseInt(splitsf[3]), TransparentColor, Integer.parseInt(splitsf[4]));
            MenuAnimation = new Animation(spsheet, Integer.parseInt(splitsf[1]));
        } catch (SlickException ex) {
        }


        if (splitsf[6].equals("X")) {
            //Image should stay where it is for the X location
        } else { //Location X should change
            LocX = Integer.parseInt(splitsf[6]);
        }
        if (splitsf[7].equals("X")) {
            //Image should stay where it is for the Y location
        } else { //Location Y should change
            LocY = Integer.parseInt(splitsf[7]);
        }

        if (splitsf[8].equals("X")) {
            //Image should keep visible status
        } else { //Visibility should change
            if (splitsf[8].equals("T")) {
                isVisible = true;
            } else {
                isVisible = false;
            }
        }
        if (splitsf[9].equals("X")) {
            //Image should keep animation status
        } else { //Animation should stop/start
            if (splitsf[9].equals("T")) {
                playAnimation = true;
            } else {
                playAnimation = false;
            }
        }
    }

    public String GetKeyword() {
        return Keyword;
    }

    public void SetKeyword(String value) {
        Keyword = value;
    }
    
    public int GetKeyInt() {
        return KeyInt;
    }

    public void SetKeyInt(int value) {
        KeyInt = value;
    }
    
    public void SetRunScript(boolean value)
    {
        RunScript = value;
    }
    
    public void ResetAnimation()
    {
        if (isAnimated)
        {
            MenuAnimation.setCurrentFrame(0);
        }
    }
}
