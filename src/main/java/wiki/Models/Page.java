package wiki.Models;

public class Page {
    //attributi
    private String titolo;
    private String contenuto;

    //costruttore
    public Page(String titolo, String contenuto) {
        this.titolo = titolo;
        this.contenuto = contenuto;
    }

    //metodi
    public String getTitolo() {
        return titolo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }
}
