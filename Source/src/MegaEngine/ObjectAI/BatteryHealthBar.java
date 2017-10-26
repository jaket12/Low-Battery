package MegaEngine.ObjectAI;

import MegaEngine.AnimationComposite;
import MegaEngine.GameObject;
import MegaEngine.Global;
import MegaEngine.LevelState;
import MegaEngine.PlayerCharacter;
import MegaEngine.Timer;
import MegaEngine.main;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * A UI Object that shouldn't be spawned like a regular one!
 * This will be individually managed in the LevelState itself.
 * You should only have one of this Object at any time.
 * @author McRib
 */
public class BatteryHealthBar extends GameObject {
        
    private float BatteryPercent;
    private Timer GameOverCountDown;
    
    public BatteryHealthBar(int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, boolean respawnable, boolean respawned, int objectarrayid, float inertiax, float intertiay, String customtext)
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
        Name = "Battery Monitor";
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
        
        GameOverCountDown = new Timer("Time to sit at 0%", 5000, true, false);
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
        AnimationCount = 11;
        //The sprite animations list
        EntityAnimation = new AnimationComposite[AnimationCount];
        //The Hitboxes to match with each sprite in the list
        HitBoxWidth = new int[AnimationCount];
        HitBoxHeight = new int[AnimationCount];
        HitBoxOffsetX = new float[AnimationCount];
        HitBoxOffsetY = new float[AnimationCount];
        MinimumYTilesToCheck = new int[AnimationCount];
        MinimumXTilesToCheck = new int[AnimationCount];
        
// <editor-fold defaultstate="collapsed" desc="Animation0 100">
        EntityAnimation[0] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(5, 0, 5000, 0, 0);

        HitBoxWidth[0] = 32;
        HitBoxHeight[0] = 16;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation1 90">
        EntityAnimation[1] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[1].AddFrame(5, 1, 5000, 0, 0);

        HitBoxWidth[1] = 32;
        HitBoxHeight[1] = 16;
        HitBoxOffsetX[1] = 0;
        HitBoxOffsetY[1] = 0;
        SetMinimumTilesForHitBox(1);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation2 80">
        EntityAnimation[2] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[2].AddFrame(5, 2, 5000, 0, 0);

        HitBoxWidth[2] = 32;
        HitBoxHeight[2] = 16;
        HitBoxOffsetX[2] = 0;
        HitBoxOffsetY[2] = 0;
        SetMinimumTilesForHitBox(2);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation3 70">
        EntityAnimation[3] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[3].AddFrame(5, 3, 5000, 0, 0);

        HitBoxWidth[3] = 32;
        HitBoxHeight[3] = 16;
        HitBoxOffsetX[3] = 0;
        HitBoxOffsetY[3] = 0;
        SetMinimumTilesForHitBox(3);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation4 60">
        EntityAnimation[4] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[4].AddFrame(5, 4, 5000, 0, 0);

        HitBoxWidth[4] = 32;
        HitBoxHeight[4] = 16;
        HitBoxOffsetX[4] = 0;
        HitBoxOffsetY[4] = 0;
        SetMinimumTilesForHitBox(4);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation5 50">
        EntityAnimation[5] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[5].AddFrame(5, 5, 5000, 0, 0);

        HitBoxWidth[5] = 32;
        HitBoxHeight[5] = 16;
        HitBoxOffsetX[5] = 0;
        HitBoxOffsetY[5] = 0;
        SetMinimumTilesForHitBox(5);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation6 40">
        EntityAnimation[6] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[6].AddFrame(5, 6, 5000, 0, 0);

        HitBoxWidth[6] = 32;
        HitBoxHeight[6] = 16;
        HitBoxOffsetX[6] = 0;
        HitBoxOffsetY[6] = 0;
        SetMinimumTilesForHitBox(6);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation7 30">
        EntityAnimation[7] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[7].AddFrame(5, 7, 5000, 0, 0);

        HitBoxWidth[7] = 32;
        HitBoxHeight[7] = 16;
        HitBoxOffsetX[7] = 0;
        HitBoxOffsetY[7] = 0;
        SetMinimumTilesForHitBox(7);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation8 20">
        EntityAnimation[8] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[8].AddFrame(5, 8, 5000, 0, 0);

        HitBoxWidth[8] = 32;
        HitBoxHeight[8] = 16;
        HitBoxOffsetX[8] = 0;
        HitBoxOffsetY[8] = 0;
        SetMinimumTilesForHitBox(8);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation9 10">
        EntityAnimation[9] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[9].AddFrame(5, 9, 5000, 0, 0);

        HitBoxWidth[9] = 32;
        HitBoxHeight[9] = 16;
        HitBoxOffsetX[9] = 0;
        HitBoxOffsetY[9] = 0;
        SetMinimumTilesForHitBox(9);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation10 0">
        EntityAnimation[10] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[10].AddFrame(5, 10, 5000, 0, 0);

        HitBoxWidth[10] = 32;
        HitBoxHeight[10] = 16;
        HitBoxOffsetX[10] = 0;
        HitBoxOffsetY[10] = 0;
        SetMinimumTilesForHitBox(10);
// </editor-fold>
    }
    
    /**
     * The first step in the Update() process which determines what the Entity
     * wants to do this frame based on input and other current states of the game.
     * @param delta
     * @param TileData
     * @param RoomData
     * @param Player
     */
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        
        BatteryPercent = (main.Global.BatteryHealth / main.Global.BatteryHealthMax) * 100;
        
        if (BatteryPercent == 0) {
            if(GameOverCountDown.Update(delta) && !IsDying) {
                IsDying = true;
                LevelState.PerformCutScene("Resource/Movie/GameOverBattery.scr", Global.CutSceneType.CustomEvent);
            }
        }
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
        
        if (BatteryPercent > 90) {
            frameanim = 0;
        } else if (BatteryPercent > 80) {
            frameanim = 1;
        } else if (BatteryPercent > 70) {
            frameanim = 2;
        } else if (BatteryPercent > 60) {
            frameanim = 3;
        } else if (BatteryPercent > 50) {
            frameanim = 4;
        } else if (BatteryPercent > 40) {
            frameanim = 5;
        } else if (BatteryPercent > 30) {
            frameanim = 6;
        } else if (BatteryPercent > 20) {
            frameanim = 7;
        } else if (BatteryPercent > 10) {
            frameanim = 8;
        } else if (BatteryPercent > 5) {
            frameanim = 9;
        } else {
            frameanim = 10;
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
            ScreenLocationX = (ScreenX + LocationX) - main.Global.TileWidth - HitBoxOffsetX[CurrentAnimation];;
            ScreenLocationY = (ScreenY + LocationY) - main.Global.TileHeight - HitBoxOffsetY[CurrentAnimation];;

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
        }
        
    }
}
