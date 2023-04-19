package foodrecommender.system;

public class SearchFood {
    private String sshortDesc, foodGroup;
    private int skcal;

    public SearchFood(String sshortDesc, String foodGroup, int skcal) {
        this.sshortDesc = sshortDesc;
        this.foodGroup = foodGroup;
        this.skcal = skcal;
    }

    public String getSShortDesc() {
        return sshortDesc;
    }

    public String getFoodGroup() {
        return foodGroup;
    }

    public int getSKcal() {
        return skcal;
    }
}
