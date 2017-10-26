package MegaEngine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.*;


/*
 * A class which represents an option inside of an in game menu system.
 * 
 * Options may be text, image, both, and multiples of either type.
 * The specific items which will be displayed are dependant upon 'keywords'
 * that are assigned to them.
 */
public class MenuOption {

    protected boolean isSelected = false;
    protected boolean isActivated = false;
    protected boolean OptionExists = false; //Whether or not this option should be considered in the manager
    protected boolean isMultiOption = false;//If true, this option has more than one way to be activated (left/right buttons)
    protected boolean OptionContainsImages = false;
    protected boolean OptionContainsText = false;
    
    protected UIImage[] OptionImage;
    protected MenuText[] OptionText;
    protected int ImageArraySize = 0;
    protected int TextArraySize = 0;
    protected int iImage = 0;
    protected int iText = 0;
    
    protected String KeyString = "";
    protected float KeyInt = 0;

    protected float SelectorX = 0; //When this option is highlighted, the selector image
    protected float SelectorY = 0; //Should be placed on this location
    

    public MenuOption()
    {
        
    }
    
    public MenuOption(String keystring, float keyint, float SelectX, float SelectY)// throws SlickException 
    {
        KeyString = keystring;
        KeyInt = keyint;
        SelectorX = SelectX;
        SelectorY = SelectY;
        
    }

    public void draw(Graphics g) 
    {        
        if (OptionExists)
        {
            if (OptionContainsImages)
            {
                for (iImage = 0; iImage < ImageArraySize; iImage++)
                {
                    OptionImage[iImage].draw();
                }
            }

            if (OptionContainsText)
            {
                for (iText = 0; iText < TextArraySize; iText++)
                {
                        OptionText[iText].draw(g, isSelected, isActivated);
                }
            }
        }
        

    }//End Draw

    public void Update(int delta) 
    {
        if (OptionExists)
        {
            if (OptionContainsImages)
            {
                for (iImage = 0; iImage < ImageArraySize; iImage++)
                {
                    OptionImage[iImage].Update(delta);
                }
            }

            if (OptionContainsText)
            {
                for (iText = 0; iText < TextArraySize; iText++)
                {
                    OptionText[iText].Update(delta);
                }
            }
        }
        
  
    }//End Update
    
    public boolean OptionExists()
    {
        return OptionExists;
    }
    
    public float getSelectorLocationX()
    {
        return SelectorX;
    }
    
    public float getSelectorLocationY()
    {
        return SelectorY;
    }
    
    public boolean isMultiOption()
    {
        return isMultiOption;
    }
    
    public void SubtractKeyValue()
    {
        
    }
    
    public void AddKeyValue()
    {
        
    }
    
    public void Activate()
    {
        
    }
    
    public void Selected(boolean value)
    {
        isSelected = value;
    }
    
    public void CenterTextLast()
    {
        if (TextArraySize > 0)
        OptionText[TextArraySize - 1].centerText();
    }
    
    public void AddText(String text, float LocX, float LocY, Color textcolor)
    {
        ExpandTextArraySize(1);
        OptionText[OptionText.length - 1] = new MenuText(text, LocX, LocY, textcolor.r, textcolor.g, textcolor.b, textcolor.a, -1);
        OptionContainsText = true;
        
    }
    
    public void AddStillImage(String StillImagePath, float X, float Y, boolean Visible, int keyint)
    {
        ExpandImageArraySize(1);
        try {
            OptionImage[OptionImage.length - 1] = new UIImage(StillImagePath, X, Y, Visible);
            OptionImage[OptionImage.length - 1].SetKeyInt(keyint);
            OptionContainsImages = true;
        } catch (SlickException ex) {
            Logger.getLogger(MenuOptionButton.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void AddAnimatedImage(String SpriteSheetPath, int duration, int framewidth, int frameheight, int spacing, int margin, float X, float Y, boolean Visible, boolean Animating, int keyint)
    {
        ExpandImageArraySize(1);
        try {
            OptionImage[OptionImage.length - 1] = new UIImage(SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating);
            OptionImage[OptionImage.length - 1].SetKeyInt(keyint);
            OptionContainsImages = true;
        } catch (SlickException ex) {
            Logger.getLogger(MenuOptionButton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Increase the array size by a specific number.
     * 
     * For MenuOption, this function should only be used by the program itself.
     * The array must no hold any empty items.
     */
    protected void ExpandImageArraySize(int extrasize)
    {
        if (ImageArraySize > 0)
        {
            int OldArrayLength = ImageArraySize;
            int NewArraySize = ImageArraySize + extrasize;

            UIImage[] TempImages = new UIImage[ImageArraySize];

            //Copy all data to a temporary location
            System.arraycopy(OptionImage, 0, TempImages, 0, ImageArraySize);

            //Increase the length of the array        
            OptionImage = new UIImage[NewArraySize];

            //Copy the data back into the usable objects
            System.arraycopy(TempImages, 0, OptionImage, 0, OldArrayLength);
            ImageArraySize = NewArraySize;
        }
        else 
        {
            //The array has not been used and is null or empty.
            //Just create it
            OptionImage = new UIImage[extrasize];
            ImageArraySize = extrasize;
        }
        
        
    }//End ExpandArraySize()
    
    /*
     * Increase the array size by a specific number.
     * 
     * For MenuOption, this function should only be used by the program itself.
     * The array must no hold any empty items.
     */
    protected void ExpandTextArraySize(int extrasize)
    {
        if (TextArraySize > 0)
        {
              int OldArrayLength = TextArraySize;
            int NewArraySize = TextArraySize + extrasize;

            MenuText[] TempText = OptionText;

            //Copy all data to a temporary location
            System.arraycopy(OptionText, 0, TempText, 0, TextArraySize);

            //Increase the length of the array        
            OptionText = new MenuText[NewArraySize];

            //Copy the data back into the usable objects
            System.arraycopy(TempText, 0, OptionText, 0, OldArrayLength);
            TextArraySize += extrasize;
        } else
        {
            //Array was either null or 0.  Just initialize it
            OptionText = new MenuText[extrasize];
            TextArraySize = extrasize;
        }
      
        
    }//End ExpandArraySize()
    

}
