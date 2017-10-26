package MegaEngine;

import org.newdawn.slick.*;

/*
 * A container class for images to be drawn on screen.
 * 
 * Images are expected to be noninteractable objects which
 * are only drawn to screen and updated for movement and effects.
 * 
 * Examples: Menu titles, backgrounds, fog effects, screen colorations.
 */
public class StaticImageManager 
{
    private UIImage[] Images;
    private boolean[] ImageExists;
    private int ArraySize = -1;
    private int iDraw = 0; //Dedicated iterator for Draw()
    private int iUpdate = 0; //Dedicated iterator for Update()
    
    /*
     * Constructor
     * Image array will start with a max count of 10 by default.
     */
    public StaticImageManager()
    {
        Images = new UIImage[10];
        ImageExists = new boolean[10];
        ArraySize = 10;
        MarkAllImages(false);
    }//End Constructor
    
    /*
     * Constructor.
     * Initializes the image array with a specified size.
     */
    public StaticImageManager(int InitialSize)
    {
        Images = new UIImage[InitialSize];
        ImageExists = new boolean[InitialSize];
        ArraySize = InitialSize;
        MarkAllImages(false);
    }//End Constructor
    
    /*
     * Iterates through the image array and marks all images
     * as either exists (true) or does not exist (false).
     * Images which do not exist will not be drawn or updated
     * and may be overwritten by new images which are created.
     */
    private void MarkAllImages(boolean value)
    {
        int i;
        for (i = 0; i < ArraySize; i++)
        {
            ImageExists[i] = value;
        }
    }//End MarkAllImages()
    
    /*
     * Mark an image to exist (true) or not (false).
     * Images which do not exist are not drawn and will
     * be written over if a new image is created.
     */
    private void MarkImage(int location, boolean value)
    {
        ImageExists[location] = value;
    }
    
    /*
     * Insert an image into the array.
     * If array has no available space the array will be increased.
     */
    public void AddImage(String ImagePath, float X, float Y, boolean Visible)
    {
        int freeslot = -1;
        int i;
        
        for (i = 0; freeslot == -1 && i < ArraySize; i++)
        {
            if (!ImageExists[i])
            { //Free slot found, exit loop
                freeslot = i;
            }
        }
    
        if (freeslot != -1)
        { //Insert the image
            try
            {
                Images[freeslot] = new UIImage(ImagePath, X, Y, Visible);
                ImageExists[freeslot] = true;
            } 
            catch (Exception e) 
            {
                System.out.println("Error in StaticImageManager while trying to AddImage()");
                System.out.println(e);
            }
            
        }
        else
        { //No free space was found in array.  Increase size and try again.
            ExpandArraySize(10);
            AddImage(ImagePath, X, Y, Visible);
        }
        
    }//End AddImage()
    
    
    public void AddImage(String SpriteSheetPath, int duration, int framewidth, int frameheight, int spacing, int margin, float X, float Y, boolean Visible, boolean Animating)
    {
        int freeslot = -1;
        int i;
        
        for (i = 0; freeslot == -1 && i < ArraySize; i++)
        {
            if (!ImageExists[i])
            { //Free slot found, exit loop
                freeslot = i;
            }
        }
    
        if (freeslot != -1)
        { //Insert the image
            try
            {
                Images[freeslot] = new UIImage(SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating);
                ImageExists[freeslot] = true;
            } 
            catch (Exception e) 
            {
                System.out.println("Error in StaticImageManager while trying to AddImage()");
                System.out.println(e);
            }
            
        }
        else
        { //No free space was found in array.  Increase size and try again.
            ExpandArraySize(10);
            AddImage(SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating);
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
        boolean[] TempExists = ImageExists;
        
        //Copy all data to a temporary location
        System.arraycopy(Images, 0, TempImages, 0, ArraySize);
        System.arraycopy(ImageExists, 0, TempExists, 0, ArraySize);
        
        //Increase the length of the array        
        Images = new UIImage[NewArraySize];
        ImageExists = new boolean[NewArraySize];
        ArraySize = NewArraySize;
        
        //Copy the data back into the usable objects
        System.arraycopy(TempImages, 0, Images, 0, OldArrayLength);
        System.arraycopy(TempExists, 0, ImageExists, 0, OldArrayLength);
        
    }//End ExpandArraySize()
    
    /*
     * Display every image to the screen if it exists
     */
    public void Draw(Graphics g)
    {
        for (iDraw = 0; iDraw < ArraySize; iDraw++)
        {
            if (ImageExists[iDraw])
            {
                Images[iDraw].draw();
            }
        }
        
    }//End Draw()
    
    public void Update(int delta)
    {
        for (iUpdate = 0; iUpdate < ArraySize; iUpdate++)
        {
            if (ImageExists[iUpdate])
            {
                Images[iUpdate].Update(delta);
            }
        }
    }
 
    public void Appear(int index) {
        Images[index].Appear();
    }
    
    public void Vanish(int index) {
        Images[index].Vanish();
    }
    
    public void FadeIn(int index) {
        Images[index].fadeIn();
    }
    
    public void FadeIn(int index, int rate) {
        Images[index].fadeIn(rate);
    }
    
    public void FadeOut(int index) {
        Images[index].fadeOut();
    }
    
    public void FadeOut(int index, int rate) {
        Images[index].fadeOut(rate);
    }
}
