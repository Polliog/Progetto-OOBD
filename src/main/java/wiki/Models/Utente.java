package wiki.Models;

public class Utente {
    //unsername e password
    private String username;
    private String password;

    //costruttore

    public Utente(String username, String password) {
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
