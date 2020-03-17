package web.scraper;

public class CustomThread extends Thread {
    private String name;

    public void setThreadName(String n) {
        this.name = n;
    }

    public String getFormattedMessage(String m) {
        return String.format("%s %s", this.name, m);
    }
}