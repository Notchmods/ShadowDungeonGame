import bagel.Image;
import bagel.util.Point;

/**
 * Door which can be locked or unlocked, allows the player to move to the room it's connected to
 */
public class Door extends Obstacles{
    private final Point position;
    private Image image;
    public final String toRoomName;
    public BattleRoom battleRoom; // only set if this door is inside a Battle Room
    private Room roomAccess;
    private boolean unlocked = false;
    private boolean justEntered = false; // when the player only just entered this door's room
    private boolean shouldLockAgain = false;

    private static final Image LOCKED = new Image("res/locked_door.png");
    private static final Image UNLOCKED = new Image("res/unlocked_door.png");

    /**
     * Initialize the door into the scene
     * @param position  Determines the position within the scene
     * @param toRoomName Determine which room the door leads to (String)
     * @param rooms Determine which room the door is located in. (String)
     */
    public Door(Point position, String toRoomName, Room rooms) {
        this.position = position;
        this.image = LOCKED;
        this.toRoomName = toRoomName;
        this.roomAccess = rooms;
    }

    /**
     * Second constructor to initialize door into scene with more parameters
     * @param position Determines the position within the scene
     * @param toRoomName Determine which room the door leads to (String)
     * @param rooms Determine which room the door is located in. (String)
     * @param battleRoom Current BattleRoom
     */
    public Door(Point position, String toRoomName, Room rooms,BattleRoom battleRoom) {
        this.position = position;
        this.image = LOCKED;
        this.toRoomName = toRoomName;
        this.battleRoom = battleRoom;
        this.roomAccess = rooms;
    }

    /**
     * Update the door each frame
     * @param player Get the player from the scene
     */
    public void update(Player player) {
        //Handles collision with player
        if (hasCollidedWithPlayer(player)) {
            onCollideWith(player);
        } else {
            onNoLongerCollide();
        }

        //Collision with projectiles
        collisionWithAmmo(player);
    }

    /**
     * Draw the door
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Unlock the door when the player is standing ontop of it during entrance
     * @param justEntered Check if the player had just entered the door
     */
    public void unlock(boolean justEntered) {
        unlocked = true;
        image = UNLOCKED;
        this.justEntered = justEntered;
    }

    /**
     * Check if the door has collided with player
     * @param player player from the current scene
     * @return boolean
     */
    public boolean hasCollidedWithPlayer(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * Collision detection with projectile
     * @param projectile Projectile whether it'd be bullets or fireball
     * @return boolean
     * @return boolean
     */
    public boolean hasCollidedWithProjectile(Projectile projectile) {
        return image.getBoundingBoxAt(position).intersects(projectile.getCurrentImage().getBoundingBoxAt(projectile.getPosition()));
    }


    /**
     * Set the door to lock
     */
    public void lock() {
        unlocked = false;
        image = LOCKED;
    }

    /**
     * Ensure that ammunition dissapears when collided with ammunition shot by player
     * @param player Player character
     */
    public void collisionWithAmmo(Player player){
        //Iterate through each bullets shot by player
        for(Bullets ammo:player.getAmmunition()){
            //Destroy ammo when it's detected to be in collision with the wall
            if (hasCollidedWithProjectile(ammo)) {
                ammo.setOnScene(false);
            }
        }

        //Fireballs
        //Iterate through each enemies first
        if(roomAccess!=null){
            if(roomAccess.getEnemies()!=null){
                for(Enemies enemies:roomAccess.getEnemies()){
                    //Iterate through each projectile shot by the enemies
                    for(FireBall fireballs:BattleRoom.allFireBalls){
                        if(hasCollidedWithProjectile(fireballs)){
                            fireballs.setOnScene(false);
                        }
                    }
                }
            }
        }

    }

    /**
     * Check if the door is unlocked or not
     * @return boolean
     */
    public boolean isUnlocked() {
        return unlocked;
    }

    /**
     * Set the door to lock again when the player has entered the room and stepped off the door
     */
    public void setShouldLockAgain() {
        this.shouldLockAgain = true;
    }

    /**
     * Getters to get the door's current position
     * @return Point
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Getters to get the current image of the door
     * @return Image
     */
    public Image getCurrentImage(){
        return image;
    }

    /**
     * Getters to get if the player just entered the door
     * @return boolean
     */
    public boolean getJustEntered(){
        return justEntered;
    }

    private void onCollideWith(Player player) {
        // when the player only just entered this door's room, overlapping with the unlocked door shouldn't trigger room transition
        if (unlocked && !justEntered) {
            ShadowDungeon.changeRoom(toRoomName);
        }
        if (!unlocked) {
            player.move(player.getPrevPosition().x, player.getPrevPosition().y);
        }
    }

    private void onNoLongerCollide() {
        // when the player only just moved away from the unlocked door after walking through it
        if (unlocked && justEntered) {
            justEntered = false;

            // Battle Room activation conditions
            if (shouldLockAgain && battleRoom != null && !battleRoom.isComplete()) {
                unlocked = false;
                image = LOCKED;
                if(!battleRoom.isComplete()){
                    battleRoom.activateEnemies();
                }

            }
        }
    }

}

