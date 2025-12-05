import bagel.Image;
import bagel.util.Point;

public class Table  extends Obstacles{
    private final Point position;
    private final Image image;
    // private final double coinValue;
    private boolean active = true;
    private Player players;
    private Room roomAccess;

    /**
     * Spawn table into the scene
     * @param point Position of the table at the scene
     * @param rooms Determines which room the table is in
     */
    public Table(Point point,Room rooms) {
        this.position=point;
        this.image= new Image("res/table.png");
        this.roomAccess = rooms;
    }

    /**
     * Update the table every frame to detect collision or activation
     * @param player player from scene
     */
    public void update(Player player){
        this.players=player;
        //Iterate through each bullets shot by player
        for(Bullets ammo:player.getAmmunition()){
            //Destroy ammo when it's detected to be in collision with the wall
            if (hasCollidedWithProjectile(ammo)) {
                ammo.setOnScene(false);
                active=false;
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

        //Ensure player are unable to pass through it
        //Collision with player
        if (hasCollidedWithPlayer(player)) {
            // set the player to its position prior to attempting to move through this wall
            player.move(player.getPrevPosition().x, player.getPrevPosition().y);
        }
    }

    /**
     * Draw table within the scene
     */
    public void Draw(){
        image.draw(position.x,position.y);
    }

    /**
     * Check if it has collided with projectiles
     * @param projectile ammo or enemy fireballs
     * @return boolean
     */
    public boolean hasCollidedWithProjectile(Projectile projectile) {
        return image.getBoundingBoxAt(position).intersects(projectile.getCurrentImage().getBoundingBoxAt(projectile.getPosition()));
    }

    /**
     * Check if it has collided with player
     * @param player Player from the scene
     * @return boolean
     */
    public boolean hasCollidedWithPlayer(Player player){
        return image.getBoundingBoxAt(position).intersects(players.getCurrImage().getBoundingBoxAt(players.getPosition()));
    }

    /**
     * Getters to get if the table are active or not
     * @return boolean
     */
    public boolean getActive(){
        return active;
    }
}
