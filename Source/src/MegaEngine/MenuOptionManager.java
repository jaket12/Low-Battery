package MegaEngine;

import org.newdawn.slick.*;
import MegaEngine.Global.Directions;

/*
 * A container class which holds and controls any number of MenuOptions.
 * 
 * The options are accessed by the user through a MenuSelector, which may
 * scan across the options in a 1 or 2 dimensional motion.
 */
public class MenuOptionManager {

    private MenuOption[][] MenuOptions;
    private int OptionArraySizeX = 0;
    private int OptionArraySizeY = 0;
    private int iX = 0;
    private int iY = 0;
    //Array which contains all options to be used.
    //There are two dimensions to the array which may be used.
    //The first dimension is X which moves about Horizontally,
    //and the second dimension is Y which moves Vertically.
 
    private MenuSelector Selector; //The thing that is currently selected.
    //This does not imply any options that are activated
    
    private String SelectedKeyString = "";
    private float SelectedKeyInt = -1;

    public enum ShowWhen {Selected, UnSelected, Activated, UnActivated, Always, Never};
    
    /*
     * Constructor.
     * 
     * OX and OY are dimension sizes for option array [OX][OY]
     * 
     * Initiate the manager by specifying it's final size (when first used).
     * Options do NOT need to be initialized in every array slot.
     * Actual abilities and data of the options are created after construction
     * 
     * A default Selector will be created which may be changed later.
     */
    public MenuOptionManager(int OX, int OY)
    {

        MenuOptions = new MenuOption[OX][OY];
        OptionArraySizeX = OX;
        OptionArraySizeY = OY;
        
        int i,j;
        for (j = 0; j < OptionArraySizeY; j++)
        {
            for(i = 0; i < OptionArraySizeX; i++)
            {
                MenuOptions[i][j] = new MenuOption();
            }
        }

        Selector = new MenuSelector();

    }
   
    /*
     * An an image which relates to a specified option.
     * 
     * The option which this image is attached to is based on it's array location
     * OX and OY, and the Keyword given will determine when it is displayed.
     */
    public void AddStillImageToOption(int OX, int OY, String StillImagePath, float X, float Y, boolean Visible, int Keyint)
    {
        MenuOptions[OX][OY].AddStillImage(StillImagePath, X, Y, Visible, Keyint);
    } 

    /*
     * An an image which relates to a specified option.
     * 
     * The option which this image is attached to is based on it's array location
     * OX and OY, and the Keyword given will determine when it is displayed.
     */
    public void AddAnimatedImageToOption(int OX, int OY, String SpriteSheetPath, int duration, int framewidth, int frameheight, int spacing, int margin, float X, float Y, boolean Visible, boolean Animating, int keyint)
    {
        MenuOptions[OX][OY].AddAnimatedImage(SpriteSheetPath, duration, framewidth, frameheight, spacing, margin, X, Y, Visible, Animating, keyint);
    }
    
    public void MoveSelectorDown()
    {
            //Current position: MenuOptions[Selector.SelectionX][Selector.SelectionY][Selector.SelectionZ]

        boolean TargetOptionFound = false;
        int MoveX = Selector.SelectionX + 1;
        int MoveY = Selector.SelectionY;

        while (!TargetOptionFound)
        {
            if (MoveX < MenuOptions.length)
            {
                if (MenuOptions[MoveX][MoveY].OptionExists())
                {//Valid option was found.

                    //Unselect the previous option
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(false);

                    //Move the Selector and select the new option
                    Selector.SelectionX = MoveX;
                    Selector.SelectionY = MoveY;
                    Selector.setLocationX(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationX());
                    Selector.setLocationY(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationY());
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(true); //New location after move

                    TargetOptionFound = true;
                }
                else
                {//Check the next option
                    MoveX++;
                }
            }
            else
            {//X hit end of array. Check the next column of options for valid positions
                if (MoveY < MenuOptions[0].length - 1)
                {
                    MoveX = 0;
                    MoveY++;
                }
                else
                {//Hit the end of the MenuOption array.  Restart at 0,0.
                    MoveX = 0;
                    MoveY = 0;
                }

            }
        }
    }

