package foodrecommender.system.classes;

public class Profile {
    private String profileTitle, profileValue;

    public Profile(String profileTitle, String profileValue) {
        this.profileTitle = profileTitle;
        this.profileValue = profileValue;
    }

    public String getProfileTitle() {
        return profileTitle;
    }

    public String getProfileValue(){
        return profileValue;
    }

    public void setProfileValue(String profileValue){
        this.profileValue = profileValue;
    }
}
