package MegaEngine.ObjectAI;

import MegaEngine.AnimationComposite;
import MegaEngine.GameObject;
import MegaEngine.Global;
import MegaEngine.PlayerCharacter;
import MegaEngine.main;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * A UI Object that shouldn't be spawned like a regular one!
 * This will be individually managed in the LevelState itself.
 * You should only have one of this Object at any time.
 * @author McRib
 */
public class PlayerHealthBar extends GameObject {
        
    private float HealthPercent = 0;
    
    public PlayerHealthBar(int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, boolean respawnable, boolean respawned, int objectarrayid, float inertiax, float intertiay, String customtext)
    {
// <editor-fold defaultstate="collapsed" desc="Basic Setup">
        //Location
        LocationX = locationx;
        LocationY = locationy;
        CurrentTileX = LocationXToCurrentTileX();
        CurrentTileY = LocationYToCurrentTileY();
        
        //Movement
        VelocityEnabled = false;
        GravityEnabled = false;
        HorizontalSpeed = 0f;
        VerticalSpeed = 0f;
        InertiaX = 0f;
        InertiaY = 0f;
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = false;
        EnemyCollisionEnabled = false;
        ObjectCollisionEnabled = false;
        BulletCollisionEnabled = false;
        ItemCollisionEnabled = false;
        TileCollisionEnabled = false;
        IsLeftWall = false;
        IsRightWall = false;
        IsPlatform = false;
        IsCeiling = false;
        
        //Sprite Drawing
        IsVisible = true;
        IsAnimating = false;
        DrawHitBox = false;
        AnimationLock = false;
        DirectionLock = false;
        DirectionHorizontal = Global.Directions.None;
        DirectionVertical = Global.Directions.None;
        CurrentAnimation = 0;
        PreviousAnimation = 0;
        
        //Despawn Rules
        IsAlive = true;
        IsOverWritable = false;
        DespawnOffScreen = false;
        ScreenLeashX = 99999;
        ScreenLeashY = 99999;
        DespawnOnRoomChange = false;
        DeleteOnPause = false;
        Respawnable = false;
        Respawned = false;
        
        //Stats and Values
        Name = "Health Monitor";
        OwnerName = "Player Character";
        IsPlayerOwned = true;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = 0;
        Health = 1;
        HealthMax = 1;
        IsDying = false;
        IsFriendly = false;
        IsHostile = false;
        Invulnerable = true;
        DamageOnTouch = false;
        DamageOnTouchValue = 0;
        IsPlayerActivatorEnabled = false;
        CutSceneScriptFileName = "";
// </editor-fold>
           
    }
    
    /**
     * Create every single animation, frame, layer, hitbox, and so much crap.
     * You can use a white outline for each color to layer a composite image.
     * This will allow you to change colors on the fly per layer.
     * Or, be lazy, and just grab a flat image sprite sheet and have the colors already there.
     */
    @Override
    protected void GenerateAnimationsAndHitboxes()
    {
        //Total animations this Entity will have. It's whatever the array size needs to be.
        AnimationCount = 4;
        //The sprite animations list
        EntityAnimation = new AnimationComposite[AnimationCount];
        //The Hitboxes to match with each sprite in the list
        HitBoxWidth = new int[AnimationCount];
        HitBoxHeight = new int[AnimationCount];
        HitBoxOffsetX = new float[AnimationCount];
        HitBoxOffsetY = new float[AnimationCount];
        MinimumYTilesToCheck = new int[AnimationCount];
        MinimumXTilesToCheck = new int[AnimationCount];
        
// <editor-fold defaultstate="collapsed" desc="Animation0 OK">
        EntityAnimation[0] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(1, 0, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 1, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 2, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 3, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 4, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 5, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 6, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 7, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 8, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 9, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 10, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 11, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 12, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 13, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 14, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 15, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 16, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 17, 100, 0, 0);
        EntityAnimation[0].AddFrame(1, 18, 1000, 0, 0);

        HitBoxWidth[0] = 32;
        HitBoxHeight[0] = 32;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation1 Caution">
        EntityAnimation[1] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[1].AddFrame(2, 0, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 1, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 2, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 3, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 4, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 5, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 6, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 7, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 8, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 9, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 10, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 11, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 12, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 13, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 14, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 15, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 16, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 17, 40, 0, 0);
        EntityAnimation[1].AddFrame(2, 18, 300, 0, 0);

        HitBoxWidth[1] = 32;
        HitBoxHeight[1] = 32;
        HitBoxOffsetX[1] = 0;
        HitBoxOffsetY[1] = 0;
        SetMinimumTilesForHitBox(1);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation2 Danger">
        EntityAnimation[2] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[2].AddFrame(3, 0, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 1, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 2, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 3, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 4, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 5, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 6, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 7, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 8, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 9, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 10, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 11, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 12, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 13, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 14, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 15, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 16, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 17, 20, 0, 0);
        EntityAnimation[2].AddFrame(3, 18, 20, 0, 0);

        HitBoxWidth[2] = 32;
        HitBoxHeight[2] = 32;
        HitBoxOffsetX[2] = 0;
        HitBoxOffsetY[2] = 0;
        SetMinimumTilesForHitBox(2);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation3 No Pulse">
        EntityAnimation[3] = new AnimationComposite(4, 1, 32, 32, 13, false, false);
        EntityAnimation[3].AddFrame(4, 0, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 1, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 2, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 3, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 4, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 5, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 6, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 7, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 8, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 9, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 10, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 11, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 12, 100, 0, 0);
        EntityAnimation[3].AddFrame(4, 13, 100, 0, 0);

        HitBoxWidth[3] = 32;
        HitBoxHeight[3] = 32;
        HitBoxOffsetX[3] = 0;
        HitBoxOffsetY[3] = 0;
        SetMinimumTilesForHitBox(3);
// </editor-fold>
    }
    
