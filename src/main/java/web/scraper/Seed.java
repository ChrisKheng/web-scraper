package web.scraper;

public class Seed {
    private String sourceUrl;
    private String newUrl;

    public Seed (String s, String n) {
        this.sourceUrl = s;
        this.newUrl = n;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public String getNewUrl() {
        return newUrl;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", sourceUrl, newUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Seed)) {
            return false;
        }

        Seed s = (Seed) o;

        return sourceUrl.equals(s.sourceUrl) && newUrl.equals(s.newUrl);
    }
}