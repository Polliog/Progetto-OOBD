package wiki.Models;

public class User {
    //attributes
    private String username;
    private String password;

    //constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //metodi

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
