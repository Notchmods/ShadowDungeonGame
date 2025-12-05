import bagel.Image;
import bagel.util.Point;


public class key {
    private Point position;
    private final Image keyImage;
    private boolean active;
    private Player player;
    private BattleRoom battleRoom;

    /**
     * Initialize keys within the scene
     * @param enemyPos KeyBulletKinPos
     * @param players Player from scene
     * @param battleRooms Which BattleRoom it's at
     */
    public key(Point enemyPos,Player players,BattleRoom battleRooms){
        this.keyImage=new Image("res/key.png");
        this.player= players;
        this.battleRoom=battleRooms;
    }

    /**
     * Update the key to detect for collision and change its position based on keyBulletkIn
     * @param enemyPos KeyBulletKin position
     */
    public void update(Point enemyPos){
        this.position=enemyPos;
        //If it had collided with player then it'll dissapear
        if(hasCollidedWithPlayer(player)){
            player.setKeyCount(1);
            active=false;
            battleRoom.setCollected(true);
        }
    }

    /**
     * Draw key
     * @param enemyPos Draw it based on KeyBulletKin pos
     */
    public void Draw(Point enemyPos){
        if(active){
            keyImage.draw(enemyPos.x,enemyPos.y);
        }
    }

    /**
     * Activation status
     * @return boolean
     */
    public boolean isActive(){
        return active;
    }


    /**
     * Set the key to active
     * @param activeStat activation status
     */
    public void setActive(boolean activeStat){
        active=activeStat;
    }

    /**
     * Check if the key has collided with player
     * @param player player from scene
     * @return boolean
     */
    public boolean hasCollidedWithPlayer(Player player) {
        return keyImage.getBoundingBoxAt(position).intersects(player.getCurrImage().
                getBoundingBoxAt(player.getPosition()));
    }

}
