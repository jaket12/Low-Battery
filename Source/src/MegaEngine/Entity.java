package MegaEngine;

import org.newdawn.slick.*;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.opengl.renderer.Renderer;
import MegaEngine.Global.Directions;

/**
 * An Entity is any thing in the Level State that has logic, interaction, or
 * any other sense of presence in the game world.
 * Players, Enemies, Bullets, and Items are all Entities.
 * Entities are capable of holding their own sprites, attributes, and logic.
 * They can check for collision between each other, and understand the Tiled Level
 * and screen.
 * Entity as the class is the most basic form of 'thing' in the game: all other
 * objects inherit from this base class and build on it to make something unique.
 * The Entity class holds the most common traits that things will have in the game.
 * Things like an image to draw, how much health, where it's located, how it interacts.
 * @author McRib
 */
public class Entity {
    
    //Location
    
    //Where the entity is in the game world in relation to the ENTIRE Tiled Map.
    //This is on the degree of pixels: 32 pixels would be 1 tile.
    protected float LocationX = 0;
    protected float LocationY = 0;
    //The top-left tile that this Entity resides in. This is in terms of the TileArray[X][Y] for the entire Level.
    protected int CurrentTileX = 0;
    protected int CurrentTileY = 0;
    //IdealXY is the final LocationXY that the entity will move to after applying all Velocity if nothing is in the way.
    protected float IdealX;
    protected float IdealY;
    //NextLocationXY is the final LocationXY that the entity will move to after applying all Velocity if something is in the way.
    protected float NextLocationX;
    protected float NextLocationY;
    //Location in relation to the game screen. 0,0 would be top left of the screen.
    protected float ScreenLocationX = 0;
    protected float ScreenLocationY = 0;
    //Whether the Entity has at least 1 pixel of the Hit Box within the game screen boundaries.
    protected boolean OnScreen = true;
    
    
    
    //Movement
    
    //Velocity is the simple form of movement: the amount of pixels moved in a single frame.
    protected float VelocityX = 0;
    protected float VelocityY = 0;
    //Determines if VelocityXY will apply to the Entity each frame. Does not impact InertiaXY.
    protected boolean VelocityEnabled = true;
    //Inertia is like Velocity, only it applies on every frame. A constant, steady force.
    protected float InertiaX = 0;
    protected float InertiaY = 0;
    //If this Entity is affected by GravityEnabled Velocity.
    //Constant downward push per frame. It accumulates quickly over time.
    protected boolean GravityEnabled = false;
    //How much cumulative velocity is being added to the Entity this frame due to GravityEnabled.
    protected float CurrentGravityVelocity = 0f;
    //The default GravityEnabled strength. This value is applied dependent on the frame delta.
    protected float NormalGravity = 2.5f;
    //The fastest the Entity can move in any direction
    protected float MaxVelocity = 780f; 
    //Default movement speed of the Entity if it tries to move.
    //Custom movement can be created within the precise Entity itself.
    protected float HorizontalSpeed = 165f; 
    protected float VerticalSpeed = 0f;
    
    
    //Hitbox
    
    //Width and Height of the rectangle that can collide with other Entities.
    //Each frame of animation may have a different size, so the array is based on what the current sprite is: HitBoxWidth[CurrentAnimation].
    protected int HitBoxWidth[];
    protected int HitBoxHeight[];
    //OffsetXY is if the sprite image needs to be adjusted in relation to the hitbox.
    //OffsetX[0] = -12; means that the first animation for this entity will have the sprite shifted 12 pixels to the left of the hitbox when drawn.
    protected float HitBoxOffsetX[];
    protected float HitBoxOffsetY[];
    //Computed value based on the dimensions of the hitbox: the smallest number of tiles that the character can take up.
    //This is an array that maps to each sprite animation for the Entity.
    protected int MinimumYTilesToCheck[];
    protected int MinimumXTilesToCheck[];
    
    
    
    //Collisions
    
    //Any Collision value means that this Entity may collide with that type of Entity.
    //Collisions are determined every frame: every entity compares hitboxes against every other entity.
    protected boolean PlayerCollisionEnabled = false;
    protected boolean EnemyCollisionEnabled = false;
    protected boolean ObjectCollisionEnabled = false;
    protected boolean BulletCollisionEnabled = false;
    protected boolean ItemCollisionEnabled = false;
    protected boolean TileCollisionEnabled = false;
    //Whether or not this Entity is currently right next to a solid tile or other movement restrictive object.
    protected boolean IsTouchingCeiling = false;
    protected boolean IsTouchingLeftWall = false;
    protected boolean IsTouchingRightWall = false;
    //Whether or not this Entity's bottom hit box is touch any solid tile or Entity which counts as a solid.
    protected boolean IsMidAir = true;
    //If this Entity is touching another Entity, it can be prevented from moving.
    protected boolean IsTouchingEntityLeftWall = false;
    protected boolean IsTouchingEntityRightWall = false;
    //If this Entity is currently on top of another Entity that acts as a solid surface.
    protected boolean IsTouchingEntityPlatform = false;
    //This Entity has Collided with another Entity that acts as a Ceiling. Act as if this Entity hit a tile from above.
    protected boolean IsTouchingEntityCeiling = false;
    //The final result of calculating tile collisions.
    //For whichever direction the Entity is moving, this states if it can actually move in that direction at all.
    //If it can, then the distance it will move is based on IdealXY or NextLocationXY.
    protected boolean CanMoveHorizontal = false;
    protected boolean CanMoveVertical = false;
    //Entity acts as a solid only in horizontal motion: other Entities cannot pass left/right through it, but can go up/down.
    protected boolean IsLeftWall = false;
    protected boolean IsRightWall = false;
    //Entity can be used as solid tile if stood on. The Entity does not prevent other Entities from moving in other directions.
    protected boolean IsPlatform = false;
    //Entity prevents other Entities from jumping through it: they can fall through or go left/right, but cannot rise above it.
    protected boolean IsCeiling = false;
    
    
    
    //Sprite Drawing Rules
    
