package foodrecommender.system.classes;

public class Preferences {

    private String preferencesTitle, preferencesValue;

    public Preferences(String preferencesTitle, String preferencesValue) {
        this.preferencesTitle = preferencesTitle;
        this.preferencesValue = preferencesValue;
    }

    public String getPreferencesTitle() {
        return preferencesTitle;
    }

    public String getPreferencesValue(){
        return preferencesValue;
    }

    public void setPreferencesValue(String preferencesValue){
        this.preferencesValue = preferencesValue;
    }
}
