/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MegaEngine.EnemyAI;

import MegaEngine.AnimationComposite;
import MegaEngine.Enemy;
import MegaEngine.Global;
import MegaEngine.Global.Directions;
import MegaEngine.LevelState;
import MegaEngine.PlayerCharacter;
import MegaEngine.Timer;
import MegaEngine.main;

/**
 *
 * @author McRib
 */
public class Zombie extends Enemy {
    
    public Zombie(int aitype, Global.Directions horzfacing, Global.Directions vertfacing, float locationx, float locationy, boolean solidwall, boolean deleteonactivate, int objectarrayid, float spriterow, float spritecolumn, String cutscenefilename)
    {
        
// <editor-fold defaultstate="collapsed" desc="Basic Setup">
        //Location
        LocationX = locationx;
        LocationY = locationy;
        CurrentTileX = LocationXToCurrentTileX();
        CurrentTileY = LocationYToCurrentTileY();
        
        //Movement
        VelocityEnabled = true;
        GravityEnabled = false;
        HorizontalSpeed = 60f;
        VerticalSpeed = 60f;
        InertiaX = 0f;
        InertiaY = 0f;
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = true;
        EnemyCollisionEnabled = false;
        ObjectCollisionEnabled = true;
        BulletCollisionEnabled = false;
        ItemCollisionEnabled = false;
        TileCollisionEnabled = true;
        IsLeftWall = false;
        IsRightWall = false;
        IsPlatform = false;
        IsCeiling = false;
        
        //Sprite Drawing
        IsVisible = true;
        IsAnimating = true;
        DrawHitBox = false;
        AnimationLock = false;
        DirectionLock = false;
        DirectionHorizontal = horzfacing;
        DirectionVertical = vertfacing;
        CurrentAnimation = 0;
        PreviousAnimation = 0;
        
        //Despawn Rules
        IsAlive = true;
        IsOverWritable = true;
        DespawnOffScreen = false;
        ScreenLeashX = 99999;
        ScreenLeashY = 99999;
        DespawnOnRoomChange = true;
        DeleteOnPause = false;
        Respawnable = false;
        Respawned = false;
        
        //Stats and Values
        Name = "Zombie";
        OwnerName = "None";
        IsPlayerOwned = false;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = 0;
        Health = 1;
        HealthMax = 1;
        IsDying = false;
        IsFriendly = false;
        IsHostile = true;
        Invulnerable = false;
        DamageOnTouch = true;
        DamageOnTouchValue = 20;
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
        
// <editor-fold defaultstate="collapsed" desc="Animation0 Down">
        EntityAnimation[0] = new AnimationComposite(3, 1, 48, 48, 0, false, false);
        EntityAnimation[0].AddFrame(0, 0, 200, 0, 0);
        EntityAnimation[0].AddFrame(0, 1, 200, 0, 0);
        EntityAnimation[0].AddFrame(0, 2, 200, 0, 0);
        EntityAnimation[0].AddFrame(0, 1, 200, 0, 0);

        HitBoxWidth[0] = 16;
        HitBoxHeight[0] = 16;
        HitBoxOffsetX[0] = 8;
        HitBoxOffsetY[0] = 16;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation1 Left">
        EntityAnimation[1] = new AnimationComposite(3, 1, 48, 48, 0, false, false);
        EntityAnimation[1].AddFrame(1, 0, 200, 0, 0);
        EntityAnimation[1].AddFrame(1, 1, 200, 0, 0);
        EntityAnimation[1].AddFrame(1, 2, 200, 0, 0);
        EntityAnimation[1].AddFrame(1, 1, 200, 0, 0);

        HitBoxWidth[1] = 16;
        HitBoxHeight[1] = 16;
        HitBoxOffsetX[1] = 8;
        HitBoxOffsetY[1] = 16;
        SetMinimumTilesForHitBox(1);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation2 Right">
        EntityAnimation[2] = new AnimationComposite(3, 1, 48, 48, 0, false, false);
        EntityAnimation[2].AddFrame(2, 0, 200, 0, 0);
        EntityAnimation[2].AddFrame(2, 1, 200, 0, 0);
        EntityAnimation[2].AddFrame(2, 2, 200, 0, 0);
        EntityAnimation[2].AddFrame(2, 1, 200, 0, 0);

        HitBoxWidth[2] = 16;
        HitBoxHeight[2] = 16;
        HitBoxOffsetX[2] = 8;
        HitBoxOffsetY[2] = 16;
        SetMinimumTilesForHitBox(2);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation3 Up">
        EntityAnimation[3] = new AnimationComposite(3, 1, 48, 48, 0, false, false);
        EntityAnimation[3].AddFrame(3, 0, 200, 0, 0);
        EntityAnimation[3].AddFrame(3, 1, 200, 0, 0);
        EntityAnimation[3].AddFrame(3, 2, 200, 0, 0);
        EntityAnimation[3].AddFrame(3, 1, 200, 0, 0);

        HitBoxWidth[3] = 16;
        HitBoxHeight[3] = 16;
        HitBoxOffsetX[3] = 8;
        HitBoxOffsetY[3] = 16;
        SetMinimumTilesForHitBox(3);
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
        if (Player.ComputeHitBoxCenterX() > ComputeHitBoxCenterX()) {
            DirectionHorizontal = Directions.Right;
        } else {
            DirectionHorizontal = Directions.Left;
        }
        
        if (Player.ComputeHitBoxCenterY() > ComputeHitBoxCenterY()) {
            DirectionVertical = Directions.Down;
        } else {
            DirectionVertical = Directions.Up;
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
        
        if (DirectionHorizontal == Directions.Left) {
            VelocityX -= HorizontalSpeed;
        } else if (DirectionHorizontal == Directions.Right) {
            VelocityX += HorizontalSpeed;
        }
        
        if (DirectionVertical == Directions.Up) {
            VelocityY -= VerticalSpeed;
        } else if (DirectionVertical == Directions.Down) {
            VelocityY += VerticalSpeed;
        }
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
        anim = true;
        
        if (DirectionHorizontal == Directions.Left) {
            frameanim = 1;
        } else if (DirectionHorizontal == Directions.Right) {
            frameanim = 2;
        }
        
        if (DirectionVertical == Directions.Up) {
            frameanim = 3;
        } else if (DirectionVertical == Directions.Down) {
            frameanim = 0;
        }
        
        //Set the finally determined animation and state
        ChangeCurrentAnimation(frameanim, anim);
    }
}