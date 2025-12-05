import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

/**
 * Chest that can be unlocked by the player to earn coins
 */
public class TreasureBox extends Obstacles{
    private final Point position;
    private final Image image;
    private final double coinValue;
    private boolean active = true;
    private BattleRoom battleRoomAccess;

    /**
     * Spawn treasure box into the scene
     * @param position  Treasure Box position
     * @param coinValue Value that'll be spawned into the scene.
     * @param battleRoom Which room the treasure box is located in
     */
    public TreasureBox(Point position, double coinValue,BattleRoom battleRoom) {
        this.position = position;
        this.coinValue = coinValue;
        this.image = new Image("res/treasure_box.png");
        this.battleRoomAccess= battleRoom;
    }

    /**
     * Secondary update function with 1 parameter, Update treasurebox every frame
     * @param player Player from the scene
     */
    public void update(Player player) {
        //Update every frame
    }

    private void projectileCollisions(Input input,Player player){
        //Iterate through each bullets shot by player
        for(Bullets ammo:player.getAmmunition()){
            //Destroy ammo when it's detected to be in collision with the wall
            if (hasCollidedWithProjectile(ammo)) {
                ammo.setOnScene(false);
            }
        }
    }

    /**
     * Draw the TreasureBox
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Primary update function with 2 parameter, Update treasurebox every frame
     * @param input Input from the scene
     * @param player player from the scene
     */
    public void update(Input input, Player player) {
        //Check if player is able to open the treasure chest or not
        if (hasCollidedWithPlayer(player) && input.wasPressed(Keys.K)&&player.getKey()>0) {
            player.earnCoins(coinValue,false);
            active = false;
            player.setKeyCount(-1); //Remove a key
        }

        //Bullets
        projectileCollisions(input,player);

        //Iterate through each projectile shot by the enemies
        for(FireBall fireballs:BattleRoom.allFireBalls){
            if(hasCollidedWithProjectile(fireballs)){
                fireballs.setOnScene(false);
            }
        }
    }

    /**
     * Check if it has collided with player
     * @param player Player from the scene
     * @return boolean
     */
    public boolean hasCollidedWithPlayer(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * Check if it has collided with projectiles
     * @param projectile ammo or enemy fireballs
     * @return boolean
     */
    @Override
    public boolean hasCollidedWithProjectile(Projectile projectile) {
        return image.getBoundingBoxAt(position).intersects(projectile.getCurrentImage().getBoundingBoxAt(
                projectile.getPosition()));
    }

    /**
     * Check if it's active within the scene
     * @return boolean
     */
    public boolean isActive() {
        return active;
    }
}