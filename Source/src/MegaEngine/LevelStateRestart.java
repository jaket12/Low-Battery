package MegaEngine;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.state.transition.Transition;

/**
 * The whole point is for the LevelState to jump out, and then this state
 * will go right back in.
 * The Level State will render(), enter(), initialize(), and then be ready to go.
 * @author McRib
 */
public class LevelStateRestart  extends BasicGameState {

    /** The ID given to this state */
    public static final int ID = 5;
    /** The game holding this state */
    private StateBasedGame game;
    
    private Transition FadeOut = new FadeOutTransition(Color.black);
    private Transition FadeIn = new FadeInTransition(Color.black);
            
    /**
     * Returns the ID value for the screen used to reference it.
     * @return 
     */
    @Override
    public int getID() {
        return ID;
    }

    /**
     * Constructor for the screen state. This is only called once on game initialization.
     * @param container
     * @param game
     * @throws SlickException 
     */
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        this.game = game;
    }

    /**
     * Called whenever the screen is entered during game play. There is one frame
     * of the update loop that will be run -before- this function gets run!
     * This is because the fade in transition effect needs to show something 
     * on screen.
     * @param container
     * @param game
     * @throws SlickException 
     */
    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException 
    {
        FadeOut = new FadeOutTransition(Color.black);//Transitions can only be used once. Remake it once it's been used.
        main.ClearFrames(true);//This state doesn't fully cover the whole screen in drawing images,
        //so we should blank out the screen after each frame to prevent any fragments from showing.
    }

    /**
     * Called whenever the screen is exited and another screen is going to be entered.
     * @param container
     * @param game
     * @throws SlickException 
     */
    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        FadeIn = new FadeInTransition(Color.black);//Remake the transition so we can use it again if we go back to this screen.
    }
    
    /**
     * The drawing loop for the screen. Graphics g must be passed around to each
     * object that must be drawn. The render loop is separate from the update loop,
     * and may be run multiple times per frame. If a game runs at 30 fps but the refresh rate
     * is at 60, then you will get around 2 render calls per update.
     * 
     * This will be called one time whenever the enter function is run, so that
     * when the game fades in to this screen it has an image to show.
     * PreRenderLoad
     * @param container
     * @param game
     * @param g 
     */
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) {

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
    public void update(GameContainer container, StateBasedGame game, int delta) 
    {
        //Get the Global to be ready for the reset, and set them flags.
        
        
        //When done, move to the Level State we just reset.
        SwapState(LevelState.ID);
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
}
