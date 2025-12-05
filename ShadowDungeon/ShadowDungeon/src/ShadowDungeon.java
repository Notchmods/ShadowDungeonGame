import bagel.*;
import bagel.util.Point;

import java.security.Key;
import java.util.Properties;

/**
 * Main game class that manages initialising the rooms and moving the player between rooms
 */
public class  ShadowDungeon extends AbstractGame {
    /**
     * Game properties from app.properties file
     */
    public static Properties gameProps;
    /**
     * Message properties from message.properties file
     */
    public static Properties messageProps;
    /**
     * Screen width
     */
    public static double screenWidth;
    /**
     * Screen height
     */
    public static double screenHeight;

    /**
     * Current room name
     */
    public static String currRoomName;
    private static PrepRoom prepRoom;
    private static BattleRoom battleRoomA;
    private static BattleRoom battleRoomB;
    private static EndRoom endRoom;
    private static Player player;
    private final Image background;
    private static Image storeMenu;

    /**
     * Prep Room constants
     */
    public static final String PREP_ROOM_NAME = "prep";
    /**
     * BattleRoom A constants
     */
    public static final String BATTLE_ROOM_A_NAME = "A";
    /**
     * BattleRoom B constants
     */
    public static final String BATTLE_ROOM_B_NAME = "B";
    /**
     * End room constants
     */
    public static final String END_ROOM_NAME = "end";
    /**
     * Determines if the game is paused or not
     */
    public static boolean hasPaused=false;

    /**Main constructor that runs the game
     * @param gameProps Gets the game properties from app.properties text files
     * @param  messageProps Gets message from message.properties text file*/
    public ShadowDungeon(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                "Shadow Dungeon");

        ShadowDungeon.gameProps = gameProps;
        ShadowDungeon.messageProps = messageProps;
        screenWidth = Integer.parseInt(gameProps.getProperty("window.width"));
        screenHeight = Integer.parseInt(gameProps.getProperty("window.height"));
        this.background = new Image("res/background.png");
        this.storeMenu= new Image("res/store.png");

