import bagel.Image;

/**
Original character
 */
public class Original extends character {
    private String name;
    private final Image RIGHT_IMAGE;
    private final Image LEFT_IMAGE;

    /**
     * Initialize Original character
     */
    public Original(){
        this.RIGHT_IMAGE=new Image("res/player_right.png");
        this.LEFT_IMAGE=new Image("res/player_left.png");
        applySkill();
    }

    /**
     * Apply skills of the player
     */
    @Override
    public void applySkill(){
        this.name="original";
    }

    /**
     * Getters to get skills of the player
     * @return  String
     */
    public String getSkills(){
        return name;
    }

    /**
     * Getters to get current Image
     * @return Image
     */
    public Image getCurrentImage(){
        return LEFT_IMAGE;
    }

    /**
     * Getters to get left image of the player
     * @return  Image
     */
    public Image getLeftImage(){
        return LEFT_IMAGE;
    }

    /**
     * Getters to get right image of the player
     * @return  Image
     */
    public Image getRightImage(){
        return RIGHT_IMAGE;
    }
}
