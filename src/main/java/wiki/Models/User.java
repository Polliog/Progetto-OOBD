package wiki.Models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class User {
    //attributes
    private final String username;
    private final boolean isAdmin;
    private final Timestamp creationDate;


    public User(String username, boolean isAdmin, Timestamp creationDate) {
        this.username = username.toLowerCase();
        this.isAdmin = isAdmin;
        this.creationDate = creationDate;
    }


    // Getters
    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
