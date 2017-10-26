package MegaEngine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.*;

/**
 * Container class for images that are used in the movie state.
 * This will handle the loading, updating, and drawing of images.
 * @author McRib
 */
public class MovieImageManager 
{
    private UIImage[] Images;
    private int ArraySize = -1; //Equal to images.length for efficency purposes
    private int iDraw = 0; //Dedicated iterator for Draw()
    private int iUpdate = 0; //Dedicated iterator for Update()
    
    /*
     * Constructor
     * Image array will start with a max count of 10 by default.
     */
    public MovieImageManager()
    {
        Images = new UIImage[0];
        ArraySize = 0;
    }//End Constructor

    /*
     * Insert an image into the array.
     * If array has no available space the array will be increased.
     */
    public void AddStillImage(String ImagePath, float X, float Y, boolean Visible, String Keyword)
    {
        try {
            ExpandArraySize(1);
            Images[ArraySize - 1] = new UIImage(ImagePath, X, Y, Visible);
            Images[ArraySize - 1].SetKeyword(Keyword);
        } catch (SlickException ex) {
            Logger.getLogger(MovieImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
   
    }//End AddImage()
    
    
    public void AddAnimatedImage(String SpriteSheetPath, int duration, int framewidth, int frameheight, int spacing, int margin, float X, float Y, boolean Visible, boolean Animating, String Keyword)
    {
        try {
            ExpandArraySize(1);
            Images[ArraySize - 1] = new UIImage(SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating);
            Images[ArraySize - 1].SetKeyword(Keyword);
        } //End AddImage()
        catch (SlickException ex) {
            Logger.getLogger(MovieImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }//End AddImage()
    
    /*
     * Increase the array size for the Image array so that more
     * images may be loaded at one time.
     * 
     * Be sure to set a reasonable size that will actually be used,
     * as each image will be iterated through whether it exists or not.
     */
    private void ExpandArraySize(int extrasize)
    {
        int OldArrayLength = ArraySize;
        int NewArraySize = ArraySize + extrasize;
        
        UIImage[] TempImages = Images;
        
        //Copy all data to a temporary location
        System.arraycopy(Images, 0, TempImages, 0, ArraySize);
        
        //Increase the length of the array        
        Images = new UIImage[NewArraySize];
        ArraySize = NewArraySize;
        
        //Copy the data back into the usable objects
        System.arraycopy(TempImages, 0, Images, 0, OldArrayLength);
        
    }//End ExpandArraySize()
    
    /*
     * Display every image to the screen if it exists
     */
    public void Draw(Graphics g)
    {
        for (iDraw = 0; iDraw < ArraySize; iDraw++)
        {
                Images[iDraw].draw();
        }
        
    }//End Draw()
    
    public void Update(int delta)
    {
        for (iUpdate = 0; iUpdate < ArraySize; iUpdate++)
        {
                Images[iUpdate].Update(delta);
        }
    }
    
    /**
     * Make every image on the screen instantly go invisible.
     */
    public void AllVanish()
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
                Images[i].Vanish();
        }
    }
    
    /*
     * Search the entire array for any images which have the given keyword,
     * and then make them fade into the screen.
     */
    public void FadeIn(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeIn();
            }
        }
    }
    
    public void FadeIn(String keyword, int rate)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeIn(rate);
            }
        }
    }
    
    public void FadeInBlack(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeInBlack();
            }
        }
    }
    
    public void FadeInBlack(String keyword, int rate)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeInBlack(rate);
            }
        }
    }
    
    /*
     * Search the entire array for any images which have the given keyword,
     * and then make them fade out of the screen.
     */
    public void FadeOut(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeOut();
            }
        }
    }
    
    /**
     * Make the image slowly fade invisible over time.
     * @param keyword
     * @param rate 
     */
    public void FadeOut(String keyword, int rate)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeOut(rate);
            }
        }
    }
    
    /**
     * Image goes from normal colors and turns to black over time.
     * @param keyword 
     */
    public void FadeOutBlack(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeOutBlack();
            }
        }
    }
    
    /**
     * Image goes from normal colors, and turns entirely to black over time.
     * @param keyword
     * @param rate 
     */
    public void FadeOutBlack(String keyword, int rate)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].fadeOutBlack(rate);
            }
        }
    }
    
    /**
     * Image instantly turns invisible.
     * @param keyword 
     */
    public void Vanish(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].Vanish();
            }
        }
    }
    
    /**
     * Image instantly turns visible.
     * @param keyword 
     */
    public void Appear(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].Appear();
            }
        }
    }
    
    /**
     * Set a constant speed for the image to move at horizontally.
     * @param keyword
     * @param speed 
     */
    public void SetVelocityX(String keyword, int speed)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].SetVelocityX(speed);
            }
        }
    }
    
    /**
     * Set a constant speed for the image to move at vertically.
     * @param keyword
     * @param speed 
     */
    public void SetVelocityY(String keyword, int speed)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].SetVelocityY(speed);
            }
        }
    }
    
    /**
     * Instantly set an image to a vertical location.
     * @param keyword
     * @param value 
     */
    public void SetLocationY(String keyword, int value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].setY(value);
            }
        }
    }
    
    /**
     * Instantly set an image to a horizontal location.
     * @param keyword
     * @param value 
     */
    public void SetLocationX(String keyword, int value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].setX(value);
            }
        }
    }
    
    public void AddScript(String keyword, String line)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].AddScriptLine(line);

            }
        }
    }
    
    public void StartScript(String keyword, boolean value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].SetRunScript(value);

            }
        }
    }
    
    public void MoveToLocation(String keyword, String FinalX, String FinalY, String VelX, String VelY)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].MoveToLocation(FinalX, FinalY, VelX, VelY);

            }
        }
    }
    
    public void FlipHorizontally(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                System.out.println("loop");
                Images[i].FlipHorizontally();

            }
        }
    }
    
    public void FlipVertically(String keyword)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Images[i].GetKeyword().equals(keyword))
            {
                Images[i].FlipVertically();

            }
        }
    }
}