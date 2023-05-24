package foodrecommender.system.classes;

public class History {

    private String shortDesc, foodGroup, kcal;

    public History(String shortDesc, String foodGroup, String kcal) {
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
