package MegaEngine.EnemyAI;

import MegaEngine.Entity;
import MegaEngine.Enemy;
import MegaEngine.AnimationComposite;
import MegaEngine.Bullet;
import MegaEngine.Entity;
import MegaEngine.GameObject;
import MegaEngine.Global.Directions;
import MegaEngine.PlayerCharacter;
import MegaEngine.Timer;
import MegaEngine.main;

public class EnemyGeneric extends Enemy 
{
    private Timer DirectionSwitchTimer;
    
    public EnemyGeneric(int aitype, Directions horzfacing, Directions vertfacing, float locationx, float locationy)
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
        HorizontalSpeed = 90f;
        VerticalSpeed = 90f;
        InertiaX = 0f;
        InertiaY = 0f;
        HasChaseEnabled = true;        
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = true;
        EnemyCollisionEnabled = true;
        ObjectCollisionEnabled = true;
        BulletCollisionEnabled = true;
        ItemCollisionEnabled = false;
        TileCollisionEnabled = true;
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
        DirectionHorizontal = horzfacing;
        DirectionVertical = vertfacing;
        CurrentAnimation = 0;
        PreviousAnimation = 0;
        
        //Despawn Rules
        IsAlive = true;
        IsOverWritable = false;
        DespawnOffScreen = true;
        ScreenLeashX = 99999;
        ScreenLeashY = 99999;
        DespawnOnRoomChange = true;
        DeleteOnPause = false;
        Respawnable = true;
        Respawned = false;
        
        //Stats and Values
        Name = "Generic Enemy";
        OwnerName = "None";
        IsPlayerOwned = false;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = aitype;
        Health = 1;
        HealthMax = 1;
        IsDying = false;
        IsFriendly = false;
        IsHostile = true;
        Invulnerable = false;
        DamageOnTouch = true;
        DamageOnTouchValue = 9;
        IsPlayerActivatorEnabled = false;
// </editor-fold>
        
        DirectionSwitchTimer = new Timer("Go left or right", 1000, true, true);
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
        AnimationCount = 2;
        //The sprite animations list
        EntityAnimation = new AnimationComposite[AnimationCount];
        //The Hitboxes to match with each sprite in the list
        HitBoxWidth = new int[AnimationCount];
        HitBoxHeight = new int[AnimationCount];
        HitBoxOffsetX = new float[AnimationCount];
        HitBoxOffsetY = new float[AnimationCount];
        MinimumYTilesToCheck = new int[AnimationCount];
        MinimumXTilesToCheck = new int[AnimationCount];

// <editor-fold defaultstate="collapsed" desc="Animation0 Enemy Left">
        EntityAnimation[0] = new AnimationComposite(3, 1, 48, 48, 0, false, false);
        EntityAnimation[0].AddFrame(2, 5, 100, 0, 0);
        EntityAnimation[0].AddFrame(2, 6, 100, 0, 0);
        EntityAnimation[0].AddFrame(2, 7, 100, 0, 0);

        HitBoxWidth[0] = 36;
        HitBoxHeight[0] = 36;
        HitBoxOffsetX[0] = 0;
        HitBoxOffsetY[0] = 0;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation1 Enemy Right">
        EntityAnimation[1] = new AnimationComposite(3, 1, 48, 48, 0, true, false);
        EntityAnimation[1].AddFrame(2, 5, 100, -12, 0);
        EntityAnimation[1].AddFrame(2, 6, 100, -12, 0);
        EntityAnimation[1].AddFrame(2, 7, 100, -12, 0);

        HitBoxWidth[1] = 36;
        HitBoxHeight[1] = 36;
        HitBoxOffsetX[1] = 0;
        HitBoxOffsetY[1] = 0;
        SetMinimumTilesForHitBox(1);
// </editor-fold>

    }
       
    @Override
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        if(HasChaseEnabled) {
//            System.out.println("My location, x:"+LocationX+", y:"+LocationY);
//            System.out.println("Chase player at x:"+PlayerX+", y:"+PlayerY);
            if((LocationX - Player.GetLocationX()) > 2) { // we're above the player, go down
                DirectionHorizontal = Directions.Left;
            } else if ((LocationX - Player.GetLocationX()) < -2) {
                DirectionHorizontal = Directions.Right;
            } else if ((LocationX - Player.GetLocationX()) >= -2 && (LocationX - Player.GetLocationX()) <= 2) {
                DirectionHorizontal = Directions.None;
            }
            if((LocationY - Player.GetLocationY()) > 2) { // we're above the player, go down
                DirectionVertical = Directions.Up;
            } else if ((LocationY - Player.GetLocationY()) < -2) {
                DirectionVertical = Directions.Down;
            } else if ((LocationY - Player.GetLocationY()) >= -2 && (LocationY - Player.GetLocationY()) <= 2) {
                DirectionVertical = Directions.None;
            }
            
        } else {
            //This little bugger will switch directions every couple seconds.
            if (DirectionSwitchTimer.Update(delta)) {
                if (DirectionHorizontal == Directions.Left) {
                    DirectionHorizontal = Directions.Right;
                } else {
                    DirectionHorizontal = Directions.Left;
                }
            }
        }
       
        
    }
    
    @Override
    protected void ComputeVelocity()
    {
        VelocityX = 0;
        VelocityY = 0;

        if (DirectionHorizontal != Directions.None) {
            if (DirectionHorizontal == Directions.Left) {
                VelocityX -= HorizontalSpeed;
            } else {
                VelocityX += HorizontalSpeed;
            }
        }
        
        if (DirectionVertical != Directions.None) {
            if (DirectionVertical == Directions.Up) {
                VelocityY -= VerticalSpeed;
            } else {
                VelocityY += VerticalSpeed;
            }
        }
        
        if(DirectionVertical == Directions.None && DirectionHorizontal == Directions.None) {
            AnimationLock = true;
        } else {
            AnimationLock = false;
        }
        
        
    }
    
    @Override
    public void GetCurrentAnimation()
    {
        if (!AnimationLock)
        {
            frameanim = 0;
            anim = true;

            //If looking right
            if (DirectionHorizontal == Directions.Right)
            {
                frameanim = 1;
            }
            else if (DirectionHorizontal == Directions.Left)
            {
                frameanim = 0;
            }
        }
        
        ChangeCurrentAnimation(frameanim, anim); //Set the finally determined animation and state
    }
    
    @Override
    public void ProcessCollision(Bullet Bullet)
    {
        if (Bullet.IsFriendly())
        {
            if (Bullet.DamageOnTouch() && !IsInHitStun)
            {
                TriggerHitStun();
                SubtractHealth(Bullet.DamageOnTouchValue());
            }                
        }
    }
    
    @Override
    public void ProcessCollision(GameObject object) {
        //System.out.println("Enemy hit something.... :/");
    }
}