        resetGameState(gameProps);
    }

    /**
     * Reset the entire game
     * @param gameProps Properties
     */
    //Reset the state of the game
    public static void resetGameState(Properties gameProps) {
        ShadowDungeon.player = new Player(IOUtils.parseCoords(gameProps.getProperty("player.start")));
        hasPaused=false;

        //Initialize the rooms again
        prepRoom = new PrepRoom();
        battleRoomA = new BattleRoom(BATTLE_ROOM_A_NAME, BATTLE_ROOM_B_NAME);
        battleRoomB = new BattleRoom(BATTLE_ROOM_B_NAME, END_ROOM_NAME);
        endRoom = new EndRoom();

        //Initialize all the entities again within each room
        prepRoom.initEntities(gameProps,messageProps);
        battleRoomA.initEntities(gameProps,player);
        battleRoomB.initEntities(gameProps,player);
        endRoom.initEntities(gameProps);

        currRoomName = PREP_ROOM_NAME;
        prepRoom.setPlayer(player);
    }

    /**
     * Render the relevant screen based on the keyboard as given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        //Close the game
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
        }

        //Restart if pressed P
        if(hasPaused &&input.wasPressed(Keys.P)){
            ShadowDungeon.resetGameState(ShadowDungeon.getGameProps());
        }


        //Controls when to pause or unpause the game
        if(input.wasPressed(Keys.SPACE)&&!hasPaused){
            hasPaused=true;
        }else if(input.wasPressed(Keys.SPACE)&&hasPaused){
            hasPaused=false;
        }

        background.draw((double) Window.getWidth() / 2, (double) Window.getHeight() / 2);

        //If the game isn't paused then keep running it
        switch (currRoomName) {
            case PREP_ROOM_NAME:
                prepRoom.update(input);
                return;
            case BATTLE_ROOM_A_NAME:
                battleRoomA.update(input);
                return;
            case BATTLE_ROOM_B_NAME:
                battleRoomB.update(input);
                return;
            default:
                endRoom.update(input);
                break;
        }
    }

    /**
     * Change room that the player touches the door and all the criteria are fulfilled
     * @param roomName Room Name in Strings
     */
    public static void changeRoom(String roomName) {
        Door nextDoor;
        switch (roomName) {
            case PREP_ROOM_NAME:
                nextDoor = prepRoom.findDoorByDestination();
                LoadPrepRoom(nextDoor);
                return;
            case BATTLE_ROOM_A_NAME:
                nextDoor = battleRoomA.findDoorByDestination(currRoomName);

                // assume that Battle Room A can only be entered through Prep Room or Battle Room B
               LoadBattleRoomA(nextDoor);

                return;
            case BATTLE_ROOM_B_NAME:
                nextDoor = battleRoomB.findDoorByDestination(currRoomName);

                // assume that Battle Room B can only be entered through Battle Room A or End Room
               LoadBattleRoomB(nextDoor);

                return;
            default:
                nextDoor = endRoom.findDoorByDestination();

                // assume that end room can only be entered through Battle Room B
                LoadEndRoom(nextDoor);
        }
    }

    /**
     * Changing rooms
     */
    public static void changeToGameOverRoom() {
        switch (currRoomName) {
            case PREP_ROOM_NAME:
                prepRoom.stopCurrentUpdateCall();
            case BATTLE_ROOM_A_NAME:
                battleRoomA.stopCurrentUpdateCall();
            case BATTLE_ROOM_B_NAME:
                battleRoomB.stopCurrentUpdateCall();
            default:
        }

        endRoom.isGameOver();
        currRoomName = END_ROOM_NAME;

        Point startPos = IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("player.start"));
        player.move(startPos.x, startPos.y);
        endRoom.setPlayer(player);
    }

    /**
     * Used to draw the store menu when paused.
     */
    public static void Paused(){
        String[] coords=gameProps.getProperty("store").trim().split(",");
        storeMenu.draw(Double.parseDouble(coords[0]),Double.parseDouble(coords[1]));
    }

    /**
     * Load prep room
     * @param nextDoor Door that leads to the next room
     */
    public static void LoadPrepRoom(Door nextDoor){

        // assume that aprep room can only be entered through Battle Room A
        if (currRoomName.equals(BATTLE_ROOM_A_NAME)) {
            battleRoomA.stopCurrentUpdateCall();
        }
        currRoomName = PREP_ROOM_NAME;

        // move the player to the center of the next room's door
        nextDoor.unlock(true);
        player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
        prepRoom.setPlayer(player);
    }


    /**
     * Load Battle Room A
     * @param nextDoor Door that leads to next room
     */
    public static void LoadBattleRoomA(Door nextDoor) {
        if (currRoomName.equals(BATTLE_ROOM_B_NAME)) {
            battleRoomB.stopCurrentUpdateCall();
        } else if (currRoomName.equals(PREP_ROOM_NAME)) {
            prepRoom.stopCurrentUpdateCall();
        }
        currRoomName = BATTLE_ROOM_A_NAME;

        // prepare the door to be able to activate the Battle Room
        if (!battleRoomA.isComplete()) {
            nextDoor.setShouldLockAgain();
        }

        // move the player to the center of the next room's door
        nextDoor.unlock(true);
        player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
        battleRoomA.setPlayer(player);
    }

    /**
     * Load Battle Room B
     * @param nextDoor Door that leads to next  room
     */
    public static void LoadBattleRoomB(Door nextDoor) {
        if (currRoomName.equals(BATTLE_ROOM_A_NAME)) {
            battleRoomA.stopCurrentUpdateCall();
        } else if (currRoomName.equals(END_ROOM_NAME)) {
            endRoom.stopCurrentUpdateCall();
        }
        currRoomName = BATTLE_ROOM_B_NAME;

        // prepare the door to be able to activate the Battle Room
        if (!battleRoomB.isComplete()) {
            nextDoor.setShouldLockAgain();
        }

        // move the player to the center of the next room's door
        nextDoor.unlock(true);
        player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
        battleRoomB.setPlayer(player);
    }

    /**
     * Load the end room
     * @param nextDoor Door that leads to thje next room
     */
    public static void LoadEndRoom(Door nextDoor) {
        if (currRoomName.equals(BATTLE_ROOM_B_NAME)) {
            battleRoomB.stopCurrentUpdateCall();
        }
        currRoomName = END_ROOM_NAME;

        // move the player to the center of the next room's door
        nextDoor.unlock(true);
        player.move(nextDoor.getPosition().x, nextDoor.getPosition().y);
        endRoom.setPlayer(player);
    }


    /**
     * Getters to get app properties from app.properties
     * @return Properties
     */
    public static Properties getGameProps() {
        return gameProps;
    }

    /**
     * Getters to get message properties from message.properties
     * @return Properties
     */
    public static Properties getMessageProps() {
        return messageProps;
    }

    /**
     * Main function
     * @param args amount of string arguments in the console
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDungeon game = new ShadowDungeon(gameProps, messageProps);
        game.run();
    }
}
