/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class SundownTimerUI extends GameObject {
        
    private float SundownPercent;
    
    public SundownTimerUI(int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, boolean respawnable, boolean respawned, int objectarrayid, float inertiax, float intertiay, String customtext)
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
        AnimationCount = 10;
        //The sprite animations list
        EntityAnimation = new AnimationComposite[AnimationCount];
        //The Hitboxes to match with each sprite in the list
        HitBoxWidth = new int[AnimationCount];
        HitBoxHeight = new int[AnimationCount];
        HitBoxOffsetX = new float[AnimationCount];
        HitBoxOffsetY = new float[AnimationCount];
        MinimumYTilesToCheck = new int[AnimationCount];
        MinimumXTilesToCheck = new int[AnimationCount];
        
// <editor-fold defaultstate="collapsed" desc="Animation0 90+">
        EntityAnimation[0] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(6, 0, 5000, 0, 0);
        EntityAnimation[0].setLayerColor(0, 1f, 0.98f, 0.21f, 1);
        EntityAnimation[0].EnableCustomColors(true);

        HitBoxWidth[0] = 32;
        HitBoxHeight[0] = 32;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation1 80+">
        EntityAnimation[1] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[1].AddFrame(6, 1, 5000, 0, 0);
        EntityAnimation[1].setLayerColor(0, 1f, 0.88f, 0.21f, 1);
        EntityAnimation[1].EnableCustomColors(true);

        HitBoxWidth[1] = 32;
        HitBoxHeight[1] = 32;
        HitBoxOffsetX[1] = 0;
        HitBoxOffsetY[1] = 0;
        SetMinimumTilesForHitBox(1);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation2 70+">
        EntityAnimation[2] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[2].AddFrame(6, 2, 5000, 0, 0);
        EntityAnimation[2].setLayerColor(0, 1f, 0.75f, 0.21f, 1);
        EntityAnimation[2].EnableCustomColors(true);

        HitBoxWidth[2] = 32;
        HitBoxHeight[2] = 32;
        HitBoxOffsetX[2] = 0;
        HitBoxOffsetY[2] = 0;
        SetMinimumTilesForHitBox(2);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation3 60+">
        EntityAnimation[3] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[3].AddFrame(6, 3, 5000, 0, 0);
        EntityAnimation[3].setLayerColor(0, 1f, 0.52f, 0.24f, 1);
        EntityAnimation[3].EnableCustomColors(true);

        HitBoxWidth[3] = 32;
        HitBoxHeight[3] = 32;
        HitBoxOffsetX[3] = 0;
        HitBoxOffsetY[3] = 0;
        SetMinimumTilesForHitBox(3);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation4 50+">
        EntityAnimation[4] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[4].AddFrame(6, 4, 5000, 0, 0);
        EntityAnimation[4].setLayerColor(0, 0.99f, 0.25f, 0.25f, 1);
        EntityAnimation[4].EnableCustomColors(true);

        HitBoxWidth[4] = 32;
        HitBoxHeight[4] = 32;
        HitBoxOffsetX[4] = 0;
        HitBoxOffsetY[4] = 0;
        SetMinimumTilesForHitBox(4);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation0 40+">
        EntityAnimation[5] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[5].AddFrame(6, 5, 5000, 0, 0);
        EntityAnimation[5].setLayerColor(0, 0.80f, 0.15f, 0.05f, 1);
        EntityAnimation[5].EnableCustomColors(true);

        HitBoxWidth[5] = 32;
        HitBoxHeight[5] = 32;
        HitBoxOffsetX[5] = 0;
        HitBoxOffsetY[5] = 0;
        SetMinimumTilesForHitBox(5);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation0 30+">
        EntityAnimation[6] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[6].AddFrame(6, 6, 5000, 0, 0);
        EntityAnimation[6].setLayerColor(0, 0.77f, 0.07f, 0.07f, 1);
        EntityAnimation[6].EnableCustomColors(true);

        HitBoxWidth[6] = 32;
        HitBoxHeight[6] = 32;
        HitBoxOffsetX[6] = 0;
        HitBoxOffsetY[6] = 0;
        SetMinimumTilesForHitBox(6);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation0 20+">
        EntityAnimation[7] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[7].AddFrame(6, 7, 5000, 0, 0);
        EntityAnimation[7].setLayerColor(0, 0.77f, 0.07f, 0.07f, 1);
        EntityAnimation[7].EnableCustomColors(true);

        HitBoxWidth[7] = 32;
        HitBoxHeight[7] = 32;
        HitBoxOffsetX[7] = 0;
        HitBoxOffsetY[7] = 0;
        SetMinimumTilesForHitBox(7);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation0 10+">
        EntityAnimation[8] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[8].AddFrame(6, 8, 5000, 0, 0);
        EntityAnimation[8].setLayerColor(0, 0.77f, 0.07f, 0.07f, 1);
        EntityAnimation[8].EnableCustomColors(true);

        HitBoxWidth[8] = 32;
        HitBoxHeight[8] = 32;
        HitBoxOffsetX[8] = 0;
        HitBoxOffsetY[8] = 0;
        SetMinimumTilesForHitBox(8);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation0 0+">
        EntityAnimation[9] = new AnimationComposite(4, 1, 32, 32, 0, false, false);
        EntityAnimation[9].AddFrame(6, 9, 5000, 0, 0);
        EntityAnimation[9].setLayerColor(0, 0, 0, 0, 0);
        EntityAnimation[9].EnableCustomColors(true);

        HitBoxWidth[9] = 32;
        HitBoxHeight[9] = 32;
        HitBoxOffsetX[9] = 0;
        HitBoxOffsetY[9] = 0;
        SetMinimumTilesForHitBox(9);
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
        
        SundownPercent = ((float)main.Global.SundownTimer.GetTimeRemaining() / main.Global.SundownTimer.getDuration()) * 100;
        if (main.Global.SundownTimer.isExpired() && !main.Global.ZombieInvasionEnabled) {
            main.Global.ZombieInvasionEnabled = true;
            LevelState.PerformCutScene("Resource/Movie/ZombieInvasion.scr", Global.CutSceneType.CustomEvent);
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
        
        if (SundownPercent > 90) {
            frameanim = 0;
        } else if (SundownPercent > 80) {
            frameanim = 1;
        } else if (SundownPercent > 70) {
            frameanim = 2;
        } else if (SundownPercent > 60) {
            frameanim = 3;
        } else if (SundownPercent > 50) {
            frameanim = 4;
        } else if (SundownPercent > 40) {
            frameanim = 5;
        } else if (SundownPercent > 30) {
            frameanim = 6;
        } else if (SundownPercent > 20) {
            frameanim = 7;
        } else if (SundownPercent > 10) {
            frameanim = 8;
        } else {
            frameanim = 9;
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
