package wiki.Models;

public class User {
    //unsername e password
    private String username;
    private String password;

    //costruttore

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