    /*
     * Move the logical position of the Selector up one position.
     * 
     * If the option in this location does not exist, then the selector will
     * move until a valid position is found.
     */
    public void MoveSelectorUp() {
        //Current position: MenuOptions[Selector.SelectionX][Selector.SelectionY][Selector.SelectionZ]

        boolean TargetOptionFound = false;
        int MoveX = Selector.SelectionX - 1;
        int MoveY = Selector.SelectionY;

        while (!TargetOptionFound)
        {
            if (MoveX > -1)
            { //There is room to move backwards
                if (MenuOptions[MoveX][MoveY].OptionExists())
                {//We can move to this option
                    //Unselect the previous option
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(false);

                    //Move the Selector and select the new option
                    Selector.SelectionX = MoveX;
                    Selector.SelectionY = MoveY;
                    Selector.setLocationX(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationX());
                    Selector.setLocationY(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationY());
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(true); //New location after move
                    TargetOptionFound = true;
                } else
                {//Check the next option
                    MoveX--;
                }
            } else
            {//X hit the start of array. Move backwards one column and check again.
                if (MoveY > 0)
                {
                    MoveX = MenuOptions.length - 1;
                    MoveY--;
                } else
                {//X hit the beginning of the array.  Restart at the very end.
                    MoveX = MenuOptions.length - 1;
                    MoveY = MenuOptions[0].length - 1;
                }

            }
        }
    }

    /*
     * Move the logical position of the Selector left one position.
     * 
     * If the option in this location does not exist, then the selector will
     * move until a valid position is found.
     */
    public void MoveSelectorLeft()
    {
        boolean TargetOptionFound = false;
        int MoveX = Selector.SelectionX;
        int MoveY = Selector.SelectionY - 1;

        while (!TargetOptionFound)
        {
            if (MoveY > -1)
            { //There is room to move backwards
                if (MenuOptions[MoveX][MoveY].OptionExists())
                {//We can move to this option
                    //Unselect the previous option
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(false);

                    //Move the Selector and select the new option
                    Selector.SelectionX = MoveX;
                    Selector.SelectionY = MoveY;
                    Selector.setLocationX(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationX());
                    Selector.setLocationY(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationY());
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(true); //New location after move
                    TargetOptionFound = true;
                } else
                {//Check the next option
                    MoveY--;
                }
            } else
            {//Y hit end of array.
                // If X can be --, Set Y to length, and X--
                //If not, then X Y = length
                if (MoveX > 0)
                {
                    MoveY = MenuOptions[0].length - 1;
                    MoveX--;
                } else
                {
                    MoveY = MenuOptions[0].length - 1;
                    MoveX = MenuOptions.length - 1;
                }

            }
        }
    }

    /*
     * Move the logical position of the Selector right one position.
     * 
     * If the option in this location does not exist, then the selector will
     * move until a valid position is found.
     */
    public void MoveSelectorRight()
    {
        boolean TargetOptionFound = false;
        int MoveX = Selector.SelectionX;
        int MoveY = Selector.SelectionY + 1;

        while (!TargetOptionFound)
        {
            if (MoveY < MenuOptions[0].length)
            { //There is room to move backwards
                if (MenuOptions[MoveX][MoveY].OptionExists())
                {//We can move to this option
                    //Unselect the previous option
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(false);

                    //Move the Selector and select the new option
                    Selector.SelectionX = MoveX;
                    Selector.SelectionY = MoveY;
                    Selector.setLocationX(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationX());
                    Selector.setLocationY(MenuOptions[Selector.SelectionX][Selector.SelectionY].getSelectorLocationY());
                    MenuOptions[Selector.SelectionX][Selector.SelectionY].Selected(true); //New location after move
                    TargetOptionFound = true;
                } else
                {//Check the next option
                    MoveY++;
                }
            } else
            {//Y hit end of array.
                // If X can be ++, Set Y to length, and X++
                //If not, then X Y = 0
                if (MoveX < MenuOptions.length - 1)
                {
                    MoveY = 0;
                    MoveX++;
                } else
                {
                    MoveY = 0;
                    MoveX = 0;
                }

            }
        }
    }

