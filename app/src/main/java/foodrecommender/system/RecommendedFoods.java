package foodrecommender.system;

public class RecommendedFoods {
    private String shortDesc, foodGroup;

    private boolean isPart2, isChecked;

    private int energKcal;

    public RecommendedFoods(String shortDesc, String foodGroup, int energKcal, boolean isPart2) {
        this.shortDesc = shortDesc;
        this.foodGroup = foodGroup;
        this.energKcal = energKcal;
        this.isPart2 = isPart2;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getFoodGroup(){
        return foodGroup;
    }

    public int getEnergKcal(){
        return energKcal;
    }

    public boolean isPart2(){
        return isPart2;
    }

}