    //Determines if the current sprite animation will be drawn on the screen.
    protected boolean IsVisible = true;
    //Determines if the sprite needs to call Update() to move it's animation to the next frame.
    protected boolean IsAnimating = true;
    //If true, draw a square on the screen to show the hit box of this entity
    protected boolean DrawHitBox = false;
    //When Locked, the CurrentAnimation cannot be changed to show another sprite image.
    protected boolean AnimationLock = false;
    //Prevents the Entity from changing the direction they are facing; both horizontally and vertically.
    protected boolean DirectionLock = false;
    //The Left/Right/None direction that the Entity is facing. Determines what animation to play (and some game logic too)
    protected Directions DirectionHorizontal = Directions.None;
    //The Up/Down/None direction that the Entity facing.
    protected Directions DirectionVertical = Directions.None;
    //The sprite animation that will be drawn from the array of EntityAnimations.
    protected int CurrentAnimation = 0;
    //The animation that was used before the CurrentAnimation was switched to.
    //If these values don't match, the CurrentAnimation sprite will reset it's animation loop.
    protected int PreviousAnimation = 0;
    //Total number of animations for this character.
    //Some kind of low level drawing mechanism.
    protected SGL SGLRenderer = Renderer.get();
    //How many animations are available to this Entity.
    protected int AnimationCount;
    //A list of animation sprites this Entity can use. CurrentAnimation is whatever the Index is of the sprite to draw.
    protected AnimationComposite[] EntityAnimation;
    //The number of layers of Color used for each EntityAnimation.
    //If an animation does not use fancy layers, just set 1 layer, with each color to the max value.
    //The tinting that is applied to the sprite image when drawn.
    //This is too complex right now, need to refactor later.
    protected int PaletteLayers = 1;
    protected float[] PalettetRed = new float[PaletteLayers];
    protected float[] PaletteGreen = new float[PaletteLayers];
    protected float[] PaletteBlue = new float[PaletteLayers];
    protected float[] PaletteTransparency = new float[PaletteLayers];
    
    
    
    //Despawn Rules
    
    //If the Entity is active in the game. If false, Update() and Draw() will not be run.
    //A dead Entity may be overwritten with another live Entity.
    protected boolean IsAlive = false;
    //If true, then this Entity may be fully erased while dead so something new can replace it in the Entity Array.
    protected boolean IsOverWritable = true;
    //If the entity goes off screen further than the leash distance, it goes away
    protected boolean DespawnOffScreen = true;
    //How many pixels the Entity can be off the screen before it despawns.
    //Distance is determined by the side of the hitbox closest to the screen.
    protected int ScreenLeashX = 64;
    protected int ScreenLeashY = 64;
    //When the player triggers a transition to another room, this entity will be destroyed
    protected boolean DespawnOnRoomChange = true;
    //If the game is paused (switching to another game state or menu) then this entity is removed
    protected boolean DeleteOnPause = false;
    //If this Entity dies, it may come back Alive if something tries to trigger it.
    protected boolean Respawnable = true;
    //If this Entity has already died and come back to life.
    protected boolean Respawned = false;
    
    
    
    //Stats and Values
    
    //Unique name for the Entity. Basically, whatever the expected type of thing it is: "Player 1", "Met", "Enemy Type 2"
    //Used for locating all Entities of a specific group in the Game State.
    protected String Name = "Unknown";
    //If this Entity was spawned by another Entity, it can track its parent's name for reference.
    protected String OwnerName = "Unowned";
    //If this Entity was created by the Player, as opposed to an Enemy, Item, or Object.
    protected boolean IsPlayerOwned = false;
    //Array Index of the Entity which owns this Entity.
    protected int OwnerArrayIndex = 0;//-1 is player 1, -2 player 2, any positive number is an enemy in the array
    //Location in the Array that this Entity belongs to. Can be passed into an Entity it generates so the parent is tracked.
    protected int EntityArrayIndex = 0;
    //Custom state which may be used to change the logic of how an Entity will act.
    protected int AIType = 0;
    //Current life value of the Entity.
    protected float Health = 1;
    //Maximum possible life value of the Entity.
    protected float HealthMax = 1;
    //When true, denotes the AI State of the Entity to begin it's death animation process. This must be uniquely implemented.
    protected boolean IsDying = false;
    //The Entity is intended to help the Player. Might heal the Player and hurt an Enemy.
    protected boolean IsFriendly = false;
    //The Entity is intended to hurt the Player. Might hurt the Player and heal an Enemy.
    protected boolean IsHostile = false;
    //If the Entity will not even consider damage or death effects.
    protected boolean Invulnerable = false;
    //If the Entity was damaged, this denotes that it is performing the 'damage blink' effect.
    protected boolean IsInHitStun = false;
    //How long the stun interval lasts for.
    protected Timer HitStunTimer = new Timer("Damage blink duration", 1000, true, true);
    //How fast the Entity goes from Visible to Invisible.
    protected Timer HitStunBlinkIntervalTimer = new Timer("Damage blink speed", 200, true, true);
    //If Collision with another Entity, this thing's purpose is to hurt it.
    protected boolean DamageOnTouch = false;
    //When this Entity Collides with another, this amount of damage is applied to the other Entity.
    protected float DamageOnTouchValue = 0;
    //Entity took a jump and has not yet landed back on the ground.
    protected boolean IsCurrentlyInAJump = false;
    //Tracks if the Entity has gone beyond the boundaries of the current Room.
    protected boolean IsBeyondRoomCeiling = false;
    protected boolean IsBeyondRoomFloor = false;
    protected boolean IsBeyondRoomLeft = false;
    protected boolean IsBeyondRoomRight = false;
    //If the Entity Hitbox is even slightly out of bound of the screen.
    protected boolean IsOffScreen = false;
    //I guess this is the location of a boundary for each direction of the room?
    protected int[] CurrentRoomBoundaries;
    //If true, player keyboard buttons do nothing.
    protected boolean IgnorePlayerInput;
    //If this is true, a PlayerActivator will be consumed if it touches this Entity.
    protected boolean IsPlayerActivatorEnabled = false;
    //The file name of the script this entity might want to play at some point.
    protected String CutSceneScriptFileName = "";
    
    //Iterators and temp values that should never be manually set
    
