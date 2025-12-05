import bagel.Image;
import bagel.util.Point;

/**
 * Object in the game that the player can shoot at to collect coins
 */
public class Basket{
    private final Point position;
    private final Image image;
   // private final double coinValue;
    private boolean active = true;

    /**
     * Constructor to initialize basket within the scene
     * @param pos Position that the basket is initialized at
     */
    public Basket(Point pos){
        this.image= new Image("res/basket.png");
        this.position=pos;
    }

    /**
     * Update the basket in the scene every frame
     * @param player Player from the scene
     */
    public void update(Player player){
        if(active){
            //Ensure player are unable to pass through it
            if (hasCollidedWithPlayer(player)) {
                // set the player to its position prior to attempting to move through this wall
                player.move(player.getPrevPosition().x, player.getPrevPosition().y);
            }

            //Iterate through each bullets shot by player
            for(Bullets ammo:player.getAmmunition()){
                //Destroy ammo when it's detected to be in collision with the wall
                if (hasCollidedWithProjectile(ammo)) {
                    ammo.setOnScene(false);

                    //Earn some coins
                    double basketCoins= Double.parseDouble(ShadowDungeon.gameProps.getProperty("basketCoin"));
                    player.earnCoins(basketCoins,false);
                    active=false;
                }
            }
        }

    }

    /**
     * Draw the basket
     */
    public void Draw(){image.draw(position.x,position.y);}

    /**
     * Check if it has been shot by the player
     * @param bullet Player's ammunition
     * @return boolean
     */
    public boolean hasCollidedWithProjectile(Bullets bullet) {
        return image.getBoundingBoxAt(position).intersects(bullet.getCurrImage().getBoundingBoxAt(bullet.getPosition()));
    }

    /**
     * Check if it's image has collided with the player image
     * @param player To get player's image
     * @return  boolean
     */
    public boolean hasCollidedWithPlayer(Player player) {
        return image.getBoundingBoxAt(position).intersects(player.getCurrImage().getBoundingBoxAt(player.getPosition()));
    }

    /**
     * Check if it's active or not
     * @return  boolean
     */
    public boolean getBasketActive(){
        return active;
    }
}
