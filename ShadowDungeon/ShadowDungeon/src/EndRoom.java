import bagel.Input;
import bagel.Keys;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Room where the game ends when the player either completes all rooms or dies
 */
public class EndRoom extends Room {
    private Player player;
    private Door door;
    private RestartArea restartArea;
    private boolean isGameOver = false;
    private boolean stopCurrentUpdateCall = false; // this determines whether to prematurely stop the update execution
    private ArrayList<Enemies> enemies;

    /**
     *  Initialize entities within EndERoom
     * @param gameProperties GameProps from ShadowDungeon
     */
    public void initEntities(Properties gameProperties) {
        // find the configuration of game objects for this room
        for (Map.Entry<Object, Object> entry: gameProperties.entrySet()) {
            String roomSuffix = String.format(".%s", ShadowDungeon.END_ROOM_NAME);
            if (entry.getKey().toString().contains(roomSuffix)) {
                String objectType = entry.getKey().toString().substring(0, entry.getKey().toString().length() - roomSuffix.length());
                String propertyValue = entry.getValue().toString();

                switch (objectType) {
                    case "door":
                        String[] coordinates = propertyValue.split(",");
                        door = new Door(IOUtils.parseCoords(propertyValue), coordinates[2],this);
                        break;
                    case "restartarea":
                        restartArea = new RestartArea(IOUtils.parseCoords(propertyValue));
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
        UserInterface.drawEndMessage(!isGameOver);

        //Draw items
        Draw();

        // door should be locked if player got to this room by dying
        if(!ShadowDungeon.hasPaused){
            if (isGameOver) {
                findDoor().lock();
            }

            // update and draw all game objects in this room
            door.update(player);
            if (stopUpdatingEarlyIfNeeded()) {
                return;
            }

            restartArea.update(input, player);

            if (player != null) {
                player.update(input);
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

        door.draw();
        restartArea.draw();
        player.draw();

        //Draw player projectile
        for(Projectile projectile:player.getAmmunition()){
            if(projectile.getOnScene()){
                projectile.Draw();
            }
        }
        //Draw store menu
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
     * Set the game is over when enter this room
     */
    public void isGameOver() {
        isGameOver = true;
    }

    /**
     * Getters used to get list of enemies within the scene
     * @return ArrayList<Enemies>
     */
    public ArrayList<Enemies> getEnemies(){
        return enemies;
    }
}
