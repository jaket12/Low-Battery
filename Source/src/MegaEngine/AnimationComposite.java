package MegaEngine;

import java.util.ArrayList;
import org.newdawn.slick.*;
import org.newdawn.slick.opengl.renderer.SGL;

public class AnimationComposite
{
    SpriteSheet subsheet;
    
    int Layers;//How many image layers there are.  One per color.
    
    int CurrentAnimation = 0; //The current animation to play
    int PreviousAnimation = 0; //The animation last frame.  If different, reset the duration
    
    int SheetNumber;//Each animation is a different number
    boolean CustomColors;//if the sprite and it's layers should be tinted by color
    float[] Red;//The amount of red to tint in this layer (0-none, 255-all)
    float[] Green;
    float[] Blue;
    float[] Transparency;
    
    int AnimationRow = 0; //The current row of the animation being played
    int AnimationColumn = 0;//Combined with row and column, gives a specific frame in the sheet
    int AnimationRowMax = 7;//Total number of frames in a single row
    int AnimationColumnMax = 4;
    
    ArrayList<AnimationData> FrameData;//Holds the co-ords, offset, duration, and flip of this frame
    
    int ArraySize = 0;
    int CurrentFrame = 0;
    int CurrentInterval = 0;
    int StartLoop = 0;//When the animation begins to repeat, start on this frame
    
    boolean isAnimating;//if true, the interval is reduced by delta for animation.
    boolean isFlippedHorz; //if this animation is currently clipped horizontally
    boolean isFlippedVert;
    int SpriteWidth;//The size of any frame in this animation.
    int SpriteHeight;//used for speeding up the drawing
    
    private boolean RunOnce;// If true, the animation plays one time and goes back to the start frame
    private boolean isRunningOnce;
    private boolean StopAtExactFrame;//If true, when the animation stops it will show a specific frame
    private int ExactFrameStop;//plays at this frame only
    
    private Timer FadeInTimer = new Timer("Fade In", 1000, false, false);
    private boolean IsFadingIn = false;
    private float FadeInSpeed = 1f;//How much color is added per second, from 0 to 1.
    private boolean FadeInColor = true;
    
    /**
     * 
     * @param sheetnumber SpriteSheets[Index] in Global
     * @param layers How many images will be overlaid for form the final product. 1 is flat image.
     * @param spritewidth Width of the individual frames on the SpriteSheet. Match this to the second parameter it uses. Should refactor to map this automatically.
     * @param spriteheight
     * @param startloop When the animation reaches the end, jump back to this frame number to repeat a loop
     * @param flipH Horizontally flip the SpriteSheet image when drawing
     * @param flipV Vertically flip the SpriteSheet image when drawing
     */
    public AnimationComposite(int sheetnumber, int layers, int spritewidth, int spriteheight, int startloop, boolean flipH, boolean flipV)
    {
        SheetNumber = sheetnumber;//in order that they will be called
        Layers = layers;
        Red = new float[Layers];
        Green = new float[Layers];
        Blue = new float[Layers];
        Transparency = new float[Layers];
        for (int i = 0; i < Layers; i++)
        {
            Red[i] = 1;
            Green[i] = 1;
            Blue[i] = 1;
            Transparency[i] = 1;
        }
        
        //generate the amount of animations needed for the layers
        FrameData = new ArrayList<AnimationData>();
        StartLoop = startloop;
        isAnimating = true;
        
        SpriteWidth = spritewidth;
        SpriteHeight = spriteheight;
        CustomColors = true;
        
        isFlippedHorz = flipH;
        isFlippedVert = flipV;
    }
    
    public void AddFrame(int row, int column, int duration, int offsetx, int offsety)
    {
        AnimationData newframe = new AnimationData(Layers, row, column, duration, offsetx, offsety);
        newframe.setFrameNumber(ArraySize);
        FrameData.add(newframe);
        ArraySize++;
    }
    
    public void AddEventToFrame(int framenumber, int eventnumber)
    {
        AnimationData temp = FrameData.get(framenumber);
        temp.addEvent(eventnumber);
        FrameData.set(framenumber, temp);
    }
    
