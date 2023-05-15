package foodrecommender.system.classes;

public class User {

    private String username;
    private String password;
    private int pregnancyCount;

    public User(String username, String password, int pregnancyCount) {
        this.username = username;
        this.password = password;
        this.pregnancyCount = pregnancyCount;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPregnancyCount() {
        return pregnancyCount;
    }

    // Add getters and setters for other fields if needed
}