    protected int i;
    protected int j;
    protected int Ti;
    protected int Tj;
    //How many tiles to check for Collision, based on where the Entity is.
    protected int TotalXTilesToCheck;
    protected int TotalYTilesToCheck;
    //Precise pixel amount that the Entity is inside of the Tile it occupies: the range can be 0-32 if a tile width/height is 32 pixels.
    protected float YPointInsideCurrentTile;
    protected float XPointInsideCurrentTile;
    //XY Co-ordinate for the Tile that the Entity is trying to move into during a Collision check.
    protected int UpTileToMoveToX;
    protected int UpTileToMoveToY;
    protected int DownTileToMoveToX;
    protected int DownTileToMoveToY;
    protected int LeftTileToMoveToX;
    protected int LeftTileToMoveToY;
    protected int RightTileToMoveToX;
    protected int RightTileToMoveToY;
    //The XY Tile location that the Entity's Hitbox is directly above and standing on.
    protected int TileStandingOnX;
    protected int TileStandingOnY;
    //The primary type of Tile that is being checked: Solid, Water, Air/None...
    protected int TileValue;
    //Placeholder to determine if the sprite to show is animating.
    protected boolean anim = true;
    //Placeholder to determine which animation to play during a GetCurrentAnimation() call.
    protected int frameanim = 0;
    
    
    // Pathfinding
    protected boolean HasChaseEnabled = false;
    
    
    /*

    //CutScenes
    protected boolean CutSceneRoomTransition;
    protected boolean CutSceneScripted;
    protected Directions RoomTransitionDirection;
    protected float RoomTransitionSpeedHorizontal = 111f;//How fast the character moves between rooms
    protected float RoomTransitionSpeedVertical = 128f;
    protected float DistanceToNextRoom;//During a room transition, this number goes down by distance travelled.  When 0, game resumes
    protected boolean PerformCutScene = false;//If true, the next frame will trigger a scene
    protected String CutScene = "";//filename of the scene
    
    /*
     * Cutscenes and scripts
     */
/*    protected ArrayList Script = new ArrayList(); //Each line in a script file is placed in order here.
    protected String splits[]; //Dedicated string splitter for efficency in doing scripts
    protected int ReadLine = 0; //Current Line in the script being read
    protected String ScriptCurrentLine = ""; //A copied peice of that line
    protected boolean PlayScript = false;
    protected String ScriptName = null;
    protected int WaitTimer = 0;
    protected String DeathScriptName = null;
    protected boolean PlayDeathScript = false;
    
    
    
    */
    
    
    
    
    
    
    
    
    /**
     * The base level form of any 'thing' that will be inserted into the Level State.
     * An Entity holds information about itself and how it interacts with others.
     * It knows it's location, velocity, what created it, and what simple things it can do.
     * Players, Bullets, Enemies, Items, and Objects all inherit this base class
     * and will use it to understand interactions between each other.
     * Entities can detect if they collide with other Entities, and then trade
     * information so they can perform an action dependent on what it Collided with.
     */
    public Entity()
    {
        
    }
        
    public String GetName()
    {
        return Name;
    }
    
    public int getAIType()
    {
        return AIType;
    }
    
    public Directions getDirectionHorizontal()
    {
        return DirectionHorizontal;
    }
    
    public Directions getDirectionVertical()
    {
        return DirectionVertical;
    }
    
    public float getLocationX()
    {
        return LocationX;
    }
    
    public float getLocationY()
    {
        return LocationY;
    }
    
    public float getVelocityY()
    {
        return VelocityY;
    }
    
    public float getVelocityX()
    {
        return VelocityX;
    }
    
    public int getHitBoxWidth()
    {
        return HitBoxWidth[CurrentAnimation];
    }
    
    public int getHitBoxHeight()
    {
        return HitBoxHeight[CurrentAnimation];
    }
    
    public boolean GetIsAlive() {
        return IsAlive;
    }
    
    public void IgnorePlayerInput(boolean value)
    {
        IgnorePlayerInput = value;
    }
    
    public boolean IgnorePlayerInput()
    {
        return IgnorePlayerInput;
    }
    
