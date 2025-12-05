import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Room where the game starts
 */
public class PrepRoom extends Room {
    private Player player;
    private Door door;
    private RestartArea restartArea;
    private Image robotDisplay;
    private Image marineDisplay;
    private Point display[];
    private boolean stopCurrentUpdateCall = false; // this determines whether to prematurely stop the update execution
    private ArrayList<Enemies> enemy;

    /**
     * Initialize entities into the prep room
     * @param gameProperties Get gameProperties from ShadowDungeon
     * @param messageProperties Get messageProperties from ShadowDungeon
     */

    public void initEntities(Properties gameProperties, Properties messageProperties) {
        display=new Point[2];//Declare array for display
        // find the configuration of game objects for this room
        for (Map.Entry<Object, Object> entry: gameProperties.entrySet()) {
            String roomSuffix = String.format(".%s", ShadowDungeon.PREP_ROOM_NAME);
            if (entry.getKey().toString().contains(roomSuffix)) {
                String objectType = entry.getKey().toString().substring(0, entry.getKey().toString().length() - roomSuffix.length());
                String propertyValue = entry.getValue().toString();
                switch (objectType) {
                    case "door":
                        String[] coordinates = propertyValue.split(",");
                        door=new Door(IOUtils.parseCoords(propertyValue),coordinates[2],this);
                        break;
                    case "restartarea":
                        restartArea = new RestartArea(IOUtils.parseCoords(propertyValue));
                        break;
                    case "Robot":
                        robotDisplay=new Image("res/robot_sprite.png");
                        this.display[0]=IOUtils.parseCoords(propertyValue);
                        break;
                    case "Marine":
                        marineDisplay=new Image("res/marine_sprite.png");
                        this.display[1]=IOUtils.parseCoords(propertyValue);
                        break;
                    default:
                }
            }
        }
    }

    /**
     * Update the EndRoom every single frame
     * @param input Input from ShadowDungeon
     */
    public void update(Input input) {
        //Draw items
        Draw();

        // update and draw all game objects in this room
        if(!ShadowDungeon.hasPaused){
            door.update(player);

            if (stopUpdatingEarlyIfNeeded()) {
                return;
            }

            restartArea.update(input, player);

            if (player != null) {
                player.update(input);
            }

            // door unlock mechanism
            //Once character are selected it'll unlockddd
            if (input.wasPressed(Keys.R) ||input.wasPressed(Keys.M) && !findDoor().isUnlocked()) {
                findDoor().unlock(false);
            }
        }else{
            //Buy health
            if(input.isDown(Keys.E)){
                player.BuyHealth();
            }

            //Upgrade weapons
            if(input.isDown(Keys.L)){
                player.BuyWeapons();
            }
        }
    }

    private void Draw(){
        UserInterface.drawStartMessages();

        door.draw();
        //Display robot and marine sprite

        robotDisplay.draw(display[0].x,display[0].y);
        marineDisplay.draw(display[1].x,display[1].y);

        //Restart area
        restartArea.draw();

        //Player
        player.draw();


        //Draw player munitions
        //Draw player projectile
        for(Projectile projectile:player.getAmmunition()){
            if(projectile.getOnScene()){
                projectile.Draw();
            }
        }

        //Draw store menu if paused
        if(ShadowDungeon.hasPaused){
            ShadowDungeon.Paused();
        }
    }

    private boolean stopUpdatingEarlyIfNeeded() {
        if (stopCurrentUpdateCall) {
            player = null;
            stopCurrentUpdateCall = false;
            return true;
        }
        return false;
    }

    /**
     * Get the player from ShadowDungeon and set them up within this scene
     * @param player Player from ShadowDungeon
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Stop current update call
     */
    public void stopCurrentUpdateCall() {
        stopCurrentUpdateCall = true;
    }

    /**
     * Find door that leads to this room
     * @return
     */
    public Door findDoor() {
        return door;
    }

    /**
     * Determines the next room the player goes to when entering the door
     * @return Door
     */
    public Door findDoorByDestination() {
        return door;
    }

    /**
     * Getters used to get list of enemies within the scene
     * @return ArrayList<Enemies>
     */
    public ArrayList<Enemies> getEnemies(){
        return enemy;
    }
}
