package MegaEngine;

import org.newdawn.slick.*;

/**
 * Container class for all text that is used in the movie state.
 * Handles the loading, updating, and drawing of text for the state.
 * @author McRib
 */
public class MovieTextManager 
{
    
    private MenuText[] Text;
    private int ArraySize = -1; //Equal to text.length for efficency purposes
    private int iDraw = 0; //Dedicated iterator for Draw()
    private int iUpdate = 0; //Dedicated iterator for Update()
    
    /*
     * Constructor
     * Image array will start with a max count of 10 by default.
     */
    public MovieTextManager()
    {
        Text = new MenuText[0];
        ArraySize = 0;
    }//End Constructor
    /*
     * Insert an image into the array.
     * If array has no available space the array will be increased.
     */
    public void AddText(String words, float X, float Y, int red, int green, int blue, int trans, int keyint)
    {
            ExpandArraySize(1);
            Text[ArraySize - 1] = new MenuText(words, X, Y, red, green, blue, trans, keyint);
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
        
        MenuText[] Temp = Text;
        
        //Copy all data to a temporary location
        System.arraycopy(Text, 0, Temp, 0, ArraySize);
        
        //Increase the length of the array        
        Text = new MenuText[NewArraySize];
        ArraySize = NewArraySize;
        
        //Copy the data back into the usable objects
        System.arraycopy(Temp, 0, Text, 0, OldArrayLength);
        
    }//End ExpandArraySize()
    
    /*
     * Display every image to the screen if it exists
     */
    public void Draw(Graphics g)
    {
        for (iDraw = 0; iDraw < ArraySize; iDraw++)
        {
                Text[iDraw].draw(g);
        }
        
    }//End Draw()
    
    public void Update(int delta)
    {
        for (iUpdate = 0; iUpdate < ArraySize; iUpdate++)
        {
                Text[iUpdate].Update(delta);
        }
    }
    
    /**
     * Make every image on the screen fade to black.
     */
    public void AllVanish()
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
                Text[i].Vanish();
        }
    }
    
    /**
     * Search the entire array for any Text which have the given keyword,
     * and then make them fade into the screen.
     */
    public void FadeIn(int keyint)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].fadeIn();
            }
        }
    }
    
    /**
     * Specifically choose the rate in milliseconds at which the text will fade in.
     * @param keyint
     * @param rate 
     */
    public void FadeIn(int keyint, int rate)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].fadeIn(rate);
            }
        }
    }
    /*
     * Search the entire array for any Text which have the given keyword,
     * and then make them fade out of the screen.
     */
    public void FadeOut(int keyint)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].fadeOut();
            }
        }
    }
    
    /**
     * Specifically choose the speed in milliseconds at which the text will fade out.
     * @param keyint
     * @param rate 
     */
    public void FadeOut(int keyint, int rate)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].fadeOut(rate);
            }
        }
    }
    
    /**
     * Make a text instantly dissappear by reducing opacity to 0.
     * @param keyint 
     */
    public void Vanish(int keyint)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].Vanish();
            }
        }
    }
    
    public void Appear(int keyint)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].Appear();
            }
        }
    }
  
    public void SetLocationY(int keyint, int value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].setY(value);
            }
        }
    }
    
    public void SetLocationX(int keyint, int value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].setX(value);
            }
        }
    }
    
    public void setText(int keyint, String message)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].setText(message);
            }
        }
    }
    
    /**
     * Computes the width of the text and centers it in the middle of the game screen.
     * This doesn't work at all for fonts that have kerning or non standard widths for characters.
     * @param keyint 
     */
    public void CenterText(int keyint)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            if(Text[i].KeyInt == keyint)
            {
                Text[i].centerText();
            }
        }
    }
    
}