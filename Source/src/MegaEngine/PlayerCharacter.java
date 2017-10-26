package MegaEngine;

import MegaEngine.Global.CutSceneType;
import org.newdawn.slick.*;
import MegaEngine.Global.Directions;

public class PlayerCharacter extends Entity {
    
    private float RoomTransitionSpeedHorizontal = 48;
    private float RoomTransitionSpeedVertical = 48;
    private Directions RoomTransitionDirection = Directions.None;
    
    /**
     * The Entity which is interactable by the Player's input.
     * Requires a starting XY pixel location on the Level's Tile Map.
     * @param locationx
     * @param locationy
     * @throws SlickException 
     */
    public PlayerCharacter(float locationx, float locationy) throws SlickException
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
        HorizontalSpeed = 165f;
        VerticalSpeed = 165f;
        InertiaX = 0f;
        InertiaY = 0f;
        
        //Animations and Hitboxes (this can get stupidly large)
        GenerateAnimationsAndHitboxes();
        
        //Collisions
        PlayerCollisionEnabled = true;
        EnemyCollisionEnabled = true;
        ObjectCollisionEnabled = true;
        BulletCollisionEnabled = true;
        ItemCollisionEnabled = true;
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
        DirectionHorizontal = Directions.Right;
        DirectionVertical = Directions.None;//We won't use this on a top down view
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
        Name = "Player Character";
        OwnerName = "None";
        IsPlayerOwned = true;
        OwnerArrayIndex = -1;
        EntityArrayIndex = -1;
        AIType = 0;
        Health = main.Global.PlayerHealth;
        HealthMax = main.Global.PlayerHealthMax;
        IsDying = false;
        IsFriendly = true;
        IsHostile = false;
        Invulnerable = false;
        DamageOnTouch = false;
        DamageOnTouchValue = 0;
        IgnorePlayerInput = false;
        IsPlayerActivatorEnabled = false;
// </editor-fold>

        //Centrally place the Player on the tile that he spawns.
        LocationX += HitBoxWidth[CurrentAnimation] + ((main.Global.TileWidth - HitBoxWidth[CurrentAnimation]));
        LocationY += HitBoxHeight[CurrentAnimation] + ((main.Global.TileHeight - HitBoxHeight[CurrentAnimation]));
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
        AnimationCount = 8;
        //The sprite animations list
        EntityAnimation = new AnimationComposite[AnimationCount];
        //The Hitboxes to match with each sprite in the list
        HitBoxWidth = new int[AnimationCount];
        HitBoxHeight = new int[AnimationCount];
        HitBoxOffsetX = new float[AnimationCount];
        HitBoxOffsetY = new float[AnimationCount];
        MinimumYTilesToCheck = new int[AnimationCount];
        MinimumXTilesToCheck = new int[AnimationCount];
        
// <editor-fold defaultstate="collapsed" desc="Animation0 StandDown">
        EntityAnimation[0] = new AnimationComposite(0, 1, 32, 32, 0, false, false);
        EntityAnimation[0].AddFrame(0, 1, 5000, 0, 0);

        HitBoxWidth[0] = 20;
        HitBoxHeight[0] = 28;
        HitBoxOffsetX[0] = 2;
        HitBoxOffsetY[0] = 4;
        SetMinimumTilesForHitBox(0);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation1 StandUp">
        EntityAnimation[1] = new AnimationComposite(0, 1, 32, 32, 0, false, false);
        EntityAnimation[1].AddFrame(0, 10, 5000, 0, 0);

        HitBoxWidth[1] = 20;
        HitBoxHeight[1] = 28;
        HitBoxOffsetX[1] = 2;
        HitBoxOffsetY[1] = 4;
        SetMinimumTilesForHitBox(1);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation2 StandLeft">
        EntityAnimation[2] = new AnimationComposite(0, 1, 32, 32, 0, false, false);
        EntityAnimation[2].AddFrame(0, 4, 5000, 0, 0);

        HitBoxWidth[2] = 20;
        HitBoxHeight[2] = 28;
        HitBoxOffsetX[2] = 0;
        HitBoxOffsetY[2] = 4;
        SetMinimumTilesForHitBox(2);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation3 StandRight">
        EntityAnimation[3] = new AnimationComposite(0, 1, 32, 32, 0, true, false);
        EntityAnimation[3].AddFrame(0, 4, 5000, 0, 0);

        HitBoxWidth[3] = 20;
        HitBoxHeight[3] = 28;
        HitBoxOffsetX[3] = 12;
        HitBoxOffsetY[3] = 4;
        SetMinimumTilesForHitBox(3);
// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="Animation4 WalkDown">
        EntityAnimation[4] = new AnimationComposite(0, 1, 32, 32, 0, false, false);
        EntityAnimation[4].AddFrame(0, 1, 100, 0, -2);
        EntityAnimation[4].AddFrame(0, 0, 200, 0, 0);
        EntityAnimation[4].AddFrame(0, 1, 100, 0, -2);
        EntityAnimation[4].AddFrame(0, 2, 200, 0, 0);