    public void Draw(Graphics g) {

        for (iX = 0; iX < OptionArraySizeX; iX++)
        {
            for (iY = 0; iY < OptionArraySizeY; iY++)
            {
                    MenuOptions[iX][iY].draw(g);
            }
        }

        Selector.draw();
    }

    public void update(int delta) 
    {
        
        for (iX = 0; iX < OptionArraySizeX; iX++)
        {
            for (iY = 0; iY < OptionArraySizeY; iY++)
            {
                    MenuOptions[iX][iY].Update(delta);
            }
        }

        Selector.Update(delta);

    }

    public void NewSelector(String filepath, int OX, int OY, boolean visible)
    {
        Selector = new MenuSelector(filepath, OX, OY, MenuOptions[OX][OY].SelectorX, MenuOptions[OX][OY].SelectorY, visible);
    }

    public void NewSelector(String SpriteSheetPath, float X, float Y, boolean Visible,
            int startlocationx, int startlocationy, int startlocationz,
            int duration, int framewidth, int frameheight, int spacing, int margin, boolean Animating)
    {
        Selector = new MenuSelector(SpriteSheetPath, X, Y, Visible, startlocationx, startlocationy, startlocationz, duration, framewidth, frameheight, spacing, margin, Animating);
    }

    public int GetSelectionX()
    {
        return Selector.getSelectionX();
    }

    public int GetSelectionY()
    {
        return Selector.getSelectionY();
    }
    
    public void CenterTextLast(int OX, int OY)
    {
        MenuOptions[OX][OY].CenterTextLast();
    }

    /**
     * Add a new option to the menu in the form of a single action button.
     * The select must be on this option, and then the player must hit enter/start
     * to activate it.
     * @param OX The X location of the option in the array for navigation
     * @param OY The Y location of the option in the array for navigation
     * @param keystring The unique string that says what this option does on activation
     * @param keyint The unique int for this value on activation
     * @param selectorx Where the Selector Symbol should be drawn on the screen when this option is hovered
     * @param selectory Where the Selector Symbol should be drawn on the screen when this option is hovered
     */
    public void NewOptionButton(int OX, int OY, String keystring, float keyint, float selectorx, float selectory)
    {
        MenuOptions[OX][OY] = new MenuOptionButton(keystring, keyint, selectorx, selectory);
    }
    
    public void AddTextToOption(int OX, int OY, String Text, float locx, float locy, Color textcolor)
    {
        MenuOptions[OX][OY].AddText(Text, locx, locy, textcolor);
    }
    
    public void MoveSelector(Directions direction)
    {
        if (direction == Directions.Up)
        {
            MoveSelectorUp();
        } else if (direction == Directions.Down)
        {
            MoveSelectorDown();
        } else if (direction == Directions.Left)
        {
            //Left or right will activate a slider option if it is selected.  Check that
            if (MenuOptions[Selector.SelectionX][Selector.SelectionY].isMultiOption())
            {
                //This option is a slider or something that changes based on left/right buttons
                MenuOptions[Selector.SelectionX][Selector.SelectionY].SubtractKeyValue();
            }
            else
            {//Typical option with nothing special
                MoveSelectorLeft();
            }
        } else if (direction == Directions.Right)
        {
            //Left or right will activate a slider option if it is selected.  Check that
            if (MenuOptions[Selector.SelectionX][Selector.SelectionY].isMultiOption())
            {
                //This option is a slider or something that changes based on left/right buttons
                MenuOptions[Selector.SelectionX][Selector.SelectionY].AddKeyValue();
            }
            else
            {//Typical option with nothing special
                MoveSelectorRight();
            }
        }
        
        SaveCurrentSelectionKeys();
    }
    
