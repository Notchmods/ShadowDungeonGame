import bagel.Image;
import bagel.util.Point;

/**
 * Obstacle that blocks the player from moving through it
 */
public class Wall extends Obstacles {
    private final Point position;
    private final Image image;
    private Room roomAccess;

    /**
     * Constructor to initialize wall within the scene
     * @param position  Position of the wall
     * @param rooms Room that it will spawn at
     */
    public Wall(Point position,Room rooms) {
        this.position = position;
        this.image = new Image("res/wall.png");
        this.roomAccess = rooms;
    }

    /**
     * Update the wall to block things off
     * @param player block player off
     */
    @Override
    public void update(Player player) {
        //Collision with player
        if (hasCollidedWithPlayer(player)) {
            // set the player to its position prior to attempting to move through this wall
            player.move(player.getPrevPosition().x, player.getPrevPosition().y);
        }

        //Iterate through each projectile shot by player and Enemies

        //Player ammo
        for(Bullets ammo:player.getAmmunition()){
            //Destroy ammo when it's detected to be in collision with the wall
            if (hasCollidedWithProjectile(ammo)) {
                ammo.setOnScene(false);
            }
        }

        //Fireballs
        //Iterate through each enemies first
        if(roomAccess.getEnemies()!=null) {
            //Iterate through each projectile shot by the enemies
            for (FireBall fireballs:BattleRoom.allFireBalls) {
                if (hasCollidedWithProjectile(fireballs)) {
                    fireballs.setOnScene(false);
                }
            }
        }


    }

    /**
     * Draw the wall
     */
    public void draw() {
        image.draw(position.x, position.y);
    }

    /**
     * Check if it has collided with player image
     * @param player to get player image
     * @return boolean
     */
    public boolean hasCollidedWithPlayer(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * Check if it has collided with projectile image
     * @param projectile ammunition
     * @return Image
     */
    @Override
    public boolean hasCollidedWithProjectile(Projectile projectile) {
        return image.getBoundingBoxAt(position).intersects(projectile.getCurrentImage().getBoundingBoxAt(projectile.getPosition()));
    }

    /**
     * Get the current wall image
     * @return Image
     */
    public Image getCurrentImage(){
        return image;
    }
}