        HitBoxWidth[4] = 20;
        HitBoxHeight[4] = 28;
        HitBoxOffsetX[4] = 2;
        HitBoxOffsetY[4] = 2;
        SetMinimumTilesForHitBox(4);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation5 WalkUp">
        EntityAnimation[5] = new AnimationComposite(0, 1, 32, 32, 0, false, false);
        EntityAnimation[5].AddFrame(0, 10, 100, 0, -2);
        EntityAnimation[5].AddFrame(0, 9, 200, 0, 0);
        EntityAnimation[5].AddFrame(0, 10, 100, 0, -2);
        EntityAnimation[5].AddFrame(0, 11, 200, 0, 0);

        HitBoxWidth[5] = 20;
        HitBoxHeight[5] = 28;
        HitBoxOffsetX[5] = 2;
        HitBoxOffsetY[5] = 4;
        SetMinimumTilesForHitBox(5);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation6 WalkLeft">
        EntityAnimation[6] = new AnimationComposite(0, 1, 32, 32, 0, false, false);
        EntityAnimation[6].AddFrame(0, 4, 100, 0, 0);
        EntityAnimation[6].AddFrame(0, 3, 200, 0, 0);
        EntityAnimation[6].AddFrame(0, 4, 100, 0, 0);
        EntityAnimation[6].AddFrame(0, 5, 200, 0, 0);

        HitBoxWidth[6] = 20;
        HitBoxHeight[6] = 28;
        HitBoxOffsetX[6] = 0;
        HitBoxOffsetY[6] = 4;
        SetMinimumTilesForHitBox(6);
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="Animation7 WalkRight">
        EntityAnimation[7] = new AnimationComposite(0, 1, 32, 32, 0, true, false);
        EntityAnimation[7].AddFrame(0, 4, 100, 0, 0);
        EntityAnimation[7].AddFrame(0, 3, 200, 0, 0);
        EntityAnimation[7].AddFrame(0, 4, 100, 0, 0);
        EntityAnimation[7].AddFrame(0, 5, 200, 0, 0);

        HitBoxWidth[7] = 20;
        HitBoxHeight[7] = 28;
        HitBoxOffsetX[7] = 12;
        HitBoxOffsetY[7] = 4;
        SetMinimumTilesForHitBox(7);
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
        
        //Use this to change the Entity based on things:
        //If the previous frame said you were touching a water tile, then slow your movement speed value or take damage
        
