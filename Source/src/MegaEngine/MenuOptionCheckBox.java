package MegaEngine;

public class MenuOptionCheckBox extends MenuOption
{
    
    public MenuOptionCheckBox(String keystring, boolean keyint, float SelectX, float SelectY)
    {
        KeyString = keystring;
        if (keyint)
        {
            KeyInt = 1;
            isActivated = true;
        }
        else
        {
            KeyInt = 0;
            isActivated = false;
        }
        
        SelectorX = SelectX;
        SelectorY = SelectY;
        OptionExists = true;
        OptionImage = new UIImage[0];
        OptionText = new MenuText[0];
        isMultiOption = false;
        
    }

    /*
     * Toggle the Activation status boolean of this option.
     */
    @Override
    public void Activate()
    {
        if (isActivated)
        {
            isActivated = false;
            KeyInt = 0;
        }
        else
        {
            isActivated = true;
            KeyInt = 1;
        }
            
    }

}
