package foodrecommender.system;

public class SampleFood {
    private String shortDesc, foodGroup;
    private int kcal;

    public SampleFood(String shortDesc, String foodGroup, int kcal) {
        this.shortDesc = shortDesc;
        this.foodGroup = foodGroup;
        this.kcal = kcal;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public int getKcal() {
        return kcal;
    }
}