        if (LevelState.CutScene != CutSceneType.ChatWindow) {
            
            if (main.Global.Keyboard.Player1LeftPressed > 0) {
                DirectionHorizontal = Directions.Left;
            }
            if (main.Global.Keyboard.Player1RightPressed > 0) {
                DirectionHorizontal = Directions.Right;
            }
            if (main.Global.Keyboard.Player1UpPressed > 0) {
                DirectionHorizontal = Directions.Up;
            }
            if (main.Global.Keyboard.Player1DownPressed > 0) {
                DirectionHorizontal = Directions.Down;
            }
            if (main.Global.Keyboard.Player1JumpPressed == 1) {
                //Single push event. Works only one frame, until repressed.
            }
            if (main.Global.Keyboard.Player1ShootPressed == 1 || main.Global.Keyboard.Player1PausePressed == 1) {
                GeneratePlayerActivator();
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
        
        if (VelocityEnabled && LevelState.CutScene != CutSceneType.ChatWindow) {
        
            if (!IgnorePlayerInput) {
                
                if (main.Global.Keyboard.Player1RightPressed > 0) {
                    VelocityX += HorizontalSpeed;
                }
                if (main.Global.Keyboard.Player1LeftPressed > 0) {
                    VelocityX -= HorizontalSpeed;
                }
                if (main.Global.Keyboard.Player1UpPressed > 0) {
                    VelocityY -= VerticalSpeed;
                }
                if (main.Global.Keyboard.Player1DownPressed > 0) {
                    VelocityY += VerticalSpeed;
                }
            }
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
        if (!AnimationLock && LevelState.CutScene != CutSceneType.ChatWindow)
        {
            frameanim = 1;
            anim = true;
        
            if (DirectionHorizontal == Directions.Left)
            {
                if (main.Global.Keyboard.Player1LeftPressed > 0) {
                    //Walking left
                    frameanim = 6;
                    anim = true;
                } else {
                    //Standing left
                    frameanim = 2;
                    anim = false;
                }
            } 
            
            if (DirectionHorizontal == Directions.Right)
            {
                if (main.Global.Keyboard.Player1RightPressed > 0) {
                    //Walking right
                    frameanim = 7;
                    anim = true;
                } else {
                    //Standing right
                    frameanim = 3;
                    anim = false;
                }
            }
            
            if (DirectionHorizontal == Directions.Up)
            {
                if (main.Global.Keyboard.Player1UpPressed > 0) {
                    //Walking up
                    frameanim = 5;
                    anim = true;
                } else {
                    //Standing up
                    frameanim = 1;
                    anim = false;
                }
            }
            
            if (DirectionHorizontal == Directions.Down)
            {
                if (main.Global.Keyboard.Player1DownPressed > 0) {
                    //Walking down
                    frameanim = 4;
                    anim = true;
                } else {
                    //Standing down
                    frameanim = 0;
                    anim = false;
                }
            }
        }
        
        //Set the finally determined animation and state
        ChangeCurrentAnimation(frameanim, anim);
    }    
    
    public void SetRoomTransition(int direction) {
        
        switch (direction) 
        {
            case 0:
                RoomTransitionDirection = Directions.None;
                InertiaX = 0;
                InertiaY = 0;
                break;
            case 1:
                RoomTransitionDirection = Directions.Up;
                InertiaX = 0;
                InertiaY = -RoomTransitionSpeedVertical;
                break;
            case 2:
                RoomTransitionDirection = Directions.Down;
                InertiaX = 0;
                InertiaY = RoomTransitionSpeedVertical;
                break;
            case 3:
                RoomTransitionDirection = Directions.Left;
                InertiaX = -RoomTransitionSpeedHorizontal;
                InertiaY = 0;
                break;
            case 4:
                RoomTransitionDirection = Directions.Right;
                InertiaX = RoomTransitionSpeedHorizontal;
                InertiaY = 0;
                break;
        }
        
        if (RoomTransitionDirection == Directions.None) {
            VelocityEnabled = true;
            IgnorePlayerInput = false;
            //GravityEnabled = true;
            PlayerCollisionEnabled = true;
            EnemyCollisionEnabled = true;
            ObjectCollisionEnabled = true;
            BulletCollisionEnabled = true;
            ItemCollisionEnabled = true;
            TileCollisionEnabled = true;
            AnimationLock = false;
            DirectionLock = false;
        } else {
            VelocityEnabled = false;
            IgnorePlayerInput = true;
            //GravityEnabled = false;
            PlayerCollisionEnabled = false;
            EnemyCollisionEnabled = false;
            ObjectCollisionEnabled = false;
            BulletCollisionEnabled = false;
            ItemCollisionEnabled = false;
            TileCollisionEnabled = false;
            AnimationLock = true;
            DirectionLock = true;
        }
    }
    
    @Override
    public void SubtractHealth(float value)
    {
        Health -= value;
        if (Health < 1 && IsAlive)
        {//None left, so kill character
            Health = 0;
        }
        if (Health > HealthMax)
        {
            Health = HealthMax;
        }
    }
        
    @Override
    public void AddHealth(float value)
    {
        Health += value;
        main.Global.Sound.PlaySound(12);
        if (Health < 1)
        {//None left, so kill character
            Health = 0;
        }
        if (Health > HealthMax)
        {
            Health = HealthMax;
        }
    }
    
    @Override
    public void ProcessCollision(Entity Entity)
    {
        if (Entity.IsHostile)
        {//Player can take damage/negative effects from the Entity
            if (Entity.DamageOnTouch && !IsInHitStun)
            {
                TriggerHitStun();
                SubtractHealth(Entity.DamageOnTouchValue);
                if (Health  <= 0 && !IsDying) {
                    IsDying = true;
                    IgnorePlayerInput = true;
                    AnimationLock = true;
                    LevelState.PerformCutScene("Resource/Movie/GameOverDeath.scr", CutSceneType.CustomEvent);
                }
            }                
        }
        if (Entity.IsFriendly)
        {//Player can receive health/positive effects from the Entity

        }

        //This Entity cares about if the other Entity is a platform or wall.
        //Check if it should restrict movement.
        //An Entity may only want to respect the wall nature of something if it's friendly,
        //or some other weird set up.
        ProcessCollisionEntityWalls(Entity);
        
    }
    
    @Override
    public void ProcessCollision(Item Item)
    {
        if (Item.IsHostile)
        {//Player can take damage/negative effects from the Entity
            if (Item.DamageOnTouch)
            {
                SubtractHealth(Item.DamageOnTouchValue);
            }                
        }
        if (Item.IsFriendly)
        {//Player can receive health/positive effects from the Entity
            if (Item.DamageOnTouch) {
                AddHealth(Item.DamageOnTouchValue);
                main.Global.Sound.PlaySound(5);
            }
        }
    }
    
    private void GeneratePlayerActivator() {
        switch (DirectionHorizontal) {
            case Up:
                LevelState.CreateObject(3, 0, DirectionHorizontal, DirectionVertical, ComputeHitBoxCenterX(), LocationY - 2, false, false, 0, 0, 0, "");
                break;
            case Down:
                LevelState.CreateObject(3, 0, DirectionHorizontal, DirectionVertical, ComputeHitBoxCenterX() - 2, ComputeHitBoxBottomSide() - 1, false, false, 0, 0, 0, "");
                break;
            case Left:
                LevelState.CreateObject(3, 0, DirectionHorizontal, DirectionVertical, LocationX - 2, ComputeHitBoxCenterY() - 1, false, false, 0, 0, 0, "");
                break;
            case Right:
                LevelState.CreateObject(3, 0, DirectionHorizontal, DirectionVertical, ComputeHitBoxRightSide(), ComputeHitBoxCenterY() - 1, false, false, 0, 0, 0, "");
                break;
        }
        
    }
    
}