package MegaEngine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.*;
import java.awt.Point;

/**
 *
 * @author Multiplexing
 */
public class MenuSelector {

    UIImage SelectorImage;

    //The location in which selector is currently at
    int SelectionX = -1;
    int SelectionY = -1;
    int SelectionZ = -1;

    public MenuSelector() {
        try {
            SelectorImage = new UIImage("Resource/Image/NoImage.png", -1, -1, false);
        } catch (SlickException ex) {
            Logger.getLogger(MenuSelector.class.getName()).log(Level.SEVERE, null, ex);
        }

        SelectionX = 0;
        SelectionY = 0;
        SelectionZ = 0;
    }

    public MenuSelector(String directory, float X, float Y, boolean visible, int startlocationx, int startlocationy, int startlocationz) {

        try {
            SelectorImage = new UIImage(directory, X, Y, visible);
        } catch (SlickException ex) {
            Logger.getLogger(MenuSelector.class.getName()).log(Level.SEVERE, null, ex);
        }

        SelectionX = startlocationx;
        SelectionY = startlocationy;
        SelectionZ = startlocationz;

        SelectorImage.setAlpha(1);

        
    }
    
    public MenuSelector(String filepath, int selectionX, int selectionY, float LocationX, float LocationY, boolean visible) 
    {

        try {
            SelectorImage = new UIImage(filepath, LocationX, LocationY, visible);
        } catch (SlickException ex) {
            Logger.getLogger(MenuSelector.class.getName()).log(Level.SEVERE, null, ex);
        }

        SelectionX = selectionX;
        SelectionY = selectionY;

        SelectorImage.setAlpha(1);

        
    }

    public MenuSelector(String SpriteSheetPath, float X, float Y, boolean Visible,
            int startlocationx, int startlocationy, int startlocationz,
            int duration, int framewidth, int frameheight, int spacing, int margin, boolean Animating)
   {
        try {
            SelectorImage = new UIImage(SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating);
        } catch (SlickException ex) {
            Logger.getLogger(MenuSelector.class.getName()).log(Level.SEVERE, null, ex);
        }
        SelectionX = startlocationx;
        SelectionY = startlocationy;
        SelectionZ = startlocationz;


   }

    public void draw() {
        SelectorImage.draw();
    }

    public void Update(int delta) {
        SelectorImage.Update(delta);
    }

    public void SetLocation(Point Location) {
        //Move the image to this position
        SelectorImage.setX(Location.x);
        SelectorImage.setY(Location.y);
    }

    public void setAlpha(float value) {
        SelectorImage.setAlpha(value);
    }

    public void MoveSelectDown() {
        SelectionX++;
    }

    public void MoveSelectUp() {
        SelectionX--;
    }

    public int getSelectionX() {
        return SelectionX;
    }

    public int getSelectionY() {
        return SelectionY;
    }

    public int getSelectionZ() {
        return SelectionZ;
    }
    public void fadeIn() {
        SelectorImage.fadeIn();
    }

    public void fadeOut() {
        SelectorImage.fadeOut();
    }

    public void Vanish() {
        SelectorImage.Vanish();
    }
    
    /*
     * Sets the animation frame to 0
     */
    public void ResetAnimation()
    {
        SelectorImage.ResetAnimation();
    }
    
        public void setLocationX(float x)
    {
        SelectorImage.setX(x);
    }
    
    public void setLocationY(float y)
    {
        SelectorImage.setY(y);
    }
}
