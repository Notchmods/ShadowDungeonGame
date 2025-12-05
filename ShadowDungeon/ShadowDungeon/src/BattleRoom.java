import bagel.Input;
import bagel.Keys;
import bagel.util.Point;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * Room with doors that are locked until the plaer defeats all enemies
 */
public class BattleRoom extends Room {
    private Player player;
    private Door primaryDoor;
    private Door secondaryDoor;
    private KeyBulletKin keyBulletKin;
    private ArrayList<TreasureBox> treasureBoxes;
    private Table table;
    private Basket basket;
    private ArrayList<Wall> walls;
    private ArrayList<River> rivers;
    private ArrayList<Point> patrolPoints;
    private ArrayList<Point> enemyPoints;

    private ArrayList<Enemies> enemies;
    private boolean stopCurrentUpdateCall = false; // this determines whether to prematurely stop the update execution
    private boolean isComplete = false;
    private final String nextRoomName;
    private final String roomName;
    private final int PAT_A = 5;
    private final int PAT_B = 3;
    private key keys;
    private boolean justEntered, collected;
    /**
     * List of all the fireball projectiles within the scene
     */
    public static ArrayList<FireBall> allFireBalls = new ArrayList<>();


    /**
     * Constructor to initialize BattleRoom
     * @param roomName  Current room name
     * @param nextRoomName  Name of the room after it
     */
    public BattleRoom(String roomName, String nextRoomName) {
        walls = new ArrayList<>();
        rivers = new ArrayList<>();
        treasureBoxes = new ArrayList<>();
        patrolPoints = new ArrayList<>();
        this.roomName = roomName;
        this.nextRoomName = nextRoomName;
        enemies = new ArrayList<>();
        enemyPoints= new ArrayList<>();
    }

    /**
     * Initialize all the entities within the battle room
     * @param gameProperties Get game props from ShadowDungeon
     * @param player  Get player from ShadowDungeon
     */
    public void initEntities(Properties gameProperties,Player player) {
        collected=false;
        // find the configuration of game objects for this room
        for (Map.Entry<Object, Object> entry : gameProperties.entrySet()) {
            String roomSuffix = String.format(".%s", roomName);

            if (entry.getKey().toString().contains(roomSuffix)) {
                String objectType = entry.getKey().toString()
                        .substring(0, entry.getKey().toString().length() - roomSuffix.length());
                String propertyValue = entry.getValue().toString();

                // ignore if the value is 0
                if (propertyValue.equals("0")) {
                    continue;
                }

                String[] coordinates;
                for (String coords : propertyValue.split(";")) {
                    switch (objectType) {
                        case "primarydoor":
                            coordinates = propertyValue.split(",");
                            primaryDoor = new Door(IOUtils.parseCoords(propertyValue), coordinates[2], this, this);
                            break;
                        case "secondarydoor":
                            coordinates = propertyValue.split(",");
                            secondaryDoor = new Door(IOUtils.parseCoords(propertyValue), coordinates[2], this,
                                    this);
                            break;
                        case "keyBulletKin":
                            //Get the initial position of the robot
                            coordinates = propertyValue.split(";");
                            int patPoints;
                            //Determine the amount of patrol points it can have based on rooms
                            if (roomName == "A") {
                                patPoints = PAT_A;
                            } else {
                                patPoints = 3;
                            }

                            //Get the robot's patrol points
                            for (int i = 0; i < patPoints; i++) {
                                patrolPoints.add(IOUtils.parseCoords(coordinates[i]));
                            }
                            keyBulletKin = new KeyBulletKin(IOUtils.parseCoords(coordinates[0]), patrolPoints, patPoints);
                            enemies.add(keyBulletKin);
                            break;
                        case "bulletKin":
                            //Get the coordinates of the bulletKins
                            coordinates = propertyValue.split(";");
                            for(int i=0;i<coordinates.length;i++){
                                for(Point pts:enemyPoints){
                                    if(pts.equals(IOUtils.parseCoords(coords))){
                                        break;
                                    }
                                }
                              //Prevent duplicates from spawning
                                BulletKin bulletKin = new BulletKin(IOUtils.parseCoords(coords));
                                enemyPoints.add(IOUtils.parseCoords(coords));
                                enemies.add(bulletKin);
                            }
                            break;
                        case "ashenBulletKin":
                            //Get the coordinates of the bulletKins
                            coordinates = propertyValue.split(";");
                            for (int i = 0; i < coordinates.length; i++) {
                                for(Point pts:enemyPoints){
                                    if(pts.equals(IOUtils.parseCoords(coords))){
                                        break;
                                    }
                                }
                                AshenBulletKin ashenBulletKins = new AshenBulletKin(IOUtils.parseCoords(coordinates[i]));
                                enemyPoints.add(IOUtils.parseCoords(coords));
                                enemies.add(ashenBulletKins);
                            }
                            break;
                        case "wall":
                            Wall wall = new Wall(IOUtils.parseCoords(coords), this);
                            walls.add(wall);
                            break;
                        case "treasurebox":
                            TreasureBox treasureBox = new TreasureBox(IOUtils.parseCoords(coords),
                                    Double.parseDouble(coords.split(",")[2]), this);
                            treasureBoxes.add(treasureBox);
                            break;
                        case "river":
                            River river = new River(IOUtils.parseCoords(coords));
                            rivers.add(river);
                            break;
                        case "table":
                            table = new Table(IOUtils.parseCoords(coords), this);
                            break;
                        case "basket":
                            basket = new Basket(IOUtils.parseCoords(coords));
                            break;
                        default:
                    }
                }
            }
        }
        //Initialize key to prepare for keybulletKin's death
        keys = new key(keyBulletKin.getPosition(), player, this);
    }