    /*
     * Add another image to a specific frame in the animation, creating a composite final.
     * All images MUST be from the same sprite sheet in the main database
     */
    public void AddFrameLayer(int frame, int layernumber, int row, int column, int offsetx, int offsety)
    {
        FrameData.get(frame).AddLayer(layernumber, row, column, offsetx, offsety);
    }
    
    public void setLayerColor(int layer, float red, float green, float blue, float transparency)
    {
        Red[layer] = red;
        Green[layer] = green;
        Blue[layer] = blue;
        Transparency[layer] = transparency;
    }
    
    public void setCurrentAnimation(int value)
    {
        CurrentFrame = value;
        CurrentInterval = FrameData.get(value).getDuration();
    }
    public void Update(int delta)
    {
        if (isAnimating)
        {
            CurrentInterval -= delta;
            if (CurrentInterval < 0)
            {
                CurrentFrame++;
                if (CurrentFrame >= ArraySize)
                {
                    if (StartLoop != -1)
                    {
                        CurrentFrame = StartLoop;
                    }
                }

                CurrentInterval = FrameData.get(CurrentFrame).getDuration();//Max duration for this frame
            }
        }
        
        if (IsFadingIn)
        {
            if (FadeInTimer.Update(delta))
            {//Fade in has ended
                IsFadingIn = false;
            } else
            {
                if (FadeInColor)
                {
                    //Increase the color
                    AddToAllColor(FadeInSpeed * (delta/1000f));
                }
                AddTransparency(FadeInSpeed * (delta/1000f));
            }
        }
    }
   
    public void Draw(Graphics g, SGL gl, float ScreenX, float ScreenY)
    {
        if (main.Global.inUse() && main.Global.SheetInUse == SheetNumber)
        {//The correct sheet is loaded and being used, so draw
            for (int i = 0; i < Layers; i++)
            {//Draw each layer for the current animation frame
                subsheet = new SpriteSheet(main.Global.CurrentSpriteSheet.getSprite(FrameData.get(CurrentFrame).getLayerColumn(i), FrameData.get(CurrentFrame).getLayerRow(i)).getFlippedCopy(isFlippedHorz, isFlippedVert), SpriteWidth, SpriteHeight);
                if (CustomColors)
                {//Use the specific colors given for this frame's layers
                    gl.glColor4f(Red[i], Green[i], Blue[i], Transparency[i]);
                }
                subsheet.renderInUse((int)ScreenX + FrameData.get(CurrentFrame).getLayerOffsetX(i), (int)ScreenY + FrameData.get(CurrentFrame).getLayerOffsetY(i), 0, 0);
            }
        }
        else
        {
            //Set up the sheet to be the one we want
            main.Global.endUse();
            main.Global.SheetInUse(SheetNumber);
            main.Global.startUse();
            
            //and then draw it as usual
            for (int i = 0; i < Layers; i++)
            {//Draw each layer for the current animation frame
                subsheet = new SpriteSheet(main.Global.CurrentSpriteSheet.getSprite(FrameData.get(CurrentFrame).getLayerColumn(i), FrameData.get(CurrentFrame).getLayerRow(i)).getFlippedCopy(isFlippedHorz, isFlippedVert), SpriteWidth, SpriteHeight);
                if (CustomColors)
                {//Use the specific colors given for this frame's layers
                    gl.glColor4f(Red[i], Green[i], Blue[i], Transparency[i]);
                }
                subsheet.renderInUse((int)ScreenX + FrameData.get(CurrentFrame).getLayerOffsetX(i), (int)ScreenY + FrameData.get(CurrentFrame).getLayerOffsetY(i), 0, 0);
            }
        }
        
    }
    