    /**
     * The first step in the Update() process which determines what the Entity
     * wants to do this frame based on input and other current states of the game.
     * @param delta
     * @param TileData
     * @param RoomData
     */
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        
        HealthPercent = (Player.GetHealth() / Player.GetHealthMax()) * 100;
    }
    
    /**
     * The second step in the Update() process which determines how far the
     * Entity wants to move this frame, based on the Status that was just computed.
     */
    @Override
    protected void ComputeVelocity() {
        
        VelocityX = 0;
        VelocityY = 0;
        
    }
    
    /*
     * Goes through the keyboard for direction to look
     * and then any statuses that may apply (ladder, falling, damage, shoot)
     * Once the final animation is figured out, it's hitbox is used for the
     * rest of the frame.
     */
    @Override
    public void GetCurrentAnimation()
    {
        frameanim = 0;
        anim = false;
        
        if (HealthPercent > 75) {
            frameanim = 0;
            anim = true;
        } else if (HealthPercent > 50) {
            frameanim = 1;
            anim = true;
        } else if (HealthPercent > 0) {
            frameanim = 2;
            anim = true;
        } else if (HealthPercent <= 0) {
            frameanim = 3;
            anim = true;
        }
        //Set the finally determined animation and state
        ChangeCurrentAnimation(frameanim, anim);
    }
    
    /**
     * This UI object will always be static on the screen. The LocationXY will
     * be where it is placed on the screen, rather than within the level.
     * @param g
     * @param ScreenX
     * @param ScreenY 
     */
    @Override
    public void Draw(Graphics g, float ScreenX, float ScreenY) 
    {
        if (IsAlive && IsVisible)
        {
            //Determine where the Entity is in relation to the Top-Left of the Screen.
            ScreenLocationX = (ScreenX + LocationX) - main.Global.TileWidth - HitBoxOffsetX[CurrentAnimation];
            ScreenLocationY = (ScreenY + LocationY) - main.Global.TileHeight - HitBoxOffsetY[CurrentAnimation];

            //Entity must be on screen in order to be drawn.  Minimum of one tile of forgiveness
            if (ScreenLocationX > -ScreenLeashX - main.Global.TileWidth && ScreenLocationX < main.WindowWidth + ScreenLeashX && ScreenLocationY > -ScreenLeashY && ScreenLocationY < main.WindowHeight + ScreenLeashY)
            {
                OnScreen = true;
                EntityAnimation[CurrentAnimation].Draw(g, SGLRenderer, ScreenLocationX, ScreenLocationY);
            }
            else
            {
                OnScreen = false;
            }
            if (DrawHitBox || main.Global.DrawAllHitboxes)
            {
                main.Global.endUse();
                g.setColor(new Color(255,0,0));
                g.drawRect(ScreenLocationX + HitBoxOffsetX[CurrentAnimation], ScreenLocationY + HitBoxOffsetY[CurrentAnimation], HitBoxWidth[CurrentAnimation], HitBoxHeight[CurrentAnimation]);
            }
        } else {
            System.out.println("Why am I not drawing" + IsAlive + ", " + IsVisible);
        }
        
    }
}
