/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MegaEngine;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

/**
 *
 * @author McRib
 */
public class MenuStateVictory  extends BasicGameState {

    /** The ID given to this state */
    public static final int ID = 9;
    /** The game holding this state */
    private StateBasedGame game;
    private Input GameContainerInput;//Use for keyboard polling to detect keys held between frames
    
    private Transition FadeOut = new FadeOutTransition(Color.black);
    private Transition FadeIn = new FadeInTransition(Color.black);
        
    private StaticImageManager Images;
    private MenuOptionManager MenuOptions;
    private StaticTextManager Text;

    private boolean PreRenderLoad = true;//If true, this is the first frame to be seen for the state. Goes true on Leave()
    
    private KeyBoard Keyboard;
    
    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
    }

    /**
     * Called whenever the state is entered from SwapState(), and performs a 
     * screen transition.
     * This function is called AFTER a render call! Anything that must be initialized
     * or reset before even a single frame can run must not be done here, but
     * instead put it into Initialize().
     * @param container
     * @param game
     * @throws SlickException 
     */
    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        main.Global.Music.PlayMusic(1);
        FadeOut = new FadeOutTransition(Color.black);
        main.ClearFrames(true);
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        FadeIn = new FadeInTransition(Color.black);
        PreRenderLoad = true;
    }

    /**
     * Called whenever the state needs to be set up, either by first time creation
     * or when the state is entered into.
     * This will be called BEFORE enter(), and as such will need to be the place
     * where most initialization of variables takes place. It will be called on the
     * first frame of render().
     * @throws SlickException 
     */
    private void Initialize() throws SlickException {
        
        Keyboard = main.Global.getKeyboard();
        GameContainerInput = main.Container.getInput();//Transfer the input of the whole game into this state
        Images = new StaticImageManager();
        Text = new StaticTextManager();
        
        Images.AddImage("Resource/Image/Menu/Background/Temp-MainMenuBackground.png", 0f, 0f, true);
        
        Text.AddText("Even when power is running out,", 0, 200, 1);
        Text.CenterTextLast();
        Text.AddText("I will never run out on you.", 0, 248, 1);
        Text.CenterTextLast();
        Text.AddText("Thank you for believing in me.", 0, 296, 1);
        Text.CenterTextLast();
        
        //Create the menu options for the screen. Use the manager to easily set all the rules.
        
        //To use the manager, think of all options as if they are in a 2D array[X][Y] (cause that is exactly how they are).
        //When the player navigates with the keyboard they are moving the selected option index around.
        //Keyboard Up would be IndexY -= 1;
        //Keyboard Right would be IndexX += 1;
        //The first two parameters to a new option are the index location of this option.
        //The option can be visibly anywhere on the screen and not affect how you navigate around.
        //If you want text or an image for the option that requires another call to create it.
        //Images and text are linked to an option by index, so use the same indexes when adding these things.
        MenuOptions = new MenuOptionManager(1, 1);
        MenuOptions.NewOptionButton(0, 0, "Main Menu", 1, 88, 412);
                
        MenuOptions.NewSelector("Resource/Image/NoImage.png", 0, 0, true);

    }

    private void ResetState() throws SlickException
    {
        Initialize();
    }
    
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) {
        if (PreRenderLoad) {
            try {
                ResetState();
                update(container, game, 0);//Run an update of delta 0 so the image to draw on fade in is not out of date
            } catch (SlickException ex) {  }
            PreRenderLoad = false;
        }
        
        Images.Draw(g);
        Text.Draw(g);
        MenuOptions.Draw(g);
        
    }

    /**
     * Game loop for the logic to get things moving around. All objects, entities,
     * and processing is put into here for each frame. Each object should have an
     * update function to call that uses the delta to know how much time has passed
     * since the last frame.
     * @param container
     * @param game
     * @param delta 
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) {
        
        PollCurrentKeys();//Get the current keyboard state
        main.Global.Update(delta);//Update the universal stuff for all states
        
        //Update the logic for each menu element we previously created
        Images.Update(delta);
        Text.Update(delta);
        MenuOptions.update(delta);
        
    }

    /**
     * Given a state ID, jump to a new state. This state will be paused and sit idle
     * until the game calls the state again. All values will persist until a forced
     * reset is made within the state.
     * @param ID 
     */
    private void SwapState(int ID) {
        game.enterState(ID, FadeOut, FadeIn);
    }
   
    /**
     * Do a one time check on the current keyboard state to see what keys 
     * are held down for this frame.
     * This is how to determine if a key is held down for multiple frames.
     * You must call this function at the start of every update frame in order
     * to properly track keyboard state!
     * 
     * This is useful for when entering a state, so you can tell if the user
     * was holding a key while the transition occurred (so you can ignore that button).
     * 
     * Example: Player hits pause, you go to pause state. First frame in pause 
     * state detects the player holding the pause key, and instantly jumps out
     * of the state. This function would tell you that the button was already held
     * when you go into the new state, so you can ignore that input.
     */
    public void PollCurrentKeys()
    {
        //This menu doesn't care about this, since we only listen on Key Press.
        GameContainerInput.poll(main.WindowWidth, main.WindowHeight);//Polling detects the state of the keys, controllers, and mouse location and state too!
        
        if (GameContainerInput.isKeyDown(Keyboard.Player1Up)) {
            //Do something related to this key being held down for a frame
            //Need to denote the difference between pressed and held.
            //So, menu options would be stupid here, but player movement is great.
            //Check if the player move button is down for this function. If so, 
            //Keyboard.Player1UpHeld += delta;
        } else {
            //Do something related to this key not beind held down for a frame
            //Keyboard.Player1UpHeld = 0;
        }
        
    }
        
    /**
     * Whenever a key is pressed down, this will trigger once.
     * It only triggers once, even if the player holds the button.
     * There is no trigger for when the key is released.
     * 
     * KeyPressed is good for navigation menu options that don't need to care
     * about key pressed being repeated by holding down.
     * @param key
     * @param c 
     */
    @Override
    public void keyPressed(int key, char c) 
    {
        if (key == Keyboard.Player1Up || key == Keyboard.Player1Left) {
            MenuOptions.MoveSelector(Global.Directions.Up);
        } else if (key == Keyboard.Player1Down || key == Keyboard.Player1Right) {
            MenuOptions.MoveSelector(Global.Directions.Down);
        } else if (key == Keyboard.Player1Pause || key == Keyboard.Player1Shoot)
        {
            //Activated an option
            String activation = MenuOptions.SelectedKeyString();
            if (activation.equals("Main Menu"))
            {
                SwapState(MenuStateMain.ID);
            }
        }
    }
    
    /**
     * Whenever a key is released, this will trigger.
     * It does not trigger on key down or hold, only when you let go of the button.
     * Useful for when holding a key down for a time period needs to be tracked.
     * @param key
     * @param c 
     */
    @Override
    public void keyReleased(int key, char c)
    {
        //In the case of a main menu, we don't need this.
    }
    
    /**
     * Similar to KeyPressed, this tracks input on game pad controllers.
     * It will call the keyPressed function with the correctly mapped input.
     * Since controllers don't have keyboards, they can't send a character value.
     * This means it will always send a default value for that, so don't expect
     * all user input types to be capable of the same things.
     * @param controller Int value for the unique controller (who did it)
     * @param button Int value to represent the button pressed (what they did)
     */
    @Override
    public void controllerButtonPressed(int controller, int button)
    {
        //Since many different controllers use this function, determine
        //which player is calling.
        if (controller == Keyboard.Player1ControllerNumber) 
        {
            if (button == Keyboard.Player1CUp) {
                keyPressed(Keyboard.Player1Up, '^');
            } else if (button == Keyboard.Player1CDown) {
                keyPressed(Keyboard.Player1Down, '^');
            } else if (button == Keyboard.Player1CLeft) {
                keyPressed(Keyboard.Player1Left, '^');
            } else if (button == Keyboard.Player1CRight) {
                keyPressed(Keyboard.Player1Right, '^');
            } else if (button == Keyboard.Player1CJump) {
                keyPressed(Keyboard.Player1Jump, '^');
            } else if (button == Keyboard.Player1CShoot) {
                keyPressed(Keyboard.Player1Shoot, '^');
            } else if (button == Keyboard.Player1CPause) {
                keyPressed(Keyboard.Player1Pause, '^');
            } else if (button == Keyboard.Player1CMenu) {
                keyPressed(Keyboard.Player1Menu, '^');
            }
        } else if (controller == Keyboard.Player2ControllerNumber) {
            if (button == Keyboard.Player2CUp) {
                keyPressed(Keyboard.Player2Up, '^');
            } else if (button == Keyboard.Player2CDown) {
                keyPressed(Keyboard.Player2Down, '^');
            } else if (button == Keyboard.Player2CLeft) {
                keyPressed(Keyboard.Player2Left, '^');
            } else if (button == Keyboard.Player2CRight) {
                keyPressed(Keyboard.Player2Right, '^');
            } else if (button == Keyboard.Player2CJump) {
                keyPressed(Keyboard.Player2Jump, '^');
            } else if (button == Keyboard.Player2CShoot) {
                keyPressed(Keyboard.Player2Shoot, '^');
            } else if (button == Keyboard.Player2CPause) {
                keyPressed(Keyboard.Player2Pause, '^');
            } else if (button == Keyboard.Player2CMenu) {
                keyPressed(Keyboard.Player2Menu, '^');
            }
        }
    }
    
    /**
     * Similar to KeyReleased, this detects when a game pad controller button
     * is released.
     * This will map back to the keyReleased function, but it will not have
     * a character code to return since controllers don't have keyboards.
     * @param controller
     * @param button 
     */
    @Override
    public void controllerButtonReleased(int controller, int button)
    {
        //Since many different controllers use this function, determine
        //which player is calling.
        if (controller == Keyboard.Player1ControllerNumber) 
        {
            if (button == Keyboard.Player1CUp) {
                keyReleased(Keyboard.Player1Up, '^');
            } else if (button == Keyboard.Player1CDown) {
                keyReleased(Keyboard.Player1Down, '^');
            } else if (button == Keyboard.Player1CLeft) {
                keyReleased(Keyboard.Player1Left, '^');
            } else if (button == Keyboard.Player1CRight) {
                keyReleased(Keyboard.Player1Right, '^');
            } else if (button == Keyboard.Player1CJump) {
                keyReleased(Keyboard.Player1Jump, '^');
            } else if (button == Keyboard.Player1CShoot) {
                keyReleased(Keyboard.Player1Shoot, '^');
            } else if (button == Keyboard.Player1CPause) {
                keyReleased(Keyboard.Player1Pause, '^');
            } else if (button == Keyboard.Player1CMenu) {
                keyReleased(Keyboard.Player1Menu, '^');
            }
        } else if (controller == Keyboard.Player2ControllerNumber) {
            if (button == Keyboard.Player2CUp) {
                keyReleased(Keyboard.Player2Up, '^');
            } else if (button == Keyboard.Player2CDown) {
                keyReleased(Keyboard.Player2Down, '^');
            } else if (button == Keyboard.Player2CLeft) {
                keyReleased(Keyboard.Player2Left, '^');
            } else if (button == Keyboard.Player2CRight) {
                keyReleased(Keyboard.Player2Right, '^');
            } else if (button == Keyboard.Player2CJump) {
                keyReleased(Keyboard.Player2Jump, '^');
            } else if (button == Keyboard.Player2CShoot) {
                keyReleased(Keyboard.Player2Shoot, '^');
            } else if (button == Keyboard.Player2CPause) {
                keyReleased(Keyboard.Player2Pause, '^');
            } else if (button == Keyboard.Player2CMenu) {
                keyReleased(Keyboard.Player2Menu, '^');
            }
        }
    }
}