    /**
     * Update the scene on each frame
     * @param input Get input from ShadowDungeon
     */
    public void update(Input input) {
        removeExcessFireBalls();
        removeExcessEnemy();
        // update and draw all active game objects in this room
        if (!ShadowDungeon.hasPaused) {
            primaryDoor.update(player);

            secondaryDoor.update(player);

            //Check if the player had just entered the door and update accordingly.
            doorJustEntered();

            if (stopUpdatingEarlyIfNeeded()) {
                return;
            }

            //Update enemies movement
            for (Enemies enemy : enemies) {
                if (enemy.isActive() && !enemy.isRewardGiven()) {
                    enemy.update(player);
                }
            }

            //Update Fireball  shot


            //Update items within the scene
           updateItems(input);

            //Update player once it has been activated
            if (player != null) {
                player.update(input);
            }

            //Check for level completion
            if (noMoreEnemies() && !isComplete()) {
                setComplete(true);
                unlockAllDoors();
            }

        } else {
            //Else if it's paused the you're allowed to
           purchaseItems(input);
        }
        //Draw enemies
        Draw();
    }

    //Remove additonal enemies from the scene
    private void removeExcessEnemy() {
        ArrayList<Enemies> toRemove = new ArrayList<>();
        for (Enemies enemy : enemies) {
            if (enemy.isDead()) {
                toRemove.add(enemy);
                continue;
            }
        }
        enemies.removeAll(toRemove);
    }

    private void updateItems(Input input) {
        //Update walls
        for (Wall wall : walls) {
            wall.update(player);
        }

        //Update rivers
        for (River river : rivers) {
            river.update(player);
        }

        //Draw treasurebox
        for (TreasureBox treasureBox : treasureBoxes) {
            if (treasureBox.isActive()) {
                treasureBox.update(input, player);
            }
        }

        if (!keyBulletKin.isActive() && !justEntered&&!collected) {
            keys.setActive(true);
            keys.update(keyBulletKin.getPosition());
        }

        //Update tables
        if(table.getActive()){
            table.update(player);
        }

        //Update basket
        if(basket.getBasketActive()){
            basket.update(player);
        }

        //Update fireBall shot within the scene
        //Continuously updating the fireball in the scene
        for(FireBall fireballsShoot:allFireBalls){
            fireballsShoot.update();
            //Collided with player
            if(fireballsShoot.hasCollidedWithPlayer(player)){
                fireballsShoot.setOnScene(false);
                player.receiveDamage(30);
            }

        }
    }

