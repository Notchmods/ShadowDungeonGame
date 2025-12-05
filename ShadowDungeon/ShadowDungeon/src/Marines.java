import bagel.Image;

public class Marines extends character{
    private String name;
    private final Image RIGHT_IMAGE;
    private final Image LEFT_IMAGE;
    private Image spriteImage;

    /**
     * Initialize marine character
     */
    public Marines(){
        this.RIGHT_IMAGE=new Image("res/marine_right.png");
        this.LEFT_IMAGE=new Image("res/marine_left.png");
        this.spriteImage= new Image("res/marine.png");
        applySkill();
    }

    /**
     * Apply skills of the player
     */
    @Override
    public void applySkill(){
        this.name="marines";
    }

    /**
     * Getters to get skills of the player
     * @return  String
     */
    public String getSkills(){
        return name;
    }

    /**
     * Getters to get right image of the player
     * @return  Image
     */
    public Image getRightImage(){
        return RIGHT_IMAGE;
    }

    /**
     * Getters to get left image of the player
     * @return  Image
     */
    public Image getLeftImage(){
        return LEFT_IMAGE;
    }

    /**
     * Getters to get current Image
     * @return Image
     */
    public Image getCurrentImage(){
        return spriteImage;
    }
}
