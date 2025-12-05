import bagel.Font;
import bagel.Window;
import bagel.util.Point;

/**
 * Helper methods to display information for the player
 */
public class UserInterface {
    /**
     * Draw statistics on the UI
     * @param health players health
     * @param coins players coins
     * @param keys players key collected
     * @param weapon Weapon level
     */
    public static void drawStats(double health, double coins,double keys,Weapon weapon) {
        int fontSize = Integer.parseInt(ShadowDungeon.getGameProps().getProperty("playerStats.fontSize"));
        //Health UI
        drawData(String.format("%s %.1f", ShadowDungeon.getMessageProps().getProperty("healthDisplay"), health), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("healthStat")));
       //Coins UI
        drawData(String.format("%s %.0f", ShadowDungeon.getMessageProps().getProperty("coinDisplay"), coins), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("coinStat")));
        //Weapons UI
        drawData(String.format("%s %d", ShadowDungeon.getMessageProps().getProperty("weaponDisplay"),
                        weapon.getWeaponStat()), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("weaponStat")));
        //Keys UI
        drawData(String.format("%s %.0f", ShadowDungeon.getMessageProps().getProperty("keyDisplay"), keys), fontSize,
                IOUtils.parseCoords(ShadowDungeon.getGameProps().getProperty("keyStat")));
    }

    /**
     * Draw message in the prep room
     */
    public static void drawStartMessages() {
        drawTextCentered("title", Integer.parseInt(ShadowDungeon.getGameProps().getProperty("title.fontSize")), Double.parseDouble(ShadowDungeon.getGameProps().getProperty("title.y")));
        drawTextCentered("moveMessage", Integer.parseInt(ShadowDungeon.getGameProps().getProperty("prompt.fontSize")), Double.parseDouble(ShadowDungeon.getGameProps().getProperty("moveMessage.y")));
      //Marines and robot character description
       Font robotDesc=new Font("res/wheaton.otf",
                Integer.parseInt(ShadowDungeon.getGameProps().getProperty("playerStats.fontSize")));
        Font marineDesc=new Font("res/wheaton.otf",
                Integer.parseInt(ShadowDungeon.getGameProps().getProperty("playerStats.fontSize")));
        //Get coordinates for marines and robot
        String coords[]=ShadowDungeon.getGameProps().getProperty("marineMessage").trim().split(",");

        robotDesc.drawString(ShadowDungeon.getMessageProps().getProperty("robotDescription"),
                               Integer.parseInt(coords[0]),Integer.parseInt(coords[1]));
        //Display message for marines and robot description
        coords= ShadowDungeon.getGameProps().getProperty("robotMessage").trim().split(",");
        marineDesc.drawString(ShadowDungeon.getMessageProps().getProperty("marineDescription"),
                            Integer.parseInt(coords[0]),Integer.parseInt(coords[1]));
    }

    /**
     * Draw end message within the end scene
     * @param win If the game has been won
     */
    public static void drawEndMessage(boolean win) {
        drawTextCentered(win ? "gameEnd.won" : "gameEnd.lost", Integer.parseInt(ShadowDungeon.getGameProps().getProperty("title.fontSize")), Double.parseDouble(ShadowDungeon.getGameProps().getProperty("title.y")));
    }

    /**
     * Centered version of drawData
     * @param textPath Text
     * @param fontSize fontSize of the text
     * @param posY y position of the text
     */
    public static void drawTextCentered(String textPath, int fontSize, double posY) {
        Font font = new Font("res/wheaton.otf", fontSize);
        String text = ShadowDungeon.getMessageProps().getProperty(textPath);
        double posX = (Window.getWidth() - font.getWidth(text)) / 2;
        font.drawString(text, posX, posY);
    }

    /**
     * Draw the text
     * @param data text
     * @param fontSize fontSize of the text
     * @param location Location of the text
     */
    public static void drawData(String data, int fontSize, Point location) {
        Font font = new Font("res/wheaton.otf", fontSize);
        font.drawString(data, location.x, location.y);
    }
}
