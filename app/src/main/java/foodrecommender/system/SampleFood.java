package foodrecommender.system;

public class SampleFood {
    private String shortDesc, foodGroup, kcal;

    public SampleFood(String shortDesc, String foodGroup, String kcal) {
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

    public String getKcal() {
        return kcal;
    }
}