    public void Draw(Graphics g, SGL gl, float ScreenX, float ScreenY, ColorData Scheme)
    {
        if (main.Global.inUse() && main.Global.SheetInUse == SheetNumber)
        {//The correct sheet is loaded and being used, so draw
            for (int i = 0; i < Layers; i++)
            {//Draw each layer for the current animation frame
                subsheet = new SpriteSheet(main.Global.CurrentSpriteSheet.getSprite(FrameData.get(CurrentFrame).getLayerColumn(i), FrameData.get(CurrentFrame).getLayerRow(i)).getFlippedCopy(isFlippedHorz, isFlippedVert), SpriteWidth, SpriteHeight);
                //Use the specific colors given for this frame's layers
                gl.glColor4f(Scheme.RedF(i), Scheme.GreenF(i), Scheme.BlueF(i), Scheme.Transparency(i));
                subsheet.renderInUse((int)ScreenX + FrameData.get(CurrentFrame).getLayerOffsetX(i), (int)ScreenY + FrameData.get(CurrentFrame).getLayerOffsetY(i), 0, 0);
            }
        }
        else
        {
            //Set up the sheet to be the one we want
            main.Global.endUse();
            main.Global.SheetInUse(SheetNumber);
            main.Global.startUse();
            
            //and then draw it as usual
            for (int i = 0; i < Layers; i++)
            {//Draw each layer for the current animation frame
                subsheet = new SpriteSheet(main.Global.CurrentSpriteSheet.getSprite(FrameData.get(CurrentFrame).getLayerColumn(i), FrameData.get(CurrentFrame).getLayerRow(i)).getFlippedCopy(isFlippedHorz, isFlippedVert), SpriteWidth, SpriteHeight);
                gl.glColor4f(Scheme.RedF(i), Scheme.GreenF(i), Scheme.BlueF(i), Scheme.Transparency(i));
                subsheet.renderInUse((int)ScreenX + FrameData.get(CurrentFrame).getLayerOffsetX(i), (int)ScreenY + FrameData.get(CurrentFrame).getLayerOffsetY(i), 0, 0);
            }
        }
        
    }
    
    public int getRow(int value)
    {
        return FrameData.get(CurrentFrame).getLayerRow(value);
    }
    
    public int getColumn(int value)
    {
        return FrameData.get(CurrentFrame).getLayerColumn(value);
    }
    
    public int getLayers()
    {
        return Layers;
    }
    
    public void Reset()
    {
        //reset the animation to the first frame and it's duration
        CurrentFrame = 0;
        CurrentInterval = FrameData.get(CurrentFrame).getDuration();
    }

    public float getRed(int layer)
    {
        return Red[layer];
    }
    
    public float getGreen(int layer)
    {
        return Green[layer];
    }
    
    public float getBlue(int layer)
    {
        return Blue[layer];
    }
    
    public float getTransparency(int layer)
    {
        return Transparency[layer];
    }
    
    public boolean IsStopped()
    {
        return !isAnimating;
    }
    
    public void EnableCustomColors(boolean value)
    {
        CustomColors = value;
    }
    
    public void EnableAnimation(boolean value)
    {
        isAnimating = value;
    }

    public AnimationData getCurrentFrame()
    {
        return FrameData.get(CurrentFrame);
    }
    
    public void setTransparency(float value)
    {
        for (int i = 0; i < Layers; i++)
        {
            Transparency[i] = value;
        }
    }
    
    public void AddTransparency(float value)
    {
        for (int i = 0; i < Layers; i++)
        {
            Transparency[i] += value;
        }
    }
    
    public float getCurrentInterval()
    {
        return CurrentInterval;
    }
    
    public void isAnimating(boolean value)
    {
        isAnimating = value;
    }
    
    public void FadeInFromBlack(int duration, float rate)
    {
        setLayersBlack();
        FadeInColor = true;
        FadeIn(duration, rate);
    }
    
    /*
     * Turn the entire frame to black.
     * Transparency is not affected.
     */
    public void setLayersBlack()
    {
        for (int i = 0; i < Layers; i++)
        {
            Red[i] = 0;
            Green[i] = 0;
            Blue[i] = 0;
        }
    }
    
    private void FadeIn(int duration, float rate)
    {
        IsFadingIn = true;
        FadeInSpeed = rate;
        FadeInTimer.SetDuration(duration);
        FadeInTimer.Reset();
        FadeInTimer.Start();
    }
    
    private void AddToAllColor(float amount)
    {
        for (int i = 0; i < Layers; i++)
        {
            Red[i] += amount;
            Green[i] += amount;
            Blue[i] += amount;
        }
    }
    
    public int getCurrentFrameNumber()
    {
        return FrameData.get(CurrentFrame).getFrameNumber();
    }
}