    public boolean IsPlayerActivatorEnabled() {
        return IsPlayerActivatorEnabled;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @param value 
     */
    protected void PlayerCollision(boolean value)
    {
        PlayerCollisionEnabled = value;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @return 
     */
    public boolean PlayerCollision()
    {
        return PlayerCollisionEnabled;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @param value 
     */
    protected void EnemyCollision(boolean value)
    {
        EnemyCollisionEnabled = value;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @return 
     */
    public boolean EnemyCollision()
    {
        return EnemyCollisionEnabled;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @param value 
     */
    protected void ObjectCollision(boolean value)
    {
        ObjectCollisionEnabled = value;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @return 
     */
    public boolean ObjectCollision()
    {
        return ObjectCollisionEnabled;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @param value 
     */
    protected void BulletCollision(boolean value)
    {
        BulletCollisionEnabled = value;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @return 
     */
    public boolean BulletCollision()
    {
        return BulletCollisionEnabled;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @param value 
     */
    protected void ItemCollision(boolean value)
    {
        ItemCollisionEnabled = value;
    }
    
    /**
     * Any Collision value means that this Entity may collide with that type of Entity.
     * Collisions are determined every frame: every entity compares hitboxes against every other entity.
     * @return 
     */
    public boolean ItemCollision()
    {
        return ItemCollisionEnabled;
    }
    
    
    protected void IsFriendly(boolean value)
    {
        IsFriendly = value;
    }
    
    public boolean IsFriendly()
    {
        return IsFriendly;
    }
    
    protected void IsHostile(boolean value)
    {
        IsHostile = value;
    }
    
    public boolean IsHostile()
    {
        return IsHostile;
    }
    
    protected void DamageOnTouch(boolean value)
    {
        DamageOnTouch = value;
    }
    
    public boolean DamageOnTouch()
    {
        return DamageOnTouch;
    }
    
    protected void DamageOnTouchValue(float value)
    {
        DamageOnTouchValue = value;
    }
    
    public float DamageOnTouchValue()
    {
        return DamageOnTouchValue;
    }
    
    
    protected void IsPlatform(boolean value)
    {
        IsPlatform = value;
    }
        
    protected void IsInvuln(boolean value)
    {
        Invulnerable = value;
    }
    
    protected boolean IsInvuln()
    {
        return Invulnerable;
    }
    
    protected void IsMidAir(boolean value)
    {
        IsMidAir = value;
    }
    
    protected void IsVisible(boolean value)
    {
        IsVisible = value;
    }
    
    protected boolean IsVisible()
    {
        return IsVisible;
    }

    public int GetEntityArrayID()
    {
        return EntityArrayIndex;
    }
    
    public void setEntityArrayID(int value)
    {
        EntityArrayIndex = value;
    }
    
    public void SetHealth(float value) {
        Health = value;
    }
    
    public float GetHealth()
    {
        return Health;
    }
    
    public float GetHealthMax() {
        return HealthMax;
    }
    
    public boolean getRespawned()
    {
        return Respawned;
    }
    
    public int getCurrentWidth()
    {
        return HitBoxWidth[CurrentAnimation];
    }
    
    public int getCurrentHeight()
    {
        return HitBoxHeight[CurrentAnimation];
    }
    
    public boolean IsStopped()
    {
        return EntityAnimation[CurrentAnimation].IsStopped();
    }
    
    public int GetAnimation()
    {
        return CurrentAnimation;
    }
    
    public boolean GetisFalling()
    {
        return IsMidAir;
    }
    
    public int GetCurrentTileX() {
        return CurrentTileX;
    }

    public int GetCurrentTileY() {
        return CurrentTileY;
    }
    
    public float GetTileOffSetX() {
        return XPointInsideCurrentTile;
    }

    public float GetTileOffSetY() {
        return YPointInsideCurrentTile;
    }
    
    public float GetLocationX() {
        return LocationX;
    }

    public float GetLocationY() {
        return LocationY;
    }
    
    public void SetLocationX(int X) {
        LocationX = X;
    }

    public void SetLocationY(int Y) {
        LocationY = Y;
    }
    
    
    public void isAnimating(boolean value)
    {
        IsAnimating = value;
        EntityAnimation[CurrentAnimation].isAnimating(IsAnimating);
    }
    
    public boolean GravityEnabled()
    {
        return GravityEnabled;
    }
    
    public void GravityEnabled(boolean value)
    {
        GravityEnabled = value;
    }
    
    public boolean TileCollision()
    {
        return TileCollisionEnabled;
    }
    
    public void TileCollision(boolean value)
    {
        TileCollisionEnabled = value;
    }
    
    public boolean IsInCurrentRoomBoundary()
    {
        return !IsBeyondRoomCeiling && !IsBeyondRoomFloor && !IsBeyondRoomLeft && !IsBeyondRoomRight;
    }
        
    public boolean VelocityEnabled()
    {
        return VelocityEnabled;
    }
    
    public void VelocityEnabled(boolean value)
    {
        VelocityEnabled = value;
        
        if (!VelocityEnabled)
        {
            VelocityX = 0;
            VelocityY = 0;
        }
    }
    
    public void AnimationLock(boolean value)
    {
        AnimationLock = value;
    }
    
    public boolean AnimationLock()
    {
        return AnimationLock;
    }
    
    
    public void SetInertiaX(float value)
    {
        InertiaX = value;
    }
    
    public void SetInertiaY(float value)
    {
        InertiaY = value;
    }
    
    public void IsStandingOnObject(boolean value)
    {
        IsTouchingEntityPlatform = true;
    }
    
    public void GetCurrentAnimation()
    {
    
    }
    
    /**
     * A big boring list of data that says what sprite sheets to use to build
     * multiple animations, and the Hitboxes that relate to those animations.
     */
    protected void GenerateAnimationsAndHitboxes() {
        
    }
    
    /**
     * Convert the pixel location of the Entity on the Level, into the Tile index
     * equivalent.
     * Basically, given the exact location of the Entity, return the Tile
     * coordinate that it's top right corner is standing in.
     * @return TileX index of the Entity location.
     */
    protected int LocationXToCurrentTileX() {
        return (int) (LocationX / main.Global.TileWidth) - 1;//Must lower by 1 to get same tile
    }
    
    /**
     * Convert the pixel location of the Entity on the Level, into the Tile index
     * equivalent.
     * Basically, given the exact location of the Entity, return the Tile
     * coordinate that it's top right corner is standing in.
     * @return TileY index of the Entity location.
     */
    protected int LocationYToCurrentTileY() {
        return (int) (LocationY / main.Global.TileHeight) - 1;//Engine uses 1,1 as first tile, Tiled uses 0,0 as first
    }
    
    /**
     * This is the logical side of the Entity that makes it 'think' and do stuff.
     * Update() must be called every frame of the game in order to keep the
     * Entity in sync with everything else.
     * The goal is to pass in all critical information that an Entity will need
     * in order to run its AI. This includes how much time has passed for the frame,
     * what the current state of the Level is, and what the Player is doing right now.
     * @param delta How much time has passed since the last frame
     * @param TileData Entire Level Tilemap and special values for each tile
     * @param RoomData Specific Level Tilemap data for the current room
     * @param Player
     */
    
    public void Update(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player)
    {
        //Perform the basic things that an Entity would: tile collisions, basic velocity
        //You can make a custom update function by overriding this one and doing
        //whatever custom thing you need to do.
        if (IsAlive) {
            //Status
            ComputeStatus(delta, TileData, RoomData, Player);
            ComputeInternalStatus(delta, TileData, RoomData, Player);
            //Compute Velocity values
            ComputeVelocity();
            //Apply the Velocity values
            ApplyVelocity(delta);
            //Tile Collision and applied movement
            ComputeTileCollision(TileData, RoomData);
            //Animation to draw
            ComputeAnimation(delta);
            
            IsTouchingEntityRightWall = false;
            IsTouchingEntityLeftWall = false;
            IsTouchingEntityCeiling = false;
            IsTouchingEntityPlatform = false;
        }
    }
        
    /**
     * The first step in the Update() process which determines what the Entity
     * wants to do this frame based on input and other current states of the game.
     * @param delta
     * @param TileData
     * @param RoomData
     * @param Player
     */
    protected void ComputeStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        
    }
    
    /**
     * After the custom logic has ran, perform universally shared logic.
     * Things like hit stun or death animations.
     * @param delta
     * @param TileData
     * @param RoomData
     * @param Player
     */
    protected void ComputeInternalStatus(int delta, int[][][] TileData, int[][] RoomData, PlayerCharacter Player) {
        
        if (IsInHitStun) {
            if (HitStunBlinkIntervalTimer.Update(delta)) {
                IsVisible = !IsVisible;
            }
            if(HitStunTimer.Update(delta)) {
                RemoveHitStun();
            }
        }
        
        if (!OnScreen) {
            
        }
        
    }
    
    /**
     * The second step in the Update() process which determines how far the
     * Entity wants to move this frame, based on the Status that was just computed.
     */
    protected void ComputeVelocity() {
        
    }
    
    /**
     * Calculate the final values of velocity that were tallied from ComputeVelocity.
     * @param delta 
     */
    protected void ApplyVelocity(int delta) {
        
        //Apply the final inertias which are permanent until told to change
        VelocityX += InertiaX;
        VelocityY += InertiaY;
        
        //Apply gravity if Entity is in midair
        if (IsMidAir && GravityEnabled)
        {
            CurrentGravityVelocity += NormalGravity;
            VelocityY += CurrentGravityVelocity;
        }
        else
        {
            CurrentGravityVelocity = 0;
        }
        
        //Impose speed limitations so the game doesn't break or jump through walls
        if (VelocityX > MaxVelocity) {
            VelocityX = MaxVelocity;
        }
        if (VelocityY > MaxVelocity) {
            VelocityY = MaxVelocity;
        }
        if (VelocityX < MaxVelocity * -1) {
            VelocityX = MaxVelocity * -1;
        }
        if (VelocityY < MaxVelocity * -1) {
            VelocityY = MaxVelocity * -1;
        }
        
        //Compute the expected movement, collision pending
        NextLocationX = LocationX + (VelocityX * (delta / 1000f));
        NextLocationY = LocationY + (VelocityY * (delta / 1000f));
        CurrentTileY = LocationYToCurrentTileY();
        CurrentTileX = LocationXToCurrentTileX();
        XPointInsideCurrentTile = LocationX % main.Global.TileWidth;
        YPointInsideCurrentTile = LocationY % main.Global.TileHeight;
        
    }
    
    /**
     * The final step in the Update process, now that we have all the status,
     * we can show the correct image to reflect what this Entity is doing.
     * @param delta
     */
    protected void ComputeAnimation(int delta) {
        
        //Update the animation being played and the hitbox that it uses.
        GetCurrentAnimation();
        EntityAnimation[CurrentAnimation].Update(delta);
        
    }
    
    /**
     * Display the Entity on the Screen in relation to wherever it should logically
     * be within the entire Level.
     * If the Entity is not within the Screen boundaries it will not be drawn.
     * This function can be overridden if there needs to be complex drawing done.
     * Things like a boss health bar can be drawn on screen by the Entity itself.
     * @param g Required graphics canvas
     * @param ScreenX Left side of the Screen location on the Level
     * @param ScreenY Top side of the Screen location on the Level
     */
    public void Draw(Graphics g, float ScreenX, float ScreenY) 
    {
        if (IsAlive && IsVisible)
        {
            //Determine where the Entity is in relation to the Top-Left of the Screen.
            ScreenLocationX = LocationX - ScreenX - main.Global.TileWidth - HitBoxOffsetX[CurrentAnimation];
            ScreenLocationY = LocationY - ScreenY - main.Global.TileHeight - HitBoxOffsetY[CurrentAnimation];

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
    
    
    /**
     * Determine if this Entity's Hitbox collides with another Entity's Hitbox.
     * Collision only counts if both Entities are alive.
     * @param EntityX
     * @param EntityY
     * @param EntityWidth
     * @param EntityHeight
     * @return 
     */
    public boolean CheckCollision(float EntityX, float EntityY, int EntityWidth, int EntityHeight)
    {
        if (IsAlive)
        {
            if (LocationY + HitBoxHeight[CurrentAnimation] > EntityY && LocationY < EntityY + EntityHeight)
            {
                if (LocationX + HitBoxWidth[CurrentAnimation] > EntityX && LocationX < EntityX + EntityWidth)
                {
                    //Collision detected
                    return true;
                }
            }
        }
          
        return false;
    }
    
    /**
     * Provided an index for a Hitbox, compute and save the smallest amount of
     * Tile space that the Hitbox will be able to fit into.
     * This will be used later during Collision Detection.
     * @param hitboxindex 
     */
    protected void SetMinimumTilesForHitBox(int hitboxindex)
    {
        //Determine the number of tiles that will be fully covered by this hitbox.
        //IE: if the box is 64 pixels tall, the tile height is 32 pixels tall, then 2 tiles are the minimum required to check on any tile collision case.
        MinimumYTilesToCheck[hitboxindex] = (int) Math.floor((HitBoxHeight[hitboxindex] * 1f) / main.Global.TileHeight);
        
        //If there is a little bit extra left over, where a tile would be partially filled, we also need to check that.
        if ((HitBoxHeight[hitboxindex] % main.Global.TileHeight) > 0)
        {
            MinimumYTilesToCheck[hitboxindex] += 1;
        }
        
        //Ensure that at least one tile is checked for a hitbox, no matter how small.
        if (MinimumYTilesToCheck[hitboxindex] < 1)
        {
            MinimumYTilesToCheck[hitboxindex] = 1;
        }
        
        //Repeat the same process for width
        MinimumXTilesToCheck[hitboxindex] = (int) Math.floor((HitBoxWidth[hitboxindex] * 1f) / main.Global.TileWidth);
        
        if ((HitBoxWidth[hitboxindex] % main.Global.TileWidth) > 0)
        {
            MinimumXTilesToCheck[hitboxindex] += 1;
        }
        
        if (MinimumXTilesToCheck[hitboxindex] < 1)
        {
            MinimumXTilesToCheck[hitboxindex] = 1;
        }
        
    }
    
    /**
     * Whenever an Entity Collides with another Entity, this will be called for
     * each one to perform whatever action is a result of that Collision.
     * Since this is the most generic form of Process, it should only be called
     * if this Entity would always perform the same actions based on what touches it. 
     * @param Entity
     */
    public void ProcessCollision(Entity Entity)
    {

    }
    
    /**
     * Performs whatever action is a result of this Entity Colliding with an Item.
     * @param Enemy 
     */
    public void ProcessCollision(Enemy Enemy) {
        ProcessCollision((Entity)Enemy);
    }
    
    /**
     * Performs whatever action is a result of this Entity Colliding with an Item.
     * @param Item 
     */
    public void ProcessCollision(Item Item)
    {
        ProcessCollision((Entity)Item);
    }
    
    /**
     * Performs whatever action is a result of this Entity Colliding with a Bullet.
     * @param Bullet
     */
    public void ProcessCollision(Bullet Bullet)
    {
        ProcessCollision((Entity)Bullet);
    }
    
    /**
     * Performs whatever action is a result of this Entity Colliding with an Object.
     * @param Object 
     */
    public void ProcessCollision(GameObject Object)
    {
        ProcessCollision((Entity)Object);
    }
    
    /**
     * Performs whatever action is a result of this Entity Colliding with a Player.
     * @param Player 
     */
    public void ProcessCollision(PlayerCharacter Player)
    {
        ProcessCollision((Entity)Player);
    }
    
    public void AddHealth(float value)
    {
        Health += value;
        if (Health < 1)
        {//None left, so kill character
            Health = 0;
            IsDying = true;
        }
        if (Health > HealthMax)
        {
            Health = HealthMax;
        }
    }
    
    public void SubtractHealth(float value)
    {
        Health -= value;
        if (Health < 1 && IsAlive)
        {//None left, so kill character
            Health = 0;
            IsDying = true;
        }
        if (Health > HealthMax)
        {
            Health = HealthMax;
        }
    }
      
    public void ChangeCurrentAnimation(int newanim, boolean isanimating)
    {
        if (!AnimationLock)
        {
            CurrentAnimation = newanim;
            if (PreviousAnimation != CurrentAnimation)
            {//reset the new animation's duration and frame location
                EntityAnimation[CurrentAnimation].Reset();
                PreviousAnimation = CurrentAnimation;
            }
        }
        EntityAnimation[CurrentAnimation].isAnimating = isanimating;
    }
    
    
    /*
     * This function will change the animation regardless of AnimationLock
     */
    public void SetAnimation(int value, boolean anim)
    {
        CurrentAnimation = value;
        if (PreviousAnimation != CurrentAnimation)
        {//reset the new animation's duration and frame location
            EntityAnimation[CurrentAnimation].Reset();
            PreviousAnimation = CurrentAnimation;
        }
    }
    
    /**
     * The third step in the Update process, it uses the Velocity that was previously
     * determined and checks if the expected movement results in collision with any
     * tiles.
     * This will set the values that say if the Entity can move in a direction,
     * and also say what special Tile Attributes are being touched this frame.
     * @param TileData
     * @param RoomData
     */
    protected void ComputeTileCollision(int[][][] TileData, int[][] RoomData) {
        
        //Determine tile based status
        CheckIfInRoomBoundaries(TileData, RoomData);
            
        if (TileCollisionEnabled)
        {
            CheckIfInAir(TileData);
            CheckIfTouchingCeiling(TileData);
        
            if (CheckHorizontalMovement(TileData) == 0)
            {
                if (IsTouchingEntityLeftWall && NextLocationX > LocationX)
                {
                    //Player is blocked by object. Do nothing.
                } else if (IsTouchingEntityRightWall && NextLocationX < LocationX) {
                    //Player is blocked by object. Do nothing.
                } else 
                {
                    LocationX = NextLocationX;
                }
            }
            else
            {
                //Problem with movement, ideal location was given as a float value
                if (IsTouchingEntityLeftWall && NextLocationX > LocationX)
                {
                    //Player is blocked by object. Do nothing.
                } else if (IsTouchingEntityRightWall && NextLocationX < LocationX) {
                    //Player is blocked by object. Do nothing.
                } else 
                {
                    LocationX = IdealX;
                }
            }

            //Check if the new vertical location is a valid one in relation to the tile map.
            //If so, move there.  If not, move ideally.
            if (CheckVerticalMovement(TileData) == 0)
            {
                if (IsTouchingEntityCeiling && NextLocationY < LocationY)
                {
                    //Player is blocked by object. Do nothing.
                } else if (IsTouchingEntityPlatform && NextLocationY > LocationY) {
                    //Player is blocked by object. Do nothing.
                } else 
                {
                    LocationY = NextLocationY;
                }
            }
            else
            {
                //Problem with movement, ideal location was given as a float value
                if (IsTouchingEntityCeiling && NextLocationY < LocationY)
                {
                    //Player is blocked by object. Do nothing.
                } else if (IsTouchingEntityPlatform && NextLocationY > LocationY) {
                    //Player is blocked by object. Do nothing.
                } else 
                {
                    LocationY = IdealY;
                }
            }
        }
        else
        {//No tile collision, so it's just free movement
            LocationX = NextLocationX;
            LocationY = NextLocationY;
        }
        
    }
    
    /**
     * 
     * Provided with the TileData for the entire level, this Entity will check
     * it's immediate surroundings to see if it will collide with a Tile in the
     * direction that it is currently moving.
     * If the Entity is not moving, then a check will still be performed.
     * For each tile that it collides with, the Entity will see what Special Tile
     * Attributes the Tile has, and set a flag denoting that it is touching that
     * type of Tile for later AI rules.
     * @param TileData The entire Level tile data for the game state.
     * @return Returns 0 if movement is unhindered in terms of distance travelled. Anything else is an offset to LocationY.
     */
    protected float CheckVerticalMovement(int[][][] TileData) 
    {
        IdealY = 0; //If 0, movement is unhindered.  All else is an offset to locationY
        
        //Determine the width of tiles that need to be checked.
        //The Entity may be touching more than one Tile when moving.
        TotalXTilesToCheck = MinimumXTilesToCheck[CurrentAnimation];
        if ((XPointInsideCurrentTile + HitBoxWidth[CurrentAnimation]) > MinimumXTilesToCheck[CurrentAnimation] * main.Global.TileWidth) {
            TotalXTilesToCheck++;
        }

        CanMoveVertical = true;
        
        if (NextLocationY > LocationY)
        {//Entity is moving down
            
            //Entity's Hitbox bottom edge will be on this tile if the move is successful
            DownTileToMoveToY = (int)((NextLocationY + HitBoxHeight[CurrentAnimation]) / main.Global.TileHeight);
            
            //Check the line of tiles that moving down will impact (a horizontal line on bottom of Entity)
            for (Ti = 0; Ti < TotalXTilesToCheck; Ti++) {
                //Check each special attribute for the tile, and set a flag for each one on the Entity.
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    TileValue = TileData[CurrentTileX + Ti][DownTileToMoveToY - 1][Tj];
                    
                    if (TileValue == 1)
                    {//Solid Tile. No movement is allowed through it.
                        CanMoveVertical = false;
                    } else if (TileValue == 16)
                    {//Animated tile on touch. Only triggers on vertical movement.
                        LevelState.Level.setTileAnimation(CurrentTileX + Ti, DownTileToMoveToY - 1, true);
                    }
                }
            }
            
            if (!CanMoveVertical) 
            {//Entity is prevented from moving downwards because a Tile restricts it.
                //See if partial movement is allowed so the Entity can get closer to the Tile, but not pass through it.
                //The Entity can touch the Solid Tile, so it's standing on it.
                IdealY = ((DownTileToMoveToY * main.Global.TileHeight) - HitBoxHeight[CurrentAnimation]);
            }
        }
        else if (NextLocationY < LocationY)
        {//Checking for moving up, same concepts apply
            
            //Entity's Hitbox top edge will be on this tile if the move is successful
            UpTileToMoveToY = (int) ((NextLocationY) / main.Global.TileHeight);
            
            //Check the line of tiles that moving down will impact (a horizontal line on bottom of Entity)
            for (Ti = 0; Ti < TotalXTilesToCheck; Ti++) {
                //Check each special attribute for the tile, and set a flag for each one on the Entity.
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    TileValue = TileData[CurrentTileX + Ti][UpTileToMoveToY - 1][Tj];
                    
                    if (TileValue == 1)
                    {//Solid Tile. No movement is allowed through it.
                        CanMoveVertical = false;
                    }
                    else if (TileValue == 16)
                    {//Animated tile on touch. Only triggers on vertical movement.
                        LevelState.Level.setTileAnimation(CurrentTileX + Ti, DownTileToMoveToY - 1, true);
                    }
                }
            }
            
            if (!CanMoveVertical) 
            {//Entity is prevented from moving upwards because a Tile restricts it.
                //See if partial movement is allowed so the Entity can get closer to the Tile, but not pass through it.
                //The Entity can touch the Solid Tile, so it's standing on it.
                IdealY = (int) ((UpTileToMoveToY * main.Global.TileHeight) + (main.Global.TileHeight));
            }
        }
        else
        {//Player isn't moving at all. If Gravity applies to this object, see
            //if it changes anything.
            
            //Entity's Hitbox bottom edge will be on this tile if the move is successful
            DownTileToMoveToY = (int)((NextLocationY + HitBoxHeight[CurrentAnimation]) / main.Global.TileHeight);
            
            //Check the line of tiles that moving down will impact (a horizontal line on bottom of Entity)
            for (Ti = 0; Ti < TotalXTilesToCheck; Ti++) {
                //Check each special attribute for the tile, and set a flag for each one on the Entity.
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    TileValue = TileData[CurrentTileX + Ti][DownTileToMoveToY - 1][Tj];
                    if (TileValue == 1)
                    {//Solid Tile. No movement is allowed through it.
                        
                    } else if (TileValue == 16)
                    {//Animated tile on touch. Only triggers on vertical movement.
                        LevelState.Level.setTileAnimation(CurrentTileX + Ti, DownTileToMoveToY - 1, true);
                    }
                }
            }
        }
        
        //Return the allowed distance of the Entity. If 0, then there is no restriction.
        //Anything else will be within a range of 1-32, so that the Entity can get
        //closer to a Blocked Tile.
        return IdealY;
    }
    
    
    /**
     * 
     * Provided with the TileData for the entire level, this Entity will check
     * it's immediate surroundings to see if it will collide with a Tile in the
     * direction that it is currently moving.
     * If the Entity is not moving, then a check will still be performed.
     * For each tile that it collides with, the Entity will see what Special Tile
     * Attributes the Tile has, and set a flag denoting that it is touching that
     * type of Tile for later AI rules.
     * Code comments can be found in CheckVerticalMovement() for an explaination.
     * @param TileData The entire Level tile data for the game state.
     * @return Returns 0 if movement is unhindered in terms of distance travelled. Anything else is an offset to LocationX.
     */
    protected float CheckHorizontalMovement(int[][][] TileData) 
    {
        IdealX = 0;
        
        TotalYTilesToCheck = MinimumYTilesToCheck[CurrentAnimation];
        if ((YPointInsideCurrentTile + HitBoxHeight[CurrentAnimation]) > MinimumYTilesToCheck[CurrentAnimation] * main.Global.TileHeight) {
            TotalYTilesToCheck++;
        }

        CanMoveHorizontal = true;
        
        if (NextLocationX > LocationX)
        {//Character wants to move right
            
            RightTileToMoveToX = (int)((NextLocationX + HitBoxWidth[CurrentAnimation]) / main.Global.TileWidth);
            
            if (IsBeyondRoomCeiling)
            {
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    if (TileData[RightTileToMoveToX - 1][CurrentRoomBoundaries[0]][Tj] == 1)
                    {//Solid Tile
                        CanMoveHorizontal = false;
                    }
                }
            } else if (IsBeyondRoomFloor)
            {
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    if (TileData[RightTileToMoveToX - 1][CurrentRoomBoundaries[1]][Tj] == 1)
                    {//Solid Tile
                        CanMoveHorizontal = false;
                    }
                }
            } else if (IsBeyondRoomRight)
            {//Can't move beyond the room boundaries.
                CanMoveHorizontal = false;
            } else
            {//Normal checks: Entity is within room safely.
                
                for (Ti = 0; Ti < TotalYTilesToCheck; Ti++) 
                {
                    for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                    {
                        if (TileData[RightTileToMoveToX - 1][CurrentTileY + Ti][Tj] == 1)
                        {//Solid Tile
                            CanMoveHorizontal = false;
                        }
                    }

                }
            }

            if (!CanMoveHorizontal) 
            {//Entity is prevented from moving right because a Tile restricts it.
                //See if partial movement is allowed so the Entity can get closer to the Tile, but not pass through it.
                //The Entity can touch the Solid Tile, so it's standing on it.
                IdealX = ((RightTileToMoveToX * main.Global.TileWidth) - HitBoxWidth[CurrentAnimation]);
            }
        } 
        else if (NextLocationX < LocationX) 
        {//Moving left
            
            LeftTileToMoveToX = (int) (((NextLocationX) / main.Global.TileWidth));
            if (IsBeyondRoomCeiling)
            {
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    if (TileData[LeftTileToMoveToX - 1][CurrentRoomBoundaries[0]][Tj] == 1)
                    {//Solid Tile
                        CanMoveHorizontal = false;
                    }
                }
            } else if (IsBeyondRoomFloor)
            {
                for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                {
                    if (TileData[LeftTileToMoveToX - 1][CurrentRoomBoundaries[1]][Tj] == 1)
                    {//Solid Tile
                        CanMoveHorizontal = false;
                    }
                }
            } else if (IsBeyondRoomLeft)
            {//Can't move beyond the room boundaries.
                CanMoveHorizontal = false;
            } else
            {//Normal checks: Entity is within room safely.
                
                for (Ti = 0; Ti < TotalYTilesToCheck; Ti++) 
                {
                    for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                    {
                        if (TileData[LeftTileToMoveToX - 1][CurrentTileY + Ti][Tj] == 1)
                        {//Solid Tile
                            CanMoveHorizontal = false;
                        }
                    }
                }
            }
            if (!CanMoveHorizontal) 
            {//Entity is prevented from moving left because a Tile restricts it.
                //See if partial movement is allowed so the Entity can get closer to the Tile, but not pass through it.
                //The Entity can touch the Solid Tile, so it's standing on it.
                IdealX = (int) ((LeftTileToMoveToX * main.Global.TileWidth) + (main.Global.TileWidth));
            }
        }
        else
        {//Character isn't moving at all: determine an arbitrary direction based on where it's looking.
            
            if (DirectionHorizontal == Directions.Left)
            {
                LeftTileToMoveToX = (int) (((NextLocationX) / main.Global.TileWidth));
                
                for (Ti = 0; Ti < TotalYTilesToCheck; Ti++) 
                {
                    for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                    {
                        if (TileData[LeftTileToMoveToX - 1][CurrentTileY + Ti][Tj] == 1)
                        {//Solid Tile
                            CanMoveHorizontal = false;
                        }
                    }
                }
            } else {
                RightTileToMoveToX = (int)((LocationX + HitBoxWidth[CurrentAnimation] - 1) / main.Global.TileWidth);
                
                for (Ti = 0; Ti < TotalYTilesToCheck; Ti++) 
                {
                    for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
                    {
                        if (TileData[RightTileToMoveToX - 1][CurrentTileY + Ti][Tj] == 1)
                        {//Solid Tile
                            
                        }
                    }
                }
            }
        }
        
        return IdealX;
    }
    
    /**
     * Special case Collision detection: Check if the Entity is touching (or 1 pixel away)
     * from a Tile directly above it. If so, the Entity is basically touching the 
     * inverse ground.
     * Used for determining when to cut the velocity of a jump, or if the Entity
     * likes to climb on ceilings, it knows if it's walking on the ceiling.
     * This is a smaller section of the CheckVerticalMovement() call, where the Entity
     * is moving upwards. However, this checks an additional 1px distance.
     * @param TileData
     * @return 
     */
    protected boolean CheckIfTouchingCeiling(int[][][] TileData)
    {
        TotalXTilesToCheck = MinimumXTilesToCheck[CurrentAnimation];
        if ((XPointInsideCurrentTile + HitBoxWidth[CurrentAnimation]) > MinimumXTilesToCheck[CurrentAnimation] * main.Global.TileWidth) {
                TotalXTilesToCheck++;
        }
        
        IsTouchingCeiling = false;
        
        //Character's top edge of hitbox will be on this tile
        UpTileToMoveToY = (int) ((LocationY - 1) / main.Global.TileHeight);
        for (Ti = 0; Ti < TotalXTilesToCheck; Ti++) {
            for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
            {
                TileValue = TileData[CurrentTileX + Ti][UpTileToMoveToY - 1][Tj];
                if (TileValue == 1)//if tile is blocked
                {
                    IsTouchingCeiling = true;
                }
                
            }
        }
        
        return IsTouchingCeiling;
    }
        
    /**
     * Determine if the Entity is Colliding with any Solid Tiles directly below it.
     * This determines if the Entity is standing, or floating.
     * @param TileData
     * @return 
     */
    protected boolean CheckIfInAir(int[][][] TileData)
    {
        TotalXTilesToCheck = MinimumXTilesToCheck[CurrentAnimation];
        if (XPointInsideCurrentTile + HitBoxWidth[CurrentAnimation] > MinimumXTilesToCheck[CurrentAnimation] * main.Global.TileWidth) {
            TotalXTilesToCheck++;
        }

        IsMidAir = true;
        //Checking for moving down.
        DownTileToMoveToY = (int) ((LocationY + HitBoxHeight[CurrentAnimation]) / main.Global.TileHeight);
        for (Ti = 0; Ti < TotalXTilesToCheck; Ti++) {
            for (Tj = 0; Tj < main.Global.TileAttributesMax; Tj++)
            {
                TileValue = TileData[CurrentTileX + Ti][DownTileToMoveToY - 1][Tj];
                if (TileValue == 1)
                {//Solid Tile
                    IsMidAir = false;
                }
            }
        }
        
        //Being on an object acts as a temporary ground
        if (IsTouchingEntityPlatform)
        {
            IsMidAir = false;
        }
        
        return IsMidAir;
    }
    
    /**
     * Check each side of the Entity and see which direction, if any, the 
     * Entity is outside of the provided room boundaries.
     * The Entity will use the edge tile that it is closest to when checking
     * collisions if it is off screen.
     * @param TileData
     * @param RoomData 
     */
    protected void CheckIfInRoomBoundaries(int[][][] TileData, int[][] RoomData)
    {
        IsBeyondRoomCeiling = false;
        IsBeyondRoomFloor = false;
        IsBeyondRoomLeft = false;
        IsBeyondRoomRight = false;
        IsOffScreen = false;
        
        CurrentRoomBoundaries = LevelState.getCurrentRoomBoundaries();
        
        //Check if any aspect is offscreen
        if (
                (LocationY < ((CurrentRoomBoundaries[0] + 1) * main.Global.TileHeight)) || 
                (LocationY + HitBoxHeight[CurrentAnimation] > ((CurrentRoomBoundaries[1] + 1) * main.Global.TileHeight) + main.Global.TileHeight) || 
                (LocationX < ((CurrentRoomBoundaries[2]) * main.Global.TileWidth) + main.Global.TileWidth) ||
                (LocationX + HitBoxWidth[CurrentAnimation] > ((CurrentRoomBoundaries[3] + 1) * main.Global.TileWidth) + main.Global.TileWidth)
           )
        {
            //At least one part is off screen.  Check for any room boundary limits.
            IsOffScreen = true;
            //Check if player is above the room ceiling
            if (LocationY< (CurrentRoomBoundaries[0] * main.Global.TileHeight) + main.Global.TileHeight)
            {
                IsBeyondRoomCeiling = true;
            }
            //Check if player is below the floor
            if (LocationY + HitBoxHeight[CurrentAnimation] > (CurrentRoomBoundaries[1] * main.Global.TileHeight))
            {
                IsBeyondRoomFloor = true;
            }
            //Check if past the left wall
            if (LocationX < (CurrentRoomBoundaries[2] * main.Global.TileWidth) + main.Global.TileWidth)
            {
                IsBeyondRoomLeft = true;
            }
            //Check if past right wall
            if (LocationX + HitBoxWidth[CurrentAnimation] > (CurrentRoomBoundaries[3] * main.Global.TileWidth))
            {
                IsBeyondRoomRight = true;
            }
        }
    }
    
    /**
     * If this Entity cares about having its movement stopped if it collides with
     * a platform, ceiling, or wall like Entity, then it will run this check
     * when it collides with an Entity.
     * @param Entity 
     */
    protected void ProcessCollisionEntityWalls(Entity Entity) {
    //Neutral effects like velocity/location changes follow here
        //They effect the character but are neight hostile nor friendly.
        if (Entity.IsLeftWall)
        {
            if (LocationX + HitBoxWidth[CurrentAnimation] < Entity.LocationX + 2)
            {
                IsTouchingEntityLeftWall = true;
            }
        }
        
        if (Entity.IsRightWall) 
        {
            if (LocationX > Entity.LocationX + (Entity.HitBoxWidth[Entity.CurrentAnimation] - 2))
            {
                IsTouchingEntityRightWall = true;
            }
        }

        if (Entity.IsCeiling)
        {
            //If this Entity hits the bottom of the other Entitys Hitbox (with some forgiveness),
            //then this Entity is technically touching a ceiling.
            if (LocationY > Entity.LocationY + (Entity.HitBoxHeight[Entity.CurrentAnimation] - 2))
            {
                IsTouchingEntityCeiling = true;
            }
        }
  
        if (Entity.IsPlatform) {
            //If this Entity hits the top of the other Entitys Hitbox (with some forgiveness),
            //then this Entity is technically touching a floor and is not MidAir.
            if (LocationY + HitBoxHeight[CurrentAnimation] < Entity.LocationY + 2)
            {
                IsTouchingEntityPlatform = true;
            }
        }
    }
    
    /**
     * Returns true if ANY part of the character is off screen.
     * Similar to Room Boundaries, except room boundaries require a complete
     * edge offscreen.
     * @return 
     */
    public boolean IsOffScreen()
    {
        return IsOffScreen;
    }
    
    /**
     * Envoke this to cause the Entity to perform a damage blink animation.
     * The duration and speed can be altered by each Entity.
     * HitStun does not mean the Entity cannot be damaged: that logic must be 
     * implemented on the specific Entity itself.
     */
    protected void TriggerHitStun()
    {
        IsInHitStun = true;
        HitStunTimer.Reset();
        HitStunBlinkIntervalTimer.Reset();
        main.Global.Sound.PlaySound(0);
    }
    
    protected void RemoveHitStun() {
        IsInHitStun = false;
        IsVisible = true;
    }
    
    /**
     * Return the pixel Location X of this Entity's center.
     * @return 
     */
    public float ComputeHitBoxCenterX() {
        return LocationX + (HitBoxWidth[CurrentAnimation] / 2);
    }
    
    /**
     * Return the pixel Location Y of this Entity's center.
     * @return 
     */
    public float ComputeHitBoxCenterY() {
        return LocationY + (HitBoxHeight[CurrentAnimation] / 2);
    }
    
    public float ComputeHitBoxRightSide() {
        return LocationX + HitBoxWidth[CurrentAnimation];
    }
    
    public float ComputeHitBoxBottomSide() {
        return LocationY + HitBoxHeight[CurrentAnimation];
    }
}