    private void purchaseItems(Input input){ //Buy health
        if (input.wasReleased(Keys.E)) {
            player.BuyHealth();
        }

        //Upgrade weapons
        if (input.wasReleased(Keys.L)) {
            player.BuyWeapons();
        }

    }

    private void doorJustEntered(){
        if (primaryDoor.getJustEntered() || secondaryDoor.getJustEntered()) {
            justEntered = true;
        } else {
            justEntered = false;
        }
    }

    private void Draw() {
        //Draw doors
        primaryDoor.draw();
        secondaryDoor.draw();

        if (table.getActive()) {
            table.Draw();
        }

        //Draw basket
        if (basket.getBasketActive()) {
            basket.Draw();
        }

        //Draw all rivers
        for (River river : rivers) {
            river.draw();
        }

        //Draw treasure boxes
        for (TreasureBox treasureBox : treasureBoxes) {
            if (treasureBox.isActive()) {
                treasureBox.draw();
            }
        }


        //Draw all enemies
        for (Enemies enemy : enemies) {
            if (enemy.isActive()) {
                enemy.draw();
            }
        }

        //Draw all walls
        for (Wall wall : walls) {
            wall.draw();
        }


        //Draw projectiles of the enemies
        for(Projectile fireBalls:allFireBalls){
            fireBalls.Draw();
        }

        //Draw player projectile
        for(Projectile projectile:player.getAmmunition()){
            if(projectile.getOnScene()){
                projectile.Draw();
            }
        }

        //If keybulletkin is dead then it drops the key
        if(keys!=null&&keys.isActive()){
            keys.Draw(keyBulletKin.getPosition());
        }

        player.draw();

        //Draw store menu
        if (ShadowDungeon.hasPaused) {
            ShadowDungeon.Paused();
        }
    }

     //Clean the ArrayList of fireballs after it has been deactivated
    private void removeExcessFireBalls(){
        //Gather a list of fireBall to remove
        ArrayList<FireBall> toRemove = new ArrayList<>();
        for(FireBall ammo:allFireBalls){
            if(!ammo.getOnScene()){
                toRemove.add(ammo);
            }
        }
        //Remove all of them at once from the main list
        allFireBalls.removeAll(toRemove);
    }

    private boolean stopUpdatingEarlyIfNeeded() {
        if (stopCurrentUpdateCall) {
            player = null;
            stopCurrentUpdateCall = false;
            return true;
        }
        return false;
    }

    private void unlockAllDoors() {
        primaryDoor.unlock(false);
        secondaryDoor.unlock(false);
    }

    /**
     *
     */
    public void stopCurrentUpdateCall() {
        stopCurrentUpdateCall = true;
    }

    /**
     * Get the player from ShadowDungeon
     * @param player player from ShadowDungeon
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Determines the next room the player goes to when entering the door
     * @param roomName
     * @return
     */
    public Door findDoorByDestination(String roomName) {
        if (primaryDoor.toRoomName.equals(roomName)) {
            return primaryDoor;
        } else {
            return secondaryDoor;
        }
    }

    /**
     * Determines if every enemy within the scene has been killed or not
     * @return boolean
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Setters used to set if the level has been completed
     * @param complete Determines if the level is completed or not
     */
    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Activate every enemy within the scene at the start
     */
    public void activateEnemies() {
        if(isComplete)return;
        //Activate enemies
        for (Enemies enemy : enemies) {
            if(!enemy.isRewardGiven() && !enemy.isDead()){
                enemy.setActive(true);
            }
        }

    }


    /**
     * Getters used to check if the scene has anymore enemies
     * @return boolean
     */
    public boolean noMoreEnemies() {
        //return keyBulletKin.isDead();
        //Iterate through the enemy list to see if there's any left
        for (Enemies enemy : enemies) {
            if (!enemy.isDead()) {
                return false;
            }
        }
        //If there's none then the level has won
        return true;
    }

    /**
     * Getters used to get list of enemies within the scene
     * @return ArrayList<Enemies>
     */
    public ArrayList<Enemies> getEnemies() {
        return enemies;
    }

    /**
     * Setters used to set if the key has been collected
     * @param status Whether the key has been collected or not
     */
    public void setCollected(boolean status){
        collected= status;
    }

}