    public void ActivateSelection()
    {
        MenuOptions[Selector.SelectionX][Selector.SelectionY].Activate();
        SaveCurrentSelectionKeys();
    }
    
    private void SaveCurrentSelectionKeys()
    {
        SelectedKeyString = MenuOptions[Selector.SelectionX][Selector.SelectionY].KeyString;
        SelectedKeyInt = MenuOptions[Selector.SelectionX][Selector.SelectionY].KeyInt;
    }
    
    public String SelectedKeyString()
    {
        SelectedKeyString = MenuOptions[Selector.SelectionX][Selector.SelectionY].KeyString;
        return SelectedKeyString;
    }
    
    public float SelectedKeyInt()
    {
        SelectedKeyInt = MenuOptions[Selector.SelectionX][Selector.SelectionY].KeyInt;
        return SelectedKeyInt;
    }
    
    public void NewOptionCheckBox(int OX, int OY, String keystring, boolean keyint, float selectorX, float selectorY)
    {
        MenuOptions[OX][OY] = new MenuOptionCheckBox(keystring, keyint, selectorX, selectorY);
    }
    
    public void NewOptionSlider(int OX, int OY, String keystring, float keyint, float selectorX, float selectorY, boolean horizontal, float sliderx, float slidery)
    {
        MenuOptions[OX][OY] = new MenuOptionSlider(keystring, keyint, selectorX, selectorY, horizontal, sliderx, slidery);
    }
    
    public void NewOptionList(int OX, int OY, String keystring, float SelectX, float SelectY)
    {
        MenuOptions[OX][OY] = new MenuOptionList(keystring, SelectX, SelectY);
    }
    
    public void setActivated(int OX, int OY, boolean value)
    {
        if (MenuOptions[OX][OY].isActivated != value)
        {
            MenuOptions[OX][OY].Activate();
        }
        
    }
    
    public void ShowLastText(int OX, int OY, String value)
    {
        if (MenuOptions[OX][OY].TextArraySize > 0)
        {
            if (value.equals("Always"))
            {
                MenuOptions[OX][OY].OptionText[MenuOptions[OX][OY].TextArraySize - 1].ShowAlways(true);
            } else if (value.equals("Never"))
            {
                MenuOptions[OX][OY].OptionText[MenuOptions[OX][OY].TextArraySize - 1].ShowNever(true);
            } else if (value.equals("Selected"))
            {
                MenuOptions[OX][OY].OptionText[MenuOptions[OX][OY].TextArraySize - 1].ShowSelected(true);
            } else if (value.equals("UnSelected"))
            {
                MenuOptions[OX][OY].OptionText[MenuOptions[OX][OY].TextArraySize - 1].ShowUnSelected(true);
            } else if (value.equals("Activated"))
            {
                MenuOptions[OX][OY].OptionText[MenuOptions[OX][OY].TextArraySize - 1].ShowActivated(true);
            } else if (value.equals("UnActivated"))
            {
                MenuOptions[OX][OY].OptionText[MenuOptions[OX][OY].TextArraySize - 1].ShowUnActivated(true);
            }
            
        }
    }
    
    public boolean isSelectedMultiOption()
    {
        return MenuOptions[Selector.SelectionX][Selector.SelectionY].isMultiOption;
    }
    
    public void SelectOption(int OX, int OY)
    {
        boolean reseting = true;
        while (reseting)
        {
            System.out.print("Is " + Selector.SelectionX + "," + Selector.SelectionY + " == " + OX + "," + OY + "?");
            if (Selector.SelectionX != OX || Selector.SelectionY != OY)
            {
                MoveSelector(Directions.Down);
                System.out.println(" nope");
            }
            else
            {
                System.out.println(" yep");
                reseting = false;
            }
            
        }
        System.out.println("Selector reset to " + Selector.SelectionX + ", " + Selector.SelectionY);
    }
    
    public float getKeyInt(int OX, int OY)
    {
        return MenuOptions[OX][OY].KeyInt;
    }
}
