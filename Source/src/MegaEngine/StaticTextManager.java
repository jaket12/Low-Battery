package MegaEngine;

import org.newdawn.slick.*;

/*
 * A container class for text to be drawn on screen.
 * 
 * Text is expected to be noninteractable objects which
 * are only drawn to screen and updated for movement and effects.
 * 
 * Examples: Menu titles, Menu help info, fog effects, screen colorations.
 */
public class StaticTextManager 
{
    private MenuText[] Text;
    private boolean[] TextExists;
    private int ArraySize = -1;
    private int iDraw = 0; //Dedicated iterator for Draw()
    private int iUpdate = 0; //Dedicated iterator for Update()
    
    /*
     * Constructor
     * Image array will start with a max count of 10 by default.
     */
    public StaticTextManager()
    {
        Text = new MenuText[1];
        TextExists = new boolean[1];
        ArraySize = 1;
        MarkAllText(false);
    }//End Constructor
    
    /*
     * Constructor.
     * Initializes the image array with a specified size.
     */
    public StaticTextManager(int InitialSize)
    {
        Text = new MenuText[InitialSize];
        TextExists = new boolean[InitialSize];
        ArraySize = InitialSize;
        MarkAllText(false);
    }//End Constructor
    
    /*
     * Iterates through the image array and marks all images
     * as either exists (true) or does not exist (false).
     * Images which do not exist will not be drawn or updated
     * and may be overwritten by new images which are created.
     */
    private void MarkAllText(boolean value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            TextExists[i] = value;
        }
    }//End MarkAllImages()
    
    /*
     * Mark an image to exist (true) or not (false).
     * Images which do not exist are not drawn and will
     * be written over if a new image is created.
     */
    private void MarkText(int location, boolean value)
    {
        TextExists[location] = value;
    }
    
    /*
     * Insert an image into the array.
     * If array has no available space the array will be increased.
     */
    public void AddText(String words, float X, float Y, int Trans)
    {
        int freeslot = -1;
        int i;
        
        for (i = 0; freeslot == -1 && i < ArraySize; i++)
        {
            if (!TextExists[i])
            { //Free slot found, exit loop
                freeslot = i;
            }
        }
    
        if (freeslot != -1)
        { //Insert the image
            try
            {
                Text[freeslot] = new MenuText(words, X, Y, Trans);
                TextExists[freeslot] = true;
            } 
            catch (Exception e) 
            {
                System.out.println("Error in StaticTextManager while trying to AddText()");
                System.out.println(e);
            }
            
        }
        else
        { //No free space was found in array.  Increase size and try again.
            ExpandArraySize(1);
            AddText(words, X, Y, Trans);
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
        
        MenuText[] TempText = Text;
        boolean[] TempExists = TextExists;
        
        //Copy all data to a temporary location
        System.arraycopy(Text, 0, TempText, 0, ArraySize);
        System.arraycopy(TextExists, 0, TempExists, 0, ArraySize);
        
        //Increase the length of the array        
        Text = new MenuText[NewArraySize];
        TextExists = new boolean[NewArraySize];
        ArraySize = NewArraySize;
        
        //Copy the data back into the usable objects
        System.arraycopy(TempText, 0, Text, 0, OldArrayLength);
        System.arraycopy(TempExists, 0, TextExists, 0, OldArrayLength);
        
    }//End ExpandArraySize()
    
    /*
     * Display every image to the screen if it exists
     */
    public void Draw(Graphics g)
    {
        for (iDraw = 0; iDraw < ArraySize; iDraw++)
        {
            if (TextExists[iDraw])
            {
                Text[iDraw].draw(g);
            }
        }
        
    }//End Draw()
    
    public void Update(int delta)
    {
        for (iUpdate = 0; iUpdate < ArraySize; iUpdate++)
        {
            if (TextExists[iUpdate])
            {
                Text[iUpdate].Update(delta);
            }
        }
    }//End Update()
    
    /*
     * Given a specified image in the array, the text will be centered
     * upon the screen.
     * 
     * This will change the X position of the text, and is an approximate value.
     */
    public void CenterText(int imagenumber)
    {
        Text[imagenumber].centerText();
    }
    
    /*
     * Perform a text centering on the screen for that last added text.
     */
    public void CenterTextLast()
    {
        Text[ArraySize - 1].centerText();
    }
    
    public void ChangeText(int location, String text)
    {
        Text[location].setText(text);
    }
 
    public void AllVanish() {
        for (MenuText text : Text) {
            text.Vanish();
        }
    }
    
    public void AllAppear() {
        for (MenuText text : Text) {
            text.Appear();
        }
    }
